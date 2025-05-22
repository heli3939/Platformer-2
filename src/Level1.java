import bagel.Font;
import bagel.Input;

import java.util.Properties;

public class Level1 extends GamePlayScreen{
    public Level1(Properties gameProps, int currLevel, int startedScore) {
        super(gameProps, currLevel, startedScore);
    }

    @Override
    public void updateExtra(Input input) {
        // no extra update for level 1
    }

    @Override
    public void displayBullet(Font STATUS_FONT, int DKH_X, int DKH_Y) {
        // no bullet info for level 1
    }
}
