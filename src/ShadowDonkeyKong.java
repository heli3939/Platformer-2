import bagel.*;
import java.util.Properties;

/**
 * The main class for the Shadow Donkey Kong game.
 * This class extends {@code AbstractGame} and is responsible for managing game initialization,
 * updates, rendering, and handling user input.
 *
 * It sets up the game world, initializes characters, platforms, ladders, and other game objects,
 * and runs the game loop to ensure smooth gameplay.
 */
public class ShadowDonkeyKong extends AbstractGame {

    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    private HomeScreen homeScreen;
    private GamePlayScreen gamePlayScreen;
    private GameEndScreen gameEndScreen;

    public static double screenWidth;

    public static double screenHeight;

    private static int currLevel = -1;
    private int score = 0;

    /**
     * Constructs a new instance of the ShadowDonkeyKong game.
     * Initializes the game window using provided properties and sets up the home screen.
     *
     * @param gameProps     A {@link Properties} object containing game configuration settings
     *                      such as window width and height.
     * @param messageProps  A {@link Properties} object containing localized messages or UI labels,
     *                      including the title for the home screen.
     */
    public ShadowDonkeyKong(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty("window.width")),
                Integer.parseInt(gameProps.getProperty("window.height")),
                messageProps.getProperty("home.title"));

        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;
        this.screenWidth = Integer.parseInt(gameProps.getProperty("window.width"));
        this.screenHeight = Integer.parseInt(gameProps.getProperty("window.height"));

        homeScreen = new HomeScreen(GAME_PROPS, MESSAGE_PROPS);
    }


    /**
     * Render the relevant screen based on the keyboard input given by the user and the status of the gameplay.
     * @param input The current mouse/keyboard input.
     */
    @Override
    protected void update(Input input) {
        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }
        int levelSelect = homeScreen.update(input);
        // Home Screen
        if (gamePlayScreen == null && gameEndScreen == null) {
            if (levelSelect == 1) {
                currLevel = 1;
                gamePlayScreen = new Level1(GAME_PROPS, currLevel, 0);
            } else if (levelSelect == 2) {
                currLevel = 2;
                gamePlayScreen = new Level2(GAME_PROPS, currLevel, 0);
            }
        }
        // Gameplay Screen
        else if (gamePlayScreen != null && gameEndScreen == null) {
            // The gameplay ended
            if (gamePlayScreen.update(input)) {
                boolean isWon = gamePlayScreen.isLevelCompleted();

                // 1) GET THE SCORE
                int finalScore = gamePlayScreen.getStartedScore();
                int timeRemaining = gamePlayScreen.getSecondsLeft();

                if (currLevel == 1 && isWon){
                    currLevel = 2;
                    score += finalScore;
                    gamePlayScreen = new Level2(GAME_PROPS, currLevel, score);
                }
                else{
                    // 2) CREATE THE END SCREEN
                    gameEndScreen = new GameEndScreen(GAME_PROPS, MESSAGE_PROPS);

                    // 3) PASS finalScore
                    gameEndScreen.setIsWon(isWon);
                    gameEndScreen.setFinalScore(timeRemaining, finalScore);

                    // 4) Nullify gameplay
                    gamePlayScreen = null;
                }

            }

        }

        // Game Over / Victory Screen
        else if (gamePlayScreen == null ) {
            if (gameEndScreen.update(input)) {
                gamePlayScreen = null;
                gameEndScreen = null;
                score = 0;
            }
        }
    }


    /**
     * Retrieves the width of the game screen.
     *
     * @return The width of the screen in pixels.
     */
    public static double getScreenWidth() {
        return screenWidth;
    }

    /**
     * Retrieves the height of the game screen.
     *
     * @return The height of the screen in pixels.
     */
    public static double getScreenHeight() {
        return screenHeight;
    }


    /**
     * The main entry point of the Shadow Donkey Kong game.
     *
     * This method loads the game properties and message files, initializes the game,
     * and starts the game loop.
     *
     * @param args Command-line arguments (not used in this game).
     */
    public static void main(String[] args) {
        Properties gameProps = IOUtils.readPropertiesFile("res/app.properties");
        Properties messageProps = IOUtils.readPropertiesFile("res/message.properties");
        ShadowDonkeyKong game = new ShadowDonkeyKong(gameProps, messageProps);
        game.run();
    }


}

