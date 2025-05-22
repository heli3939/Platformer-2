/**
 * Represents a barrel in the game, affected by gravity and platform collisions.
 * The barrel can be destroyed, at which point it will no longer be drawn or interact with the environment.
 */
public class Barrel extends GameEntity implements PhysicsAffected{
    private final static String BARREL_IMAGE = "res/barrel.png";
    private double velocityY = 0;
    private boolean isDestroyed = false;

    /**
     * Constructs a new Barrel at the specified starting position.
     *
     * @param x The initial x-coordinate of the barrel.
     * @param y The initial y-coordinate of the barrel.
     */
    public Barrel(double x, double y) {
        super(BARREL_IMAGE, x, y);
    }

    /**
     * Updates the barrel's position, applies gravity, checks for platform collisions,
     * and renders the barrel if it is not destroyed.
     *
     * @param platforms An array of platforms for collision detection.
     */
    public void update(Platform[] platforms) {
        if (!isDestroyed) {
            // 1) Apply gravity
            velocityY += BARREL_GRAVITY;
            if (velocityY > BARREL_TERMINAL_VELOCITY) {
                velocityY = BARREL_TERMINAL_VELOCITY;
            }
            y += velocityY;

            // 2) Check for platform collisions
            for (Platform platform : platforms) {
                if (this.getBoundingBox().intersects(platform.getBoundingBox())) {
                    // Position the barrel on top of the platform
                    y = platform.getY() - (platform.getHeight() / 2) - (height / 2);
                    velocityY = 0; // Stop falling
                    break;
                }
            }

            // 3) Draw the barrel
            draw();
        }
    }


    /**
     * Marks the barrel as destroyed, preventing it from being drawn or updated.
     */
    public void destroy() {
        isDestroyed = true;
        System.out.println("Barrel destroyed!");
    }

    /**
     * Checks if the barrel has been destroyed.
     *
     * @return {@code true} if the barrel is destroyed, {@code false} otherwise.
     */
    public boolean isDestroyed() {
        return isDestroyed;
    }

    @Override
    public void applyGravity(Platform[] platforms) {
        // Apply gravity
        velocityY += DONKEY_GRAVITY;
        y += velocityY;
        if (velocityY > DONKEY_TERMINAL_VELOCITY) {
            velocityY = DONKEY_TERMINAL_VELOCITY;
        }
        // Check for platform collisions
        for (Platform platform : platforms) {
            if (this.isCollide(platform)) {
                // Position Donkey on top of the platform
                y = platform.getY() - (platform.getHeight() / 2) - (height / 2);
                velocityY = 0; // Stop downward movement
                break;
            }
        }
    }

}
