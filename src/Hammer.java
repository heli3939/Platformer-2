/**
 * Represents a Hammer weapon can be collected up and used by Mario.
 */
public class Hammer extends Weapon {
    private final static String HAMMER_IMAGE = "res/hammer.png";

    /**
     * Constructs a Hammer weapon at the specified starting position.
     * @param x Initial x-coordinate.
     * @param y Initial y-coordinate.
     */
    public Hammer(double x, double y) {
        super(HAMMER_IMAGE, x, y);
    }
}