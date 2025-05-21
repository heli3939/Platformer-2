import bagel.Image;import bagel.Image;
import bagel.util.Rectangle;


public class Monkey extends GameEntity implements PhysicsAffected, HorizontallyMovable{
    // implements HorizontallyMovable, PhysicsAffected

    private double velocityY = 0; // Vertical velocity

    // store image path for normal monkey face left and right
    private final static String NMONKEYL_IMG = "res/normal_monkey_left.png";
    private final static String NMONKEYR_IMG = "res/normal_monkey_right.png";

    // store image path for intell monkey face left and right
    private final static String IMONKEYL_IMG = "res/intelli_monkey_left.png";
    private final static String IMONKEYR_IMG = "res/intelli_monkey_right.png";

    private final int OUTOFSCREEN = -10000;

    // Mario images for different states
    private Image monkeyImage;
    private final Image NMONKEY_RIGHT_IMAGE = new Image(NMONKEYR_IMG);
    private final Image NMONKEY_LEFT_IMAGE = new Image(NMONKEYL_IMG);
    private final Image IMONKEY_LEFT_IMAGE = new Image(IMONKEYL_IMG);
    private final Image IMONKEY_RIGHT_IMAGE = new Image(IMONKEYR_IMG);

    private boolean isFacingRight;
    private int lenWalkPattern;
    private int[] walkPattern = new int[lenWalkPattern];

    private double distCount = 0;
    private int i = 0;
    private boolean isAlive = true;


    public Monkey(double x, double y, boolean isNMonkeyFacingRight, int lenWalkPattern, int[] walkPattern) {
        super(NMONKEYL_IMG, x, y);
        this.isFacingRight = isNMonkeyFacingRight;
        this.lenWalkPattern = lenWalkPattern;
        this.walkPattern = walkPattern;
        this.monkeyImage = isNMonkeyFacingRight ? NMONKEY_RIGHT_IMAGE : NMONKEY_LEFT_IMAGE;
    }

    @Override
    public void draw(){
        monkeyImage.draw(x, y);
    }

    public void update(Platform[] platforms){
        if (isAlive){
            monkeyImage = isFacingRight ? NMONKEY_RIGHT_IMAGE : NMONKEY_LEFT_IMAGE;
            applyGravity(platforms);
            LRMove(platforms, isFacingRight, lenWalkPattern, walkPattern);
            updateSprite();
        }
        else{
            x = OUTOFSCREEN;
        }
    }

    private void LRMove(Platform[] platforms, boolean isFacingRight, int lenWalkPattern, int[] walkPattern){
        if (isFacingRight){
            x += MONKEY_MOVE_SPEED;
        }
        else{
            x -= MONKEY_MOVE_SPEED;
        }
        this.distCount += MONKEY_MOVE_SPEED;
        boolean onEdge = true;
        double nextX = isFacingRight ? x + MONKEY_MOVE_SPEED : x - MONKEY_MOVE_SPEED;
        Rectangle footArea = new Rectangle(nextX, y + currentImage.getHeight() + 1, 1, 1);
        for (Platform platform : platforms) { // Pass platforms into monkey during update
            onEdge = platform.getBoundingBox().intersects(footArea);
            if (onEdge){
                break;
            }
        }
        if ((this.distCount >= walkPattern[i % lenWalkPattern]) || !onEdge && velocityY == 0) {
            i++;
            this.distCount = 0;
            this.isFacingRight = !isFacingRight;
            monkeyImage =  this.isFacingRight ? NMONKEY_RIGHT_IMAGE : NMONKEY_LEFT_IMAGE;
        }
    }

    @Override
    public double getGravity() {
        return MONKEY_GRAVITY;
    }

    @Override
    public double getTerminalVelocity() {
        return MONKEY_TERMINAL_VELOCITY;
    }

    /**
     * Switch Mario's sprite (left/right, or hammer/no-hammer).
     * Adjust Mario's 'y' so that the bottom edge stays consistent.
     */
    private void updateSprite() {
        // 1) Remember the old image and its bottom
        Image oldImage = monkeyImage;
        double oldHeight = oldImage.getHeight();
        double oldBottom = y + (oldHeight / 2);

        // 2) Assign the new image based on facing & hammer & blater
        //    (Whatever logic you currently use in update())

        monkeyImage = isFacingRight ? NMONKEY_RIGHT_IMAGE : NMONKEY_LEFT_IMAGE;

        // 3) Now recalc Mario’s bottom with the new image
        double newHeight = monkeyImage.getHeight();
        double newBottom = y + (newHeight / 2);

        // 4) Shift 'y' so the bottom edge is the same as before
        //    (If new sprite is taller, we move Mario up so he doesn't sink into platforms)
        y -= (newBottom - oldBottom);

        // 5) Update the recorded width/height to match the new image
        width  = monkeyImage.getWidth();
        height = newHeight;
    }

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

    public boolean isAlive() {
        return isAlive;
    }

    public void kill() {
        isAlive = false;
        System.out.println("Monkey Killed!");
    }

    @Override
    public void enforceBoundaries() {
        // Calculate half the width of the Mario image (used for centering and boundary checks)
        double halfW = monkeyImage.getWidth() / 2;

        // Prevent Mario from moving beyond the left edge of the screen
        if (x < halfW) {
            x = halfW;
        }

        // Prevent Mario from moving beyond the right edge of the screen
        double maxX = ShadowDonkeyKong.getScreenWidth() - halfW;
        if (x > maxX) {
            x = maxX;
        }

    }
}
