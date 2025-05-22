import bagel.*;
import bagel.Input;

import java.util.ArrayList;
import java.util.Properties;

/**
 * Represents the main gameplay screen where the player controls Mario.
 * This abstract class manages game objects, updates their states, and handles game logic.
 * Being extended by Level1 (basic) and Level2 (with extra implementation on additional game logic)
 */
public abstract class GamePlayScreen {
    private final Properties GAME_PROPS;

    // Game objectss for both levels and blasters for level2
    /** Mario, player control character */
    public Mario mario;
    private Barrel[] barrels;   // Array of barrels in the game
    private Ladder[] ladders;   // Array of ladders in the game
    private Hammer[] hammers;// Array of hammers object that Mario can collect
    /** Donkey Kong, the objective of the game */
    public Donkey donkey;
    private Image background;   // Background image for the game
    /** Array of platforms in the game */
    public Platform[] platforms;
    private Blaster[] blasters = new Blaster[0]; // Array of blasters object that Mario can collect

    // Frame tracking
    private int currFrame = 0;  // Tracks the number of frames elapsed
    private int donkeyHP = 5; // Store real-time value for donkey health

    // Game parameters
    private final int MAX_FRAMES;  // Maximum number of frames before game ends

    // Display text variables for game status
    private final Font STATUS_FONT; // font for status info
    private final int SCORE_X; // x-coord for score info
    private final int SCORE_Y; // y-coord for score info
    private final int DKH_X; // x-coord for donkey health info
    private final int DKH_Y; // y-coord for donkey health info
    private static final int TIME_DISPLAY_DIFF_Y = 30; // difference between y-coord of time and score

    // Display message for game status
    private static final String SCORE_MESSAGE = "SCORE ";  // text before display score
    private static final String TIME_MESSAGE = "Time Left "; // text before display remaining time
    private static final String DKH_MESSAGE = "DONKEY HEALTH "; // text before display donkey health value

    // Score gain for game action
    private static final int BARREL_SCORE = 100; // score gain for destroy a barrel
    private static final int BARREL_CROSS_SCORE = 30; // score gain for jump across a barrel

    /** Player's startedScore at the start of level */
    public int startedScore;
    /** Game over flag */
    public boolean isGameOver = false;
    private int currLevel; // indicate current game level

    private final int LEVEL2 = 2; // at level 2 of game

    /**
     * Returns the player's current startedScore.
     *
     * @return The player's startedScore.
     */
    public int getStartedScore() {
        return startedScore;
    }

    /**
     * Calculates the remaining time left in seconds.
     *
     * @return The number of seconds remaining before the game ends.
     */
    public int getSecondsLeft() {
        return (MAX_FRAMES - currFrame) / 60;
    }


    /**
     * Constructs the gameplay screen, loading resources and initializing game objects.
     * @param gameProps property file with game setting
     * @param currLevel current level number
     * @param startedScore initial score of game
     */
    public GamePlayScreen(Properties gameProps, int currLevel, int startedScore) {
        this.GAME_PROPS = gameProps;

        // Load game parameters
        this.MAX_FRAMES = Integer.parseInt(gameProps.getProperty("gamePlay.maxFrames"));
        this.STATUS_FONT = new Font(
                gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("gamePlay.score.fontSize"))
        );
        this.SCORE_X = Integer.parseInt(gameProps.getProperty("gamePlay.score.x"));
        this.SCORE_Y = Integer.parseInt(gameProps.getProperty("gamePlay.score.y"));

        this.DKH_X = Integer.parseInt(gameProps.getProperty("gamePlay.donkeyhealth.coords").split(",")[0]);
        this.DKH_Y = Integer.parseInt(gameProps.getProperty("gamePlay.donkeyhealth.coords").split(",")[1]);

