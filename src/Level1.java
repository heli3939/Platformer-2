import bagel.Input;

import java.util.Properties;

public class Level1 extends GamePlayScreen{
    public Level1(Properties gameProps, int currLevel, int startedScore) {
        super(gameProps, currLevel, startedScore);
        System.out.println("LEVEL1");
    }

    @Override
    protected void updateExtra(Input input) {

    }
}
