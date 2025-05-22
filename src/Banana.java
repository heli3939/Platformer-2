import bagel.Image;

public class Banana extends Projectile{
    private final static String BANANA_IMG = "res/banana.png";
    private final Image BANANA_IMAGE = new Image(BANANA_IMG);
    private double distTravel = 0;
    private final int MAXDISTBANANA = 300;

    public Banana(double x, double y) {
        super(BANANA_IMG, x, y);
    }

    public void update(IntelliMonkey iMonkey){
        if (x == iMonkey.x && y == iMonkey.y){
            setRight(iMonkey.isFacingRight());
        }
        x = isRight() ? x + HorizontallyMovable.BANANA_MOVE_SPEED :
                x - HorizontallyMovable.BANANA_MOVE_SPEED;
        distTravel += HorizontallyMovable.BANANA_MOVE_SPEED;
        BANANA_IMAGE.draw(x, y);
        enforceBoundaries();
        distCheck(distTravel, MAXDISTBANANA);
    }
}
