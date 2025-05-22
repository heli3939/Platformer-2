import bagel.Image;

public class Bullet extends Projectile{
    // image path of bullet in right and left
    private final static String BULLETR_IMG = "res/bullet_right.png";
    private final static String BULLETL_IMG = "res/bullet_left.png";

    private final Image BULLET_RIGHT_IMAGE = new Image(BULLETR_IMG);
    private final Image BULLET_LEFT_IMAGE = new Image(BULLETL_IMG);

    private final int MAXDISTBULLET = 300;
    private Image bulletImage;

    private double distTravel = 0;

    public Bullet(double x, double y) {
        super(BULLETR_IMG, x, y);
    }

    public void update(Mario mario){
        // set direction of bullet same as the facing direction of monkey shot it
        if (x == mario.x && y == mario.y){
            setRight(mario.isFacingRight());
        }
        // use bullet image for correct direction
        bulletImage = isRight() ? BULLET_RIGHT_IMAGE : BULLET_LEFT_IMAGE;
        // move in correct direction
        x = isRight() ? x + HorizontallyMovable.BULLET_MOVE_SPEED :
                x - HorizontallyMovable.BULLET_MOVE_SPEED;
        distTravel += HorizontallyMovable.BULLET_MOVE_SPEED;
        bulletImage.draw(x, y);
        // prevent out of screen
        enforceBoundaries();
        // deactive when reach MAXDISTBULLET
        distCheck(distTravel, MAXDISTBULLET);
    }
}
