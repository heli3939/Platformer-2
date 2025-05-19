import bagel.Font;
import bagel.Input;
import bagel.Keys;

import java.util.ArrayList;
import java.util.Properties;

public class Level2 extends GamePlayScreen {
    private final Properties GAME_PROPS;
    private int currLevel;

    private final int BULLET_DISPLAY_DIFF_Y = 30;
    private static final String BLT_MESSAGE = "BULLET ";

    private int donkeyHP = 5;
    // New game objects for Level2
    private int currFrame = 0;
    private int bulletCount = 0;
    protected  Blaster[] blasters;   // Array of blaster in level2
    private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    protected Monkey[] monkeys;


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
        shootBullet(input);

        // Update all bullets every frame
        ArrayList<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet bullet : bullets) {
            bullet.update(mario);
            if (!bullet.isActive()) {
                bulletsToRemove.add(bullet);
            }
        }
        bullets.removeAll(bulletsToRemove);
        donkeyBeShot();
        int i = 0;
        for (Monkey monkey: monkeys){
            monkey.draw();
            monkey.update(platforms);
        }
    }

    private void donkeyBeShot(){
        for (Bullet bullet: bullets){
            if (bullet.isCollide(donkey)){
                donkeyHP = getDonkeyHP();
                donkeyHP--;
                bullet.setActive(false);
                setDonkeyHP(donkeyHP);
            }
        }
    }

    @Override
    protected void disPlayBullet(Font STATUS_FONT, int DKH_X, int DKH_Y) {
        STATUS_FONT.drawString(BLT_MESSAGE + mario.getBulletCount(),
                DKH_X, DKH_Y + BULLET_DISPLAY_DIFF_Y);
    }


    private void shootBullet(Input input){
        bulletCount =  mario.getBulletCount();
        if (mario.holdBlaster() && bulletCount != 0 && input.wasPressed(Keys.S)){
            bulletCount--;
            mario.setBulletCount(bulletCount);
            System.out.println("SHOT");
            bullets.add(new Bullet(mario.x, mario.y));
            if (!bullets.isEmpty()){
                for (Bullet bullet: bullets){
                    bullet.update(mario);
                }
            }
        }
        if (bulletCount == 0){
            mario.setHasBlaster(false);
        }
    }

    protected Blaster[] getBlasters() {
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

        String nMonkeyBaseKey = "normalMonkey.level2.";
        int nMonkeyCount = Integer.parseInt(GAME_PROPS.getProperty(nMonkeyBaseKey + "count"));
        monkeys = new Monkey[nMonkeyCount];
        for (int i = 1; i <= nMonkeyCount; i++){
            String[] nMonkeyinfo = GAME_PROPS.getProperty(nMonkeyBaseKey + i).split(";");
            String[] coords = nMonkeyinfo[0].split(",");
            boolean isNMonkeyFacingRight = (nMonkeyinfo[1].equals("right")) ? true : false;
            int lenWalkPattern = (nMonkeyinfo[2].split(",")).length;
            int[] walkPattern = new int[lenWalkPattern];
            for (int j = 0; j < lenWalkPattern; j++){
                walkPattern[j] = Integer.parseInt(nMonkeyinfo[2].split(",")[j]);
            }
            monkeys[i - 1] = new Monkey(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]),
                    isNMonkeyFacingRight, lenWalkPattern, walkPattern);

        }
    }


}
