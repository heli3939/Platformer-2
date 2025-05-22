import bagel.Image;
import bagel.util.Rectangle;

/**
 * represent enemy monkey in the game
 * monkeys can walk across platforms, turn around at edges/reaching distance o=in walking pattern
 * and respond to gravity when loaded, have normal and intelligent types.
 */
public class Monkey extends GameEntity implements PhysicsAffected, HorizontallyMovable{
    // implements HorizontallyMovable, PhysicsAffected

    private double velocityY = 0; // Vertical velocity

    // store image path for normal monkey face left and right
    private final static String NMONKEYL_IMG = "res/normal_monkey_left.png";
    private final static String NMONKEYR_IMG = "res/normal_monkey_right.png";

    // store image path for intell monkey face left and right
    private final static String IMONKEYL_IMG = "res/intelli_monkey_left.png";
    private final static String IMONKEYR_IMG = "res/intelli_monkey_right.png";

    private final int OUTOFSCREEN = -10000; // a random point out of screen boundary

    // monkey images for different types and directions
    private Image monkeyImage;
    private final Image NMONKEY_RIGHT_IMAGE = new Image(NMONKEYR_IMG);
    private final Image NMONKEY_LEFT_IMAGE = new Image(NMONKEYL_IMG);
    private final Image IMONKEY_LEFT_IMAGE = new Image(IMONKEYL_IMG);
    private final Image IMONKEY_RIGHT_IMAGE = new Image(IMONKEYR_IMG);

    private boolean isFacingRight; // record facing direction of monkey
    private int lenWalkPattern; // length of walk pattern
    // walk pattern of monkey, store a sequence of integers indicate distance in pixel
    private int[] walkPattern = new int[lenWalkPattern];

    private double distCount = 0; // count distance of current walk (in one direction)
    private int i = 0; // count index of curent walk in the walking pattern
    private boolean isAlive = true; // revords if the monkey is alive

    /**
     * Constructs a monkey with the given position, direction, and walking pattern
     * @param x Initial x-coordinate.
     * @param y Initial y-coordinate.
     * @param isMonkeyFacingRight Initial facing direction
     * @param lenWalkPattern The number of steps in the walking pattern
     * @param walkPattern An int array defining the walking pattern (in px)
     */
    public Monkey(double x, double y, boolean isMonkeyFacingRight, int lenWalkPattern, int[] walkPattern) {
        super(NMONKEYL_IMG, x, y);
        this.isFacingRight = isMonkeyFacingRight;
        this.lenWalkPattern = lenWalkPattern;
        this.walkPattern = walkPattern;
        this.monkeyImage = NMONKEY_LEFT_IMAGE;
    }

    /**
     * draw monkey with correct current image at the coordinate point
     */
    @Override
    public void draw(){
        monkeyImage.draw(x, y);
    }

    /**
     * Updates monkey's movement, image change and fall on platform when is alive
     * @param platforms an array of platforms for collision detection
     */
    public void update(Platform[] platforms){
        // only update if alive
        if (isAlive){
            monkeyImage = getMonkeyImage();
            applyGravity(platforms);
            LRMove(platforms, isFacingRight, lenWalkPattern, walkPattern);
            updateSprite();
        }
        else{ // remove if killed
            x = OUTOFSCREEN;
        }
    }

    /**
     * get correct image of monkey based on its type and facing direction
     * @return correct image of monkey based on its type and facing direction
     */
    public Image getMonkeyImage() {
        // different image for different type of monkeys and directions
        if (this instanceof IntelliMonkey){ // image for intell monkey
            return isFacingRight ? IMONKEY_RIGHT_IMAGE : IMONKEY_LEFT_IMAGE;
        }
        else{ // image for normal monkey
            return isFacingRight ? NMONKEY_RIGHT_IMAGE : NMONKEY_LEFT_IMAGE;
        }
    }

