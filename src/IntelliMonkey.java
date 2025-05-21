import bagel.Image;

public class IntelliMonkey extends Monkey{
    private final static int BANANACD = 300;

    // store image path for intell monkey face left and right
    public IntelliMonkey(double x, double y, boolean isNMonkeyFacingRight, int lenWalkPattern, int[] walkPattern) {
        super(x, y, isNMonkeyFacingRight, lenWalkPattern, walkPattern);
    }

    @Override
    public void update(Platform[] platforms) {
        super.update(platforms);
    }

}
