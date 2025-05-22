import bagel.*;
import bagel.util.Rectangle;

/**
 * Represents the player-controlled character, Mario.
 * Mario can move, jump, climb ladders, pick up a hammer, and interact with platforms.
 */
public class Mario extends GameEntity implements HorizontallyMovable, PhysicsAffected {
    private double velocityY = 0; // Vertical velocity
    private boolean isJumping = false; // Whether Mario is currently jumping
    private boolean hasHammer = false; // Whether Mario has collected a hammer
    private boolean hasBlaster = false; // Whether Mario has collected a blaster

    // store image path for mario face left and right
    private final static String MARIOL_IMG = "res/mario_left.png";
    private final static String MARIOR_IMG = "res/mario_right.png";

    // store image path for mario face left and right with a hammer
    private final static String MARIOLH_IMG = "res/mario_hammer_left.png";
    private final static String MARIORH_IMG = "res/mario_hammer_right.png";

    // store image path for mario face left and right with a blaster
    private final static String MARIOLB_IMG = "res/mario_blaster_left.png";
    private final static String MARIORB_IMG = "res/mario_blaster_right.png";


    // Mario images for different states
    private Image marioImage;
    private final Image MARIO_RIGHT_IMAGE = new Image(MARIOR_IMG);
    private final Image MARIO_LEFT_IMAGE = new Image(MARIOL_IMG);
    private final Image MARIO_HAMMER_LEFT_IMAGE = new Image(MARIOLH_IMG);
    private final Image MARIO_HAMMER_RIGHT_IMAGE = new Image(MARIORH_IMG);
    private final Image MARIO_BLASTER_LEFT_IMAGE = new Image(MARIOLB_IMG);
    private final Image MARIO_BLASTER_RIGHT_IMAGE = new Image(MARIORB_IMG);

    // Movement physics constants
    private static final double JUMP_STRENGTH = -5;
    private static final double CLIMB_SPEED = 2;

    private final int FULLBLASTERBLT = 5; // number of bullets in unused blaster

    private int bulletCount = 0;  // number of bullet own now
    private boolean isFacingRight = true; // Mario's facing direction

    /**
     * Constructs a Mario character at the specified starting position.
     *
     * @param x Initial x-coordinate.
     * @param y Initial y-coordinate.
     */
    public Mario(double x, double y) {
        super(MARIOR_IMG, x, y);
        marioImage = MARIO_RIGHT_IMAGE;
    }

    /**
     * Sets whether Mario has picked up the hammer.
     *
     * @param status {@code true} if Mario has the hammer, {@code false} otherwise.
     */
    public void setHasHammer(boolean status) {
        this.hasHammer = status;
    }

    /**
     * Checks if Mario has the hammer.
     *
     * @return {@code true} if Mario has the hammer, {@code false} otherwise.
     */
    public boolean holdHammer() {
        return this.hasHammer;
    }


    /**
     * Sets whether Mario has picked up the blaster.
     *
     * @param status {@code true} if Mario has the blaster, {@code false} otherwise.
     */
    public void setHasBlaster(boolean status) {
        this.hasBlaster = status;
    }

    /**
     * Checks if Mario has the blaster.
     *
     * @return {@code true} if Mario has the blaster, {@code false} otherwise.
     */
    public boolean holdBlaster() {
        return this.hasBlaster;
    }


    /**
     * Updates Mario's movement, jumping, ladder climbing, hammer collection, and interactions.
     * This method is called every frame to process player input and update Mario's state.
     *
     * @param input     The player's input (keyboard/mouse).
     * @param ladders   The array of ladders in the game that Mario can climb.
     * @param platforms The array of platforms in the game that Mario can walk on.
     * @param hammers    The array of hammer objects that Mario can collect and use.
     */
    public void update(Input input, Ladder[] ladders, Platform[] platforms, Hammer[] hammers, Blaster[] blasters) {
        handleHorizontalMovement(input); // Horizontal movement
        for (Hammer hammer: hammers){
            handleHammerCollection(hammer); // picked up the hammer
        }
        if (blasters != null){ // only when blaster present in current game
            for (Blaster blaster: blasters){
                handleBlasterCollection(blaster); // pick up the blaster
            }
        }
        updateSprite(); // Update Mario’s current sprite (hammer or not, facing left or right, blaster or not)
        // Ladder logic – check if on a ladder
        boolean isOnLadder;
        isOnLadder = handleLadders(input, ladders);
        // Jump logic: if on platform (we'll detect after we move) but let's queue jump if needed
        boolean wantsToJump = input.wasPressed(Keys.SPACE);
        // If not on ladder, apply gravity, move Mario
        if (!isOnLadder) {
            applyGravity(platforms);
        }
        // Actually move Mario vertically after gravity
        y += velocityY;
        // Check for platform collision AFTER Mario moves
        boolean onPlatform;
        onPlatform = handlePlatforms(platforms);
        //  If we are on the platform, allow jumping; Prevent Mario from falling below the ground
        handleJumping(onPlatform, wantsToJump);
        // Enforce horizontal screen bounds
        enforceBoundaries();
        // Draw Mario
        draw();
    }

