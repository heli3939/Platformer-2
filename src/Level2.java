import bagel.Input;
import java.util.Properties;

public class Level2 extends GamePlayScreen {
    private final Properties GAME_PROPS;
    private int currLevel;
    // New game objects for Level2
    private int currFrame = 0;
    private static Blaster[] blasters;   // Array of blaster in level2


    public Level2(Properties gameProps, int currLevel, int startedScore) {
        super(gameProps, currLevel, startedScore);
        this.GAME_PROPS = gameProps;
        this.currLevel = currLevel;
        System.out.println("LEVEL2");
        initializeGameObjects2();
    }

    @Override
    protected void updateExtra(Input input) {
        for (Blaster blaster : blasters) {
            if (!blaster.isCollected()){
                blaster.draw();
            }
        }
    }

    public static Blaster[] getBlasters() {
        return blasters;
    }

    private void initializeGameObjects2() {
        // 1. Load blasters
        this.blasters = loadEntities(GAME_PROPS, "blaster", currLevel, new EntityFactory<Blaster>() {
            @Override
            public Blaster create(String[] parts) {
                String[] coords = parts[0].split(",");
                return new Blaster(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
            }
        }).toArray(new Blaster[0]);
    }


}
