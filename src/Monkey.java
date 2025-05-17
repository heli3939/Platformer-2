import bagel.Image;

public class Monkey extends GameEntity{
    // implements HorizontallyMovable, PhysicsAffected

    private double velocityY = 0; // Vertical velocity

    // store image path for normal monkey face left and right
    private final static String NMONKEYL_IMG = "res/normal_monkey_left.png";
    private final static String NMONKEYR_IMG = "res/normal_monkey_right.png";

    // store image path for intell monkey face left and right
    private final static String IMONKEYL_IMG = "res/intelli_monkey_left.png";
    private final static String IMONKEYR_IMG = "res/intelli_monkey_right.png";


    // Mario images for different states
    private Image monkeyImage;
    private final Image NMONKEY_RIGHT_IMAGE = new Image(NMONKEYR_IMG);
    private final Image NMONKEY_LEFT_IMAGE = new Image(NMONKEYL_IMG);
    private final Image IMONKEY_LEFT_IMAGE = new Image(IMONKEYL_IMG);
    private final Image IMONKEY_RIGHT_IMAGE = new Image(IMONKEYR_IMG);


    public Monkey(double x, double y) {
        super(NMONKEYL_IMG, x, y);
    }

}
