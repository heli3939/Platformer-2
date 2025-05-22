public abstract class Projectile extends GameEntity implements HorizontallyMovable{
    private double OUTOFSCREEN = -10000;
    private boolean isRight;
    private boolean isActive = true;

    public Projectile(String imagePath, double x, double y) {
        super(imagePath, x, y);
    }

    public void distCheck(double distTravel, int maxDist){
        // deactivate the projectile if travel exceed the distance
        if (distTravel> maxDist) {
            setActive(false);
        }
    }

    @Override
    public void draw(){
        if (!isActive){
            x = OUTOFSCREEN;
        }
        // only draw inside the screen when active
        currentImage.draw(x, y);
    }

    public boolean isRight() {
        return isRight;
    }

    public void setRight(boolean right) {
        isRight = right;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public void enforceBoundaries() {
        // ensure not travel outside of the screen
        if (x < 0 || x > ShadowDonkeyKong.getScreenWidth()){
            setActive(false);
        }
    }
}
