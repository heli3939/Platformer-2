/**
 * represent proojectile (banana, bullet) in the game
 * move horizontally  and inactive when out of bounds or exceeding a maximum distance
 */
public abstract class Projectile extends GameEntity implements HorizontallyMovable{
    private double OUTOFSCREEN = -10000;
    private boolean isRight;
    private boolean isActive = true;

    /**
     * Constructs a new Projectile at the specified position using the given image
     * @param imagePath relative image path
     * @param x Initial x-coordinate.
     * @param y Initial y-coordinate.
     */
    public Projectile(String imagePath, double x, double y) {
        super(imagePath, x, y);
    }

    /**
     * deactive the projectile when travel exceed max dist
     * @param distTravel distanve traveled so far
     * @param maxDist permitted max dist of travel
     */
    public void distCheck(double distTravel, int maxDist){
        // deactivate the projectile if travel exceed the distance
        if (distTravel> maxDist) {
            setActive(false);
        }
    }

    /**
     *  only draw inside the screen when active
     */
    @Override
    public void draw(){
        if (!isActive){
            x = OUTOFSCREEN;
        }
        currentImage.draw(x, y);
    }

    /**
     * check if the project faces right
     * @return true: right; false: left
     */
    public boolean isRight() {
        return isRight;
    }

    /**
     * set direction of projectile travel
     * @param isRight new setting direction of travel of projectile
     */
    public void setRight(boolean isRight) {
        isRight = isRight;
    }

    /**
     * check if projectile is still active
     * @return true: active; false: otherwise
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * set active status of projectile
     * @param active new active status required to set on projectiile
     */
    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Enforces screen boundaries to prevent projectile from moving out of bounds.
     * Deactive projectile once outside of the left, right limits of the game window.
     */
    @Override
    public void enforceBoundaries() {
        // ensure not travel outside of the screen
        if (x < 0 || x > ShadowDonkeyKong.getScreenWidth()){
            setActive(false);
        }
    }
}
