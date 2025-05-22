/**
 * Represents a ladder in the game.
 * The ladder falls under gravity until it lands on a platform.
 */
public class Ladder extends GameEntity implements PhysicsAffected{
    private final static String LADDER_IMAGE = "res/ladder.png";
    private double velocityY = 0; // Current vertical velocity due to gravity

    /**
     * Constructs a ladder at the specified position.
     *
     * @param x The initial x-coordinate.
     * @param y The initial y-coordinate.
     */
    public Ladder(double x, double y) {
        super(LADDER_IMAGE, x, y);
    }

    /**
     * Updates the ladder's position by applying gravity and checking for platform collisions.
     * If a collision is detected, the ladder stops falling and rests on the platform.
     *
     * @param platforms An array of platforms in the game.
     */
    public void update(Platform[] platforms) {
        applyGravity(platforms);
        // 5) Draw the ladder after updating position
        draw();
    }

    @Override
    public void applyGravity(Platform[] platforms) {
        // 1) Apply gravity
        velocityY += LADDER_GRAVITY;

        // 2) Limit falling speed to terminal velocity
        if (velocityY > LADDER_TERMINAL_VELOCITY) {
            velocityY = LADDER_TERMINAL_VELOCITY;
        }

        // 3) Move the ladder downward
        y += velocityY;

        // 4) Check for collision with platforms
        for (Platform platform : platforms) {
            if (getBoundingBox().intersects(platform.getBoundingBox())) {
                // Position the ladder on top of the platform
                y = platform.getY()
                        - (platform.getHeight() / 2)  // Platform top edge
                        - (height / 2);     // Ladder height offset

                velocityY = 0; // Stop falling
                break; // Stop checking further once the ladder lands
            }
        }
    }
}
