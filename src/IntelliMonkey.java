import bagel.Image;

public class IntelliMonkey extends Monkey{
    private final static int BANANACD = 300;
    private boolean isMonkeyFacingRight;
    private int timeCount = 0;

    // store image path for intell monkey face left and right
    public IntelliMonkey(double x, double y, boolean isMonkeyFacingRight, int lenWalkPattern, int[] walkPattern) {
        super(x, y, isMonkeyFacingRight, lenWalkPattern, walkPattern);
        this.isMonkeyFacingRight = isMonkeyFacingRight;
    }

    public int getTimeCount() {
        return timeCount;
    }

    public void setTimeCount(int timeCount) {
        this.timeCount = timeCount;
    }

    @Override
    public void update(Platform[] platforms) {
        super.update(platforms);
    }

}
