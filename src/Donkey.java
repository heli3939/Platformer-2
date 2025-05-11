import bagel.*;
import bagel.util.Colour;
import bagel.util.Rectangle;

/**
 * Represents Donkey Kong in the game, affected by gravity and platform collisions.
 * The Donkey object moves downward due to gravity and lands on platforms when applicable.
 */
public class Donkey extends GameEntity implements PhysicsAffected{
    private final static String DONKEY_IMAGE = "res/donkey_kong.png";
    private double velocityY = 0;

    /**
     * Constructs a new Donkey at the specified starting position.
     *
     * @param x The initial x-coordinate of Donkey.
     * @param y The initial y-coordinate of Donkey.
     */
    public Donkey(double x, double y) {
        super(DONKEY_IMAGE, x, y);
    }

    /**
     * Updates Donkey's position by applying gravity and checking for platform collisions.
     * If Donkey lands on a platform, the velocity is reset to zero.
     *
     * @param platforms An array of platforms Donkey can land on.
     */
    public void update(Platform[] platforms) {
        // Apply gravity
        applyGravity(platforms);
        // Draw Donkey
        draw();
    }

    /**
     * Checks if Donkey is colliding with a given platform.
     *
     * @param platform The platform to check for collision.
     * @return {@code true} if Donkey is touching the platform, {@code false} otherwise.
     */
    private boolean isTouchingPlatform(Platform platform) {
        Rectangle donkeyBounds = getBoundingBox();
        return donkeyBounds.intersects(platform.getBoundingBox());
    }

    @Override
    public double getGravity(){
        return MARIO_GRAVITY;
    }

    @Override
    public double getTerminalVelocity() {
        return MARIO_TERMINAL_VELOCITY;
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
            if (isTouchingPlatform(platform)) {
                // Position Donkey on top of the platform
                y = platform.getY() - (platform.getHeight() / 2) - (height / 2);
                velocityY = 0; // Stop downward movement
                break;
            }
        }
    }
}
