/**
 * Represents a Weapon collectible in the game.
 * The hammer can be collected by the player, at which point it disappears from the screen.
 */
public  class Weapon extends GameEntity{
    private boolean isCollected = false; // record if it's picked up by mario
    private double OUTOFSCREEN = -10000; // a random point out side of the screen

    /**
     * Constructs a Weapon at the specified position.
     *
     * @param x The initial x-coordinate of the hammer.
     * @param y The initial y-coordinate of the hammer.
     */
    public Weapon(String imagePath, double x, double y) {
        super(imagePath, x, y);
    }

    /**
     * Draws the hammer on the screen if it has not been collected.
     */
    @Override
    public void draw() {
        if (isCollected) {
            x = OUTOFSCREEN;
            // Bagel centers images automatically
        }
        currentImage.draw(x, y);
    }

    /**
     * Marks the hammer as collected, removing it from the screen.
     */
    public void collect() {
        isCollected = true;
    }

    /**
     * Checks if the hammer has been collected.
     *
     * @return {@code true} if the hammer is collected, {@code false} otherwise.
     */
    public boolean isCollected() {
        return isCollected;
    }

}
