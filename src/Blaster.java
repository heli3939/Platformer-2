/**
 * Represents a Blaster weapon can be collected up and used by Mario.
 */
public class Blaster extends Weapon {
    private final static String BLASTER_IMAGE = "res/blaster.png";

    /**
     * Constructs a Blaster weapon at the specified starting position.
     * @param x Initial x-coordinate.
     * @param y Initial y-coordinate.
     */
    public Blaster(double x, double y) {
        super(BLASTER_IMAGE, x, y);
    }
}