import bagel.*;
import bagel.util.Rectangle;

/**
 * Represents a generic game entity with position, height, width and image
 * Provides basic collision detection and bounding box methods
 */
public class GameEntity {
    /** x-coordinate of the game entity */
    public double x;
    /** y-coordinate of the game entity */
    public double y;
    /** current image of the game entity */
    public Image currentImage;
    /** height of the game entity's image */
    public double height;
    /** width of the game entity's image */
    public double width;

    /**
     *
     * @param imagePath relative file path of entity image
     * @param x The initial x-coordinate of the game entity.
     * @param y The initial y-coordinate of the game entity.
     */
    public GameEntity(String imagePath, double x, double y) {
        this.currentImage = new Image(imagePath); // image for provided image path
        this.x = x;
        this.y = y;
        this.height = this.currentImage.getHeight();
        this.width = this.currentImage.getWidth();
    }

    /**
     * draw the entity with its current image at given coordinate position
     */
    public void draw() {
        currentImage.draw(x, y);
    }

    /**
     * get bounding box of the entity used for collision detection.
     * @return a Rectangle represents entity's bounds
     */
    public Rectangle getBoundingBox() {
        return new Rectangle(x - (width / 2), y - (height / 2), width, height);
    }

    /**
     * check if there's collision between this and another game entity
     * @param other another GameEntity for collision check
     * @return true for colision happen; false otherwise
     */
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
