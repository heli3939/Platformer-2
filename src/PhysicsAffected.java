public interface PhysicsAffected {
    double MARIO_GRAVITY = 0.2;
    double DONKEY_GRAVITY = 0.4;
    double LADDER_GRAVITY = 0.25;
    double BARREL_GRAVITY = 0.4;
    double MONKEY_GRAVITY = 0.4;
    double MARIO_TERMINAL_VELOCITY = 10.0;
    double BARREL_TERMINAL_VELOCITY = 5.0;
    double LADDER_TERMINAL_VELOCITY = 5.0;
    double DONKEY_TERMINAL_VELOCITY = 5.0;
    double MONKEY_TERMINAL_VELOCITY = 5.0;

    double getGravity();
    double getTerminalVelocity();
    void applyGravity(Platform[] platforms);
}
