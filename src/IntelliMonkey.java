/**
 * Represents an intelligent monkey extends from basic Monkey
 * Tracks its own facing direction and a time counter, used for throw banana
 */
public class IntelliMonkey extends Monkey{
    private boolean isMonkeyFacingRight;
    private int timeCount = 0;

    /**
     * Constructs an IntelliMonkey with the given position, direction, and walking pattern
     * @param x Initial x-coordinate.
     * @param y Initial y-coordinate.
     * @param isMonkeyFacingRight Initial facing direction
     * @param lenWalkPattern The number of steps in the walking pattern
     * @param walkPattern An int array defining the walking pattern (in px)
     */
    public IntelliMonkey(double x, double y, boolean isMonkeyFacingRight, int lenWalkPattern, int[] walkPattern) {
        super(x, y, isMonkeyFacingRight, lenWalkPattern, walkPattern);
        this.isMonkeyFacingRight = isMonkeyFacingRight;
    }

    /**
     * track intell monkey's exisiting time for throw banana interval
     * @return current time for the current interval
     */
    public int getTimeCount() {
        return timeCount;
    }

    /**
     * set intell monkey's time counter
     * @param timeCount time count value assign to counter
     */
    public void setTimeCount(int timeCount) {
        this.timeCount = timeCount;
    }
}