    private void LRMove(Platform[] platforms, boolean isFacingRight, int lenWalkPattern, int[] walkPattern){
        if (isFacingRight){ // walk right when face right
            x += MONKEY_MOVE_SPEED;
        }
        else{ // walk left when face left
            x -= MONKEY_MOVE_SPEED;
        }
        // count dist for current move
        this.distCount += MONKEY_MOVE_SPEED;
        // used to check if monkey is on edge of platform, ensure it not fell off and turn around
        boolean onEdge = true;
        double nextX = isFacingRight ? x + MONKEY_MOVE_SPEED : x - MONKEY_MOVE_SPEED;
        Rectangle footArea = new Rectangle(nextX, y + currentImage.getHeight() + 1, 1, 1);
        for (Platform platform : platforms) { // Pass platforms into monkey during update
            onEdge = platform.getBoundingBox().intersects(footArea);
            if (onEdge){
                break; // confirm monkey is on edge
            }
        }
        // turn around if on edge (include when just fell on the platform) or reach the distance for walking pattern
        if ((this.distCount >= walkPattern[i % lenWalkPattern]) || !onEdge && velocityY == 0) {
            i++;
            this.distCount = 0;
            this.isFacingRight = !isFacingRight;
            // change direction of image
            monkeyImage = getMonkeyImage();
        }
    }

    /**
     * Switch Mario's sprite (left/right, or hammer/no-hammer).
     * Adjust Mario's 'y' so that the bottom edge stays consistent.
     */
    private void updateSprite() {
        // Remember the old image and its bottom
        Image oldImage = monkeyImage;
        double oldHeight = oldImage.getHeight();
        double oldBottom = y + (oldHeight / 2);
        // Assign the new image based on direction
        monkeyImage = getMonkeyImage();
        // Now recalc monkey’s bottom with the new image
        double newHeight = monkeyImage.getHeight();
        double newBottom = y + (newHeight / 2);
        // Shift 'y' so the bottom edge is the same as before
        // (If new sprite is taller, we move monkey up so he doesn't sink into platforms)
        y -= (newBottom - oldBottom);
        // Update the recorded width/height to match the new image
        width  = monkeyImage.getWidth();
        height = newHeight;
    }

    /**
     * get if monkey faces right
     * @return true: right; false: left
     */
    public boolean isFacingRight() {
        return isFacingRight;
    }

    /**
     * Apply gravity on monkey and let it land on platform by detecting collision
     * @param platforms An array of platforms for collision detection.
     */
    @Override
    public void applyGravity(Platform[] platforms) {
        // Apply gravity
        velocityY += MONKEY_GRAVITY;
        y += velocityY;
        if (velocityY > MONKEY_TERMINAL_VELOCITY) {
            velocityY = MONKEY_TERMINAL_VELOCITY;
        }
        // Check for platform collisions
        for (Platform platform : platforms) {
            if (this.isCollide(platform)) {
                // Position Donkey on top of the platform
                y = platform.getY() - (platform.getHeight() / 2) - (this.height / 2);
                velocityY = 0; // Stop downward movement
                break;
            }
        }
    }

    /**
     * check if monkey still alive
     * @return true: alive; false: killed
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * set monkey as not alive when be killed
     */
    public void kill() {
        isAlive = false;
    }

    /**
     * Enforces screen boundaries to prevent monkey from moving out of bounds.
     * Ensures monkey stays within the left, right limit of the game window.
     */
    @Override
    public void enforceBoundaries() {
        // Calculate half the width of the monkey image (used for centering and boundary checks)
        double halfW = monkeyImage.getWidth() / 2;
        // Prevent monkey from moving beyond the left edge of the screen
        if (x < halfW) {
            x = halfW;
        }
        // Prevent monkey from moving beyond the right edge of the screen
        double maxX = ShadowDonkeyKong.getScreenWidth() - halfW;
        if (x > maxX) {
            x = maxX;
        }
    }
}