        this.background = new Image("res/background.png");
        this.currLevel = currLevel;
        this.startedScore = startedScore;
        // Initialize game objects
        initializeGameObjects();
    }

    /**
     * use to read in properties of game entity with format
     * (gameEntityName).level(cuurLevel).count = number of entity in that level
     * (gameEntityName).level(cuurLevel).(i: 1...count)
     * @param <T> game entity
     */
    public interface EntityFactory<T extends GameEntity> {
        T create(String[] entityStr);
    }
    <T extends GameEntity> ArrayList<T> loadEntities(
            Properties props, String prefix,
            int currLevel, EntityFactory<T> factory
    ) {
        ArrayList<T> entities = new ArrayList<>();
        // set base key as composition of entity prefix and level
        String baseKey = prefix + ".level" + currLevel;
        // get number of the specific entity
        int count = Integer.parseInt(props.getProperty(baseKey + ".count"));
        // read data for ith object for the entity
        for (int i = 1; i <= count; i++) {
            String data = props.getProperty(baseKey + "." + i);
            if (data == null) continue;
            String[] entityStr = data.split(";");
            try {
                // create entity by factory
                T entity = factory.create(entityStr);
                entities.add(entity);
            } catch (Exception e) {
                // error message for unable to create
                System.out.println("Error creating " + prefix + "." + i + ": " + e.getMessage());
            }
        }
        return entities;
    }

    /**
     * Initializes game objects such as Mario, Donkey Kong, barrels, ladders, platforms, and the hammer.
     */
    private void initializeGameObjects() {
        // Create Mario
        String[] marioCoord = GAME_PROPS.getProperty("mario.level" + currLevel).split(",");
        double marioX = Double.parseDouble(marioCoord[0]);
        double marioY = Double.parseDouble(marioCoord[1]);
        this.mario = new Mario(marioX, marioY);

        // Create Donkey Kong
        String[] DonkeyCoord = GAME_PROPS.getProperty("donkey.level" + currLevel).split(",");
        double donkeyX = Double.parseDouble(DonkeyCoord[0]);
        double donkeyY = Double.parseDouble(DonkeyCoord[1]);
        this.donkey = new Donkey(donkeyX, donkeyY);

        // Create the Platforms array
        String platformData = GAME_PROPS.getProperty("platforms.level" + currLevel);
        if (platformData != null && !platformData.isEmpty()) {
            String[] platformEntries = platformData.split(";");
            this.platforms = new Platform[platformEntries.length];
            int pIndex = 0;
            for (String entry : platformEntries) {
                String[] coords = entry.trim().split(",");
                if (coords.length < 2) {
                    System.out.println("Warning: Invalid platform entry -> " + entry);
                    continue; // Skip invalid entries with wrong coordinate format
                }
                double x = Double.parseDouble(coords[0]);
                double y = Double.parseDouble(coords[1]);
                if (pIndex < platformEntries.length) {
                    platforms[pIndex] = new Platform(x, y);
                    pIndex++;
                }
            }
        } else {
            this.platforms = new Platform[0]; // No platform data
        }

        // Load barrels
        this.barrels = loadEntities(GAME_PROPS, "barrel", currLevel, new EntityFactory<Barrel>() {
            @Override
            public Barrel create(String[] parts) {
                String[] coords = parts[0].split(",");
                return new Barrel(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
            }
        }).toArray(new Barrel[0]);

        // Load ladders
        this.ladders = loadEntities(GAME_PROPS, "ladder", currLevel, new EntityFactory<Ladder>() {
            @Override
            public Ladder create(String[] parts) {
                String[] coords = parts[0].split(",");
                return new Ladder(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
            }
        }).toArray(new Ladder[0]);

        // Load hammers
        this.hammers = loadEntities(GAME_PROPS, "hammer", currLevel, new EntityFactory<Hammer>() {
            @Override
            public Hammer create(String[] parts) {
                String[] coords = parts[0].split(",");
                return new Hammer(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
            }
        }).toArray(new Hammer[0]);
    }

    /**
     * get current donkey health value
     * @return current donkey health value
     */
    public int getDonkeyHP() {
        return donkeyHP;
    }

    /**
     * set current donkey health value
     * @param donkeyHP current donkey health value
     */
    public void setDonkeyHP(int donkeyHP) {
        this.donkeyHP = donkeyHP;
    }

    /**
     * Updates game state each frame.
     *
     * @param input The current player input.
     * @return {@code true} if the game ends, {@code false} otherwise.
     */
    public boolean update(Input input) {
        currFrame++;
        // Draw background
        background.drawFromTopLeft(0, 0);

        // Draw and update platforms
        for (Platform platform : platforms) {
            if (platform != null) {
                platform.draw();
            }
        }
        // Update ladders
        for (Ladder ladder : ladders) {
            if (ladder != null) {
                ladder.update(platforms);
            }
        }
        // Update barrels
        for (Barrel barrel : barrels) {
            if (barrel == null) continue;
            // gain mark for jump over barrel
            if (mario.jumpOver(barrel)) {
                startedScore += BARREL_CROSS_SCORE;
            }
            // handle collision between mario and barrel
            if (!barrel.isDestroyed() && mario.isCollide(barrel)) {
                if (!mario.holdHammer()) {
                    isGameOver = true;
                } else {
                    barrel.destroy();
                    startedScore += BARREL_SCORE;
                }
            }
            barrel.update(platforms);
        }
        // Check game time and donkey status
        if (checkingGameTime()) {
            isGameOver = true;
        }
        donkey.update(platforms);
        // draw hammers
        for (Hammer hammer: hammers){
            hammer.draw();
        }
        // draw and update donkey
        donkey.draw();
        donkey.update(platforms);
        // update blasters for level2 only
        if (currLevel == LEVEL2 && this instanceof Level2){
            blasters = ((Level2) this).getBlasters();
        }
        else{
            blasters = null;
        }
        // Update Mario
        mario.update(input, ladders, platforms, hammers, blasters);
        // Check if Mario reaches Donkey
        if (mario.isCollide(donkey) && !mario.holdHammer()) {
            isGameOver = true;
        }
        // Display shared status info
        displayInfo();
        // Update extra entity for level2 only
        updateExtra(input);
        // Return game state
        return isGameOver || isLevelCompleted();
    }

    /**
     * Displays the player's startedScore & time left on the screen.
     */
    public void displayInfo() {
        STATUS_FONT.drawString(SCORE_MESSAGE + startedScore, SCORE_X, SCORE_Y);
        // Time left in seconds
        int secondsLeft = (MAX_FRAMES - currFrame) / 60;
        int TIME_X = SCORE_X;
        int TIME_Y = SCORE_Y + TIME_DISPLAY_DIFF_Y;
        STATUS_FONT.drawString(TIME_MESSAGE + secondsLeft, TIME_X, TIME_Y);
        STATUS_FONT.drawString(DKH_MESSAGE + donkeyHP, DKH_X, DKH_Y);
        // display bullet info for level2
        displayBullet(STATUS_FONT, DKH_X, DKH_Y);
    }

    /**
     * Checks whether the level is completed by determining if Mario has reached Donkey Kong
     * while holding a hammer. This serves as the game's winning condition.
     *
     * @return {@code true} if Mario reaches Donkey Kong while holding a hammer,
     *         indicating the level is completed; {@code false} otherwise.
     */
    public boolean isLevelCompleted() {
        return (mario.isCollide(donkey) && mario.holdHammer()) || getDonkeyHP() == 0;
    }

    /**
     * Checks if the game has reached its time limit by comparing the current frame count
     * against the maximum allowed frames. If the limit is reached, the game may trigger
     * a timeout condition.
     *
     * @return {@code true} if the current frame count has reached or exceeded
     *         the maximum allowed frames, indicating the time limit has been reached;
     *         {@code false} otherwise.
     */
    public boolean checkingGameTime() {
        return currFrame >= MAX_FRAMES;
    }

    /**
     * abstract method to update extra game elements for level 2
     * @param input input from keyboard
     */
    public abstract void updateExtra(Input input);

    /**
     * abstract method to display bullet info for level 2
     * @param STATUS_FONT font for display text
     * @param DKH_X  x-coordinate to display the bullet info
     * @param DKH_Y  y-coordinate to display the bullet info
     */
    public abstract void displayBullet(Font STATUS_FONT, int DKH_X, int DKH_Y);
}
