import bagel.Font;
import bagel.Input;

import java.util.Properties;

    /**
     * represent level1 of the game, extends generic GamePlayScreen
     * no special game elements and logic required
     */
    public class Level1 extends GamePlayScreen{
    public Level1(Properties gameProps, int currLevel, int startedScore) {
        super(gameProps, currLevel, startedScore);
    }

    /**
     * no extra update for level 1
     * @param input input from keyboard
     */
    @Override
    public void updateExtra(Input input) {
    }

    /**
     * no bullet info for level 1
     * @param STATUS_FONT font for display text
     * @param DKH_X  x-coordinate to display the bullet info
     * @param DKH_Y  y-coordinate to display the bullet info
     */
    @Override
    public void displayBullet(Font STATUS_FONT, int DKH_X, int DKH_Y) {
    }
}
