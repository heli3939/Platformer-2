import bagel.DrawOptions;
import bagel.Drawing;
import bagel.Image;
import bagel.util.Colour;
import bagel.util.Rectangle;

/**
 * Represents a Hammer collectible in the game.
 * The hammer can be collected by the player, at which point it disappears from the screen.
 */
public class Hammer extends GameEntity{
    private final static String HAMMER_IMAGE = "res/hammer.png";
    private boolean isCollected = false;
    private double OUT_OF_SCREEN = 9999;

    /**
     * Constructs a Hammer at the specified position.
     *
     * @param x The initial x-coordinate of the hammer.
     * @param y The initial y-coordinate of the hammer.
     */
    public Hammer(double x, double y) {
        super(HAMMER_IMAGE, x, y);
    }


    /**
     * Draws the hammer on the screen if it has not been collected.
     */
    @Override
    public void draw() {
        if (isCollected) {
            x = OUT_OF_SCREEN;
           // Bagel centers images automatically
        }
        currentImage.draw(x, y);
    }

    /**
     * Marks the hammer as collected, removing it from the screen.
     */
    public void collect() {
        isCollected = true;
    }

    /**
     * Checks if the hammer has been collected.
     *
     * @return {@code true} if the hammer is collected, {@code false} otherwise.
     */
    public boolean isCollected() {
        return isCollected;
    }

}
