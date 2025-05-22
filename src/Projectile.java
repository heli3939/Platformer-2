public abstract class Projectile extends GameEntity implements HorizontallyMovable{
    private double OUTOFSCREEN = -10000;
    private boolean isRight;
    private boolean isActive = true;

    public Projectile(String imagePath, double x, double y) {
        super(imagePath, x, y);
    }

    protected void distCheck(double distTravel, int maxDist){
        if (distTravel> maxDist) {
            setActive(false);
        }
    }

    @Override
    public void draw(){
        if (!isActive){
            x = OUTOFSCREEN;
        }
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
        if (x < 0 || x > ShadowDonkeyKong.getScreenWidth()){
            setActive(false);
        }
    }
}