    @Override
    public void draw(){
        marioImage.draw(x, y);
    }

    public boolean isFacingRight() {
        return isFacingRight;
    }

    /**
     * Handles Mario's interaction with platforms to determine if he is standing on one.
     * Mario will only snap to a platform if he is moving downward (velocityY >= 0),
     * preventing his jump from being interrupted in mid-air.
     *
     * @param platforms An array of {@link Platform} objects representing the platforms in the game.

     * @return {@code true} if Mario is standing on a platform, {@code false} otherwise.
     */
    private boolean handlePlatforms(Platform[] platforms) {
        boolean onPlatform = false;
        // We'll only snap Mario to a platform if he's moving downward (velocityY >= 0)
        // so we don't kill his jump in mid-air.
        if (velocityY >= 0) {
            for (Platform platform : platforms) {
                Rectangle marioBounds    = getBoundingBox();
                Rectangle platformBounds = platform.getBoundingBox();
                if (marioBounds.intersects(platformBounds)) {
                    double marioBottom = marioBounds.bottom();
                    double platformTop = platformBounds.top();
                    // If Mario's bottom is at or above the platform's top
                    // and not far below it (a small threshold based on velocity)
                    if (marioBottom <= platformTop + velocityY) {
                        // Snap Mario so his bottom = the platform top
                        y = platformTop - (marioImage.getHeight() / 2);
                        velocityY = 0;
                        isJumping = false;
                        onPlatform = true;
                        break; // We found a platform collision
                    }
                }
            }
        }
        return onPlatform;
    }

    /**
     * Handles Mario's interaction with ladders, allowing him to climb up or down
     * based on user input and position relative to the ladder.
     *
     * Mario can only climb if he is within the horizontal boundaries of the ladder.
     * He stops sliding unintentionally when not pressing movement keys.
     *
     * @param input   The {@link Input} object that checks for user key presses.
     * @param ladders An array of {@link Ladder} objects representing ladders in the game.
     * @return {@code true} if Mario is on a ladder, {@code false} otherwise.
     */
    private boolean handleLadders(Input input, Ladder[] ladders) {
        boolean isOnLadder = false;
        for (Ladder ladder : ladders) {
            double ladderLeft  = ladder.getX() - (ladder.getWidth() / 2);
            double ladderRight = ladder.getX() + (ladder.getWidth() / 2);
            double marioRight  = x + (marioImage.getWidth() / 2);
            double marioBottom = y + (marioImage.getHeight() / 2);
            double ladderTop    = ladder.getY() - (ladder.getHeight() / 2);
            double ladderBottom = ladder.getY() + (ladder.getHeight() / 2);

            if (isCollide(ladder)) {
                // Check horizontal overlap so Mario is truly on the ladder
                if (marioRight - marioImage.getWidth() / 2 > ladderLeft && marioRight - marioImage.getWidth() / 2 < ladderRight) {
                    isOnLadder = true;
                    // Stop Mario from sliding up when not moving**
                    if (!input.isDown(Keys.UP) && !input.isDown(Keys.DOWN)) {
                        velocityY = 0;  // Prevent sliding inertia effect
                    }
                    // ----------- Climb UP -----------
                    if (input.isDown(Keys.UP)) {
                        y -= CLIMB_SPEED;
                        velocityY = 0;
                    }
                    // ----------- Climb DOWN -----------
                    if (input.isDown(Keys.DOWN)) {
                        double nextY = y + CLIMB_SPEED;
                        double nextBottom = nextY + (marioImage.getHeight() / 2);
                        if (marioBottom > ladderTop && nextBottom <= ladderBottom) {
                            y = nextY;
                            velocityY = 0;
                        } else if (marioBottom == ladderBottom) {
                            velocityY = 0;
                        } else if (ladderBottom - marioBottom < CLIMB_SPEED) {
                            y = y + ladderBottom - marioBottom;
                            velocityY = 0;
                        }
                    }
                }
            } else if (marioBottom == ladderTop && input.isDown(Keys.DOWN) && (marioRight - marioImage.getWidth() / 2 > ladderLeft && marioRight - marioImage.getWidth() / 2  < ladderRight)) {
                double nextY = y + CLIMB_SPEED;
                y = nextY;
                velocityY = 0; // ignore gravity
            } else if (marioBottom == ladderBottom && input.isDown(Keys.DOWN) && (marioRight - marioImage.getWidth() / 2 > ladderLeft && marioRight - marioImage.getWidth() / 2  < ladderRight)) {
                velocityY = 0; // ignore gravity
            }
        }
        return isOnLadder;
    }

    /** Handles horizontal movement based on player input. */
    private void handleHorizontalMovement(Input input) {
        if (input.isDown(Keys.LEFT)) { // move left
            x -= HorizontallyMovable.MARIO_MOVE_SPEED;
            isFacingRight = false;
        } else if (input.isDown(Keys.RIGHT)) { // move right
            x += HorizontallyMovable.MARIO_MOVE_SPEED;
            isFacingRight = true;
        }
    }

    /** Handles collecting the hammer if Mario is in contact with it. */
    private void handleHammerCollection(Hammer hammer) {
       // collect valid hammer when collide
        if (!hammer.isCollected() && isCollide(hammer)) {
            setHasHammer(true);
            setHasBlaster(false); // remove blaster if held it
            bulletCount = 0; // empty bullet count
            hammer.collect();
        }
    }

    /** Handles collecting the hammer if Mario is in contact with it. */
    private void handleBlasterCollection(Blaster blaster) {
        // collect valid blaster when collide
        if (!blaster.isCollected() && isCollide(blaster)) {
            if (!hasBlaster){
                bulletCount = FULLBLASTERBLT;
            }
            else{ // add up bullet counts if already held one blaster
                bulletCount += FULLBLASTERBLT;
            }
            setHasBlaster(true);
            setHasHammer(false); // remove hammer
            blaster.collect();
        }
    }

    public int getBulletCount() {
        return bulletCount;
    }

    public void setBulletCount(int bulletCount) {
        this.bulletCount = bulletCount;
    }

    /** Handles jumping if Mario is on a platform and jump is requested. */
    private void handleJumping(boolean onPlatform, boolean wantsToJump) {
        if (onPlatform && wantsToJump) {
            velocityY = JUMP_STRENGTH;
            isJumping = true;
        }
        double bottomOfMario = y + (marioImage.getHeight() / 2);
        if (bottomOfMario > ShadowDonkeyKong.getScreenHeight()) { // can't jump out of the screen
            y = ShadowDonkeyKong.getScreenHeight() - (marioImage.getHeight() / 2);
            velocityY = 0;
            isJumping = false;
        }
    }

    /**
     * Enforces screen boundaries to prevent Mario from moving out of bounds.
     * Ensures Mario stays within the left, right, and bottom limits of the game window.
     */
    public void enforceBoundaries() {
        // Calculate half the width of the Mario image (used for centering and boundary checks)
        double halfW = marioImage.getWidth() / 2;
        // Prevent Mario from moving beyond the left edge of the screen
        if (x < halfW) {
            x = halfW;
        }
        // Prevent Mario from moving beyond the right edge of the screen
        double maxX = ShadowDonkeyKong.getScreenWidth() - halfW;
        if (x > maxX) {
            x = maxX;
        }
        // Calculate Mario's bottom edge position
        double bottomOfMario = y + (marioImage.getHeight() / 2);
        // Prevent Mario from falling below the bottom of the screen
        if (bottomOfMario > ShadowDonkeyKong.getScreenHeight()) {
            // Reposition Mario to stand on the bottom edge
            y = ShadowDonkeyKong.getScreenHeight() - (marioImage.getHeight() / 2);
            // Stop vertical movement and reset jumping state
            velocityY = 0;
            isJumping = false;
        }
    }

    /**
     * Switch Mario's sprite (left/right, or hammer/no-hammer).
     * Adjust Mario's 'y' so that the bottom edge stays consistent.
     */
    private void updateSprite() {
        //  Remember the old image and its bottom
        Image oldImage = marioImage;
        double oldHeight = oldImage.getHeight();
        double oldBottom = y + (oldHeight / 2);
        // Assign the new image based on facing & hammer & blater
        if (hasHammer) { // hold hammer
            marioImage = isFacingRight ? MARIO_HAMMER_RIGHT_IMAGE : MARIO_HAMMER_LEFT_IMAGE;
        } else if (hasBlaster) { // hold blaster
            marioImage = isFacingRight ? MARIO_BLASTER_RIGHT_IMAGE : MARIO_BLASTER_LEFT_IMAGE;
        } else { // hold nothing
            marioImage = isFacingRight ? MARIO_RIGHT_IMAGE : MARIO_LEFT_IMAGE;
        }
        // recalc Mario’s bottom with the new image
        double newHeight = marioImage.getHeight();
        double newBottom = y + (newHeight / 2);
        //  Shift 'y' so the bottom edge is the same as before
        //    (If new sprite is taller, we move Mario up so he doesn't sink into platforms)
        y -= (newBottom - oldBottom);
        // Update the recorded width/height to match the new image
        width  = marioImage.getWidth();
        height = newHeight;
    }

    /**
     * Determines if Mario successfully jumps over a barrel.
     *
     * @param barrel The barrel object to check.
     * @return {@code true} if Mario successfully jumps over the barrel, {@code false} otherwise.
     */
    public boolean jumpOver(Barrel barrel) {
        return isJumping
                && Math.abs(this.x - barrel.getX()) <= 1
                && (this.y < barrel.getY())
                && ((this.y + height / 2) >= (barrel.getY() + barrel.height / 2
                - (JUMP_STRENGTH * JUMP_STRENGTH) / (2 * MARIO_GRAVITY) - height / 2));
    }

    @Override
    public void applyGravity(Platform[] platforms) {
        velocityY += MARIO_GRAVITY;
        velocityY = Math.min(MARIO_TERMINAL_VELOCITY, velocityY);
    }
}
