public class Blaster extends Weapon {
    private final static String BLASTER_IMAGE = "res/blaster.png";
    private static final int INITIAL_BULLETS = 5;
    private int bulletCount = INITIAL_BULLETS;

    public Blaster(double x, double y) {
        super("res/blaster.png", x, y);
    }

    public int getBulletCount() {
        return bulletCount;
    }

    @Override
    public void update() {
        // static too
    }
}