import bagel.*;
import bagel.Input;

import java.util.ArrayList;
import java.util.Properties;

/**
 * Represents the main gameplay screen where the player controls Mario.
 * This class manages game objects, updates their states, and handles game logic.
 */
public abstract class GamePlayScreen {
    private final Properties GAME_PROPS;

    // Game objectss
    protected Mario mario;
    private Barrel[] barrels;   // Array of barrels in the game
    private Ladder[] ladders;   // Array of ladders in the game
    private Hammer[] hammers;      // The hammer object that Mario can collect
    private Blaster[] blasters = new Blaster[0];
    protected Donkey donkey;      // Donkey Kong, the objective of the game
    private Image background;   // Background image for the game
    protected Platform[] platforms; // Array of platforms in the game

    // Frame tracking
    private int currFrame = 0;  // Tracks the number of frames elapsed

    private int donkeyHP = 5;

    // Game parameters
    private final int MAX_FRAMES;  // Maximum number of frames before game ends

    // Display text variables
    private final Font STATUS_FONT;
    private final int SCORE_X;
    private final int SCORE_Y;
    private final int DKH_X;
    private final int DKH_Y;

    private static final String SCORE_MESSAGE = "SCORE ";
    private static final String TIME_MESSAGE = "Time Left ";
    private static final String DKH_MESSAGE = "DONKEY HEALTH ";

    private static final int BARREL_SCORE = 100;
    private static final int TIME_DISPLAY_DIFF_Y = 30;
    private static final int BARREL_CROSS_SCORE = 30;
    protected int startedScore;  // Player's startedScore for jumping over barrels
    protected boolean isGameOver = false; // Game over flag

    private int currLevel;

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
     *
     * @param gameProps  Properties file containing game settings.
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



    public interface EntityFactory<T extends GameEntity> {
        T create(String[] entityStr);
    }

    <T extends GameEntity> ArrayList<T> loadEntities(
            Properties props, String prefix,
            int currLevel, EntityFactory<T> factory
    ) {
        ArrayList<T> entities = new ArrayList<>();
        String baseKey = prefix + ".level" + currLevel;
        int count = Integer.parseInt(props.getProperty(baseKey + ".count"));

        for (int i = 1; i <= count; i++) {
            String data = props.getProperty(baseKey + "." + i);
            if (data == null) continue;
            String[] entityStr = data.split(";");
            try {
                T entity = factory.create(entityStr);
                entities.add(entity);
            } catch (Exception e) {
                System.out.println("Error creating " + prefix + "." + i + ": " + e.getMessage());
            }
        }
        return entities;
    }

    /**
     * Initializes game objects such as Mario, Donkey Kong, barrels, ladders, platforms, and the hammer.
     */
    private void initializeGameObjects() {
        // 1) Create Mario
        String[] marioCoord = GAME_PROPS.getProperty("mario.level" + currLevel).split(",");
        double marioX = Double.parseDouble(marioCoord[0]);
        double marioY = Double.parseDouble(marioCoord[1]);
        this.mario = new Mario(marioX, marioY);

        // 2) Create Donkey Kong
        String[] DonkeyCoord = GAME_PROPS.getProperty("donkey.level" + currLevel).split(",");
        double donkeyX = Double.parseDouble(DonkeyCoord[0]);
        double donkeyY = Double.parseDouble(DonkeyCoord[1]);
        this.donkey = new Donkey(donkeyX, donkeyY);

        // 5) Create the Platforms array
        String platformData = GAME_PROPS.getProperty("platforms.level" + currLevel);
        if (platformData != null && !platformData.isEmpty()) {
            String[] platformEntries = platformData.split(";");
            this.platforms = new Platform[platformEntries.length];
            int pIndex = 0;
            for (String entry : platformEntries) {
                String[] coords = entry.trim().split(",");
                if (coords.length < 2) {
                    System.out.println("Warning: Invalid platform entry -> " + entry);
                    continue; // Skip invalid entries
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

        // 1. Load barrels
        this.barrels = loadEntities(GAME_PROPS, "barrel", currLevel, new EntityFactory<Barrel>() {
            @Override
            public Barrel create(String[] parts) {
                String[] coords = parts[0].split(",");
                return new Barrel(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
            }
        }).toArray(new Barrel[0]);

        // 2. Load ladders
        this.ladders = loadEntities(GAME_PROPS, "ladder", currLevel, new EntityFactory<Ladder>() {
            @Override
            public Ladder create(String[] parts) {
                String[] coords = parts[0].split(",");
                return new Ladder(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
            }
        }).toArray(new Ladder[0]);

        // 3. Load hammers
        this.hammers = loadEntities(GAME_PROPS, "hammer", currLevel, new EntityFactory<Hammer>() {
            @Override
            public Hammer create(String[] parts) {
                String[] coords = parts[0].split(",");
                return new Hammer(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
            }
        }).toArray(new Hammer[0]);

    }

    public int getDonkeyHP() {
        return donkeyHP;
    }

    public void setDonkeyHP(int donkeyHP) {
        this.donkeyHP = donkeyHP;
    }

    public int getCurrLevel() {
        return currLevel;
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

        // 1) Draw and update platforms
        for (Platform platform : platforms) {
            if (platform != null) {
                platform.draw();
            }
        }

        // 2) Update ladders
        for (Ladder ladder : ladders) {
            if (ladder != null) {
                ladder.update(platforms);
            }
        }

        // 3) Update barrels
        for (Barrel barrel : barrels) {
            if (barrel == null) continue;
            if (mario.jumpOver(barrel)) {
                startedScore += BARREL_CROSS_SCORE;
            }
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

        // 4) Check game time and donkey status
        if (checkingGameTime()) {
            isGameOver = true;
        }
        donkey.update(platforms);

        // 5) Draw hammer and donkey
        for (Hammer hammer: hammers){
            hammer.draw();
        }
        donkey.draw();

        if (currLevel == 2 && this instanceof Level2){
            blasters = ((Level2) this).getBlasters();
        }
        else{
            blasters = null;
        }
        // 6) Update Mario
        mario.update(input, ladders, platforms, hammers, blasters);

        // 7) Check if Mario reaches Donkey
        if (mario.isCollide(donkey) && !mario.holdHammer()) {
            isGameOver = true;
        }

        // 8) Display startedScore and time left
        displayInfo();

        updateExtra(input);

        // 9) Return game state
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
        disPlayBullet(STATUS_FONT, DKH_X, DKH_Y);
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

    protected abstract void updateExtra(Input input);
    protected abstract void disPlayBullet(Font STATUS_FONT, int DKH_X, int DKH_Y);
}
