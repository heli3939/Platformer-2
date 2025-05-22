import bagel.*;
import bagel.util.Rectangle;

public class GameEntity {
    public double x;
    public double y;
    public Image currentImage;
    public double height;
    public double width;

    public GameEntity(String imagePath, double x, double y) {
        this.currentImage = new Image(imagePath);
        this.x = x;
        this.y = y;
        this.height = this.currentImage.getHeight();
        this.width = this.currentImage.getWidth();
    }

    public void draw() {
        currentImage.draw(x, y);
    }

    public Rectangle getBoundingBox() {
        return new Rectangle(x - (width / 2), y - (height / 2), width, height);
    }

    public boolean isCollide(GameEntity other) {
        return (this.getBoundingBox()).intersects(other.getBoundingBox());
    }

    /**
     * Gets the x-coordinate of the game entity.
     *
     * @return The current x-coordinate of the game entity.
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the game entity.
     *
     * @return The current y-coordinate of the game entity.
     */
    public double getY() {
        return y;
    }

    /**
     * Gets the width of the game entity.
     *
     * @return The width of the game entity.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Gets the height of the game entity.
     *
     * @return The height of the game entity.
     */
    public double getHeight() {
        return height;
    }

}
