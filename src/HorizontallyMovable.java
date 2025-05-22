/**
 * an interface be implemented to all game entities able to be move
 * horizontally, prevent them go out of screen and provide their speed value
 */
public interface HorizontallyMovable {
    double MARIO_MOVE_SPEED = 3.5;
    double MONKEY_MOVE_SPEED = 0.5;
    double BANANA_MOVE_SPEED = 1.8;
    double BULLET_MOVE_SPEED = 3.8;

    void enforceBoundaries();
}
