import bagel.*;
import bagel.util.Rectangle;
import bagel.util.Colour;

/**
 * Represents a stationary platform in the game.
 * Platforms provide surfaces for Mario to walk on and interact with.
 */
public class Platform extends GameEntity{
    private final static String PLATFORM_IMAGE = "res/platform.png";

    /**
     * Constructs a platform at the specified position.
     *
     * @param x The initial x-coordinate of the platform.
     * @param y The initial y-coordinate of the platform.
     */
    public Platform(double x, double y) {
        super(PLATFORM_IMAGE, x, y);
    }
}
