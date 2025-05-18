import bagel.Image;

public class Bullet extends GameEntity implements HorizontallyMovable{
    private final static String BULLETR_IMG = "res/bullet_right.png";
    private final static String BULLETL_IMG = "res/bullet_left.png";

    private final int MAXDISTBULLET = 300;

    private Image bulletImage;
    private final Image BULLET_RIGHT_IMAGE = new Image(BULLETR_IMG);
    private final Image BULLET_LEFT_IMAGE = new Image(BULLETL_IMG);

    private  boolean isBulletRight;
    private boolean isActive = true;
    private double distTravel = 0;

    public Bullet(double x, double y) {
        super(BULLETR_IMG, x, y);
    }

    @Override
    public void draw(){
        bulletImage.draw(x, y);
    }

    public void update(Mario mario){
        if (x == mario.x && y == mario.y){
            setBulletRight(mario.isFacingRight());
        }
        bulletImage = isBulletRight() ? BULLET_RIGHT_IMAGE : BULLET_LEFT_IMAGE;
        x = isBulletRight() ? x + HorizontallyMovable.BULLET_MOVE_SPEED :
                x - HorizontallyMovable.BULLET_MOVE_SPEED;
        distTravel += HorizontallyMovable.BULLET_MOVE_SPEED;
        draw();
        enforceBoundaries();
        distCheck();
    }

    public boolean isBulletRight() {
        return isBulletRight;
    }

    public void setBulletRight(boolean bulletRight) {
        isBulletRight = bulletRight;
    }

    public void distCheck(){
        if (distTravel > MAXDISTBULLET){
            setActive(false);
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public void enforceBoundaries() {
        if (x < 0 || x > ShadowDonkeyKong.getScreenWidth()){
            setActive(false);
        }

    }
}
