import bagel.Font;
import bagel.Input;
import bagel.Keys;

import java.util.ArrayList;
import java.util.Properties;

public class Level2 extends GamePlayScreen {
    private final Properties GAME_PROPS;
    private int currLevel;

    private final int BULLET_DISPLAY_DIFF_Y = 30;
    private static final int MONKEY_SCORE = 100;

    private final boolean INTELLMONKEY = true;
    private final boolean NORMMONKEY = false;

    private static final String BLT_MESSAGE = "BULLET ";

    private int donkeyHP = 5;
    // New game objects for Level2
    private int bulletCount = 0;
    private Blaster[] blasters;   // Array of blaster in level2
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Banana> bananas = new ArrayList<>();
    private Monkey[] monkeys;
    private final ArrayList<Monkey> allMonkeys = new ArrayList<>();

    private final static int BANANACD = 300;

    public Level2(Properties gameProps, int currLevel, int startedScore) {
        super(gameProps, currLevel, startedScore);
        this.GAME_PROPS = gameProps;
        this.currLevel = currLevel;
        initializeGameObjects2();
    }

    private void bulletHit(){
        // Update all bullets every frame
        ArrayList<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet bullet : bullets) {
            bullet.update(mario);
            if (!bullet.isActive()) {
                bulletsToRemove.add(bullet);
            }
            if (bullet.isCollide(donkey)){
                donkeyHP--;
                setDonkeyHP(donkeyHP);
                bullet.setActive(false);
            }
            for (Monkey monkey: allMonkeys){
                if (monkey.isAlive()){
                    if (bullet.isCollide(monkey)){
                        monkey.kill();
                        startedScore += MONKEY_SCORE;
                        bullet.setActive(false);
                    }
                }
            }
        }
        bullets.removeAll(bulletsToRemove);
    }

    private void bananaHit(){
        ArrayList<Banana> bananasToRemove = new ArrayList<>();
        for (Banana banana : bananas) {
            for (Monkey monkey : allMonkeys){
                if (monkey instanceof IntelliMonkey){
                    banana.update((IntelliMonkey) monkey);
                    if (!banana.isActive()) {
                        bananasToRemove.add(banana);
                    }
                    if (banana.isCollide(mario)){
                        banana.setActive(false);
                        isGameOver = true;
                    }
                }
            }
        }
        bananas.removeAll(bananasToRemove);
    }

    private void marioVsMonkey(){
        for (Monkey monkey: allMonkeys) {
            if (mario.isCollide(monkey)) {
                if (mario.holdHammer()){
                    monkey.kill();
                    startedScore += MONKEY_SCORE;
                }
                else{
                    isGameOver = true;
                }
            }
        }
    }

    @Override
    public void updateExtra(Input input) {
        for (Blaster blaster : blasters) {
            if (!blaster.isCollected()){
                blaster.draw();
            }
        }
        shootBullet(input);
        shootBanana();
        bulletHit();
        bananaHit();
        marioVsMonkey();
        donkeyBeShot();
        for (Monkey monkey: allMonkeys){
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
    public void disPlayBullet(Font STATUS_FONT, int DKH_X, int DKH_Y) {
        STATUS_FONT.drawString(BLT_MESSAGE + mario.getBulletCount(),
                DKH_X, DKH_Y + BULLET_DISPLAY_DIFF_Y);
    }

    private void shootBanana(){
        for (Monkey monkey: allMonkeys){
            if ((monkey instanceof IntelliMonkey) && monkey.isAlive()){
                int timeCount = ((IntelliMonkey) monkey).getTimeCount();
                if (timeCount == BANANACD || timeCount == 0){
                    bananas.add(new Banana(monkey.x, monkey.y));
                    timeCount = 0;
                    ((IntelliMonkey) monkey).setTimeCount(timeCount);
                }
                timeCount++;
                ((IntelliMonkey) monkey).setTimeCount(timeCount);
                for (Banana banana: bananas){
                    banana.update((IntelliMonkey) monkey);
                }
            }
        }
    }

    private void shootBullet(Input input){
        bulletCount =  mario.getBulletCount();
        if (mario.holdBlaster() && bulletCount != 0 && input.wasPressed(Keys.S)){
            bulletCount--;
            mario.setBulletCount(bulletCount);
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

    public Blaster[] getBlasters() {
        return blasters;
    }

    private void loadMonkeys(boolean isIntell){
        String baseKey = (isIntell ? "intelligent": "normal") + "Monkey.level2.";
        int count = Integer.parseInt(GAME_PROPS.getProperty(baseKey + "count"));
        monkeys = new Monkey[count];
        for (int i = 1; i <= count; i++){
            String[] info = GAME_PROPS.getProperty(baseKey + i).split(";");
            String[] coords = info[0].split(",");
            boolean isFacingRight = (info[1].equals("right")) ? true : false;
            int lenWalkPattern = (info[2].split(",")).length;
            int[] walkPattern = new int[lenWalkPattern];
            for (int j = 0; j < lenWalkPattern; j++){
                walkPattern[j] = Integer.parseInt(info[2].split(",")[j]);
            }
            Monkey monkey = isIntell ? (new IntelliMonkey(Double.parseDouble(coords[0]),
                    Double.parseDouble(coords[1]), isFacingRight, lenWalkPattern, walkPattern)) :
                    (new Monkey(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]),
                            isFacingRight, lenWalkPattern, walkPattern));
            allMonkeys.add(monkey);
        }
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

        loadMonkeys(INTELLMONKEY);
        loadMonkeys(NORMMONKEY);

    }

}
