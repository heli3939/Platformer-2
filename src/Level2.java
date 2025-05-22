import bagel.Font;
import bagel.Input;
import bagel.Keys;

import java.util.ArrayList;
import java.util.Properties;

/**
 * represent level2 of the game, extends generic GamePlayScreen
 * include extra game elements and logics built above generic superclass
 */
public class Level2 extends GamePlayScreen {
    private final Properties GAME_PROPS;

    private final int BULLET_DISPLAY_DIFF_Y = 30; // difference between y-coord of bullet and donkey hp
    private static final int MONKEY_SCORE = 100; // score for kill a monkey

    private final boolean INTELLMONKEY = true; // intelegent monkey
    private final boolean NORMMONKEY = false; // normal monkey

    private static final String BLT_MESSAGE = "BULLET "; //

    private int currLevel; // record current level number
    private int donkeyHP = 5; // record current donkey HP

    // New game objects for Level 2
    private int bulletCount = 0; // record current number of bullets own
    private Blaster[] blasters;   // Array of blaster in level2
    private ArrayList<Bullet> bullets = new ArrayList<>(); // array list of bullets, can be shot by mario with blaster
    private ArrayList<Banana> bananas = new ArrayList<>(); // array list of bullets, can be shot by alive intell monkey
    private Monkey[] monkeys; // used to temporarily store normal and intell monkeys seperatedly
    private final ArrayList<Monkey> allMonkeys = new ArrayList<>(); // array list to store all monkeys in level2

    private final static int BANANACD = 300; // banana can be shot with interval of 300 framse (5 sec)

    /**
     * Constructs the gameplay screen for level2, loading resources and initializing game objects.
     * @param gameProps property file with game setting
     * @param currLevel current level number
     * @param startedScore initial score of game
     */
    public Level2(Properties gameProps, int currLevel, int startedScore) {
        super(gameProps, currLevel, startedScore);
        this.GAME_PROPS = gameProps;
        this.currLevel = currLevel;
        // initialize game entities (besides blasters) in level2
        initializeGameObjects2();
    }

    private void bulletHit(){
        // Update all bullets every frame
        ArrayList<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet bullet : bullets) {
            bullet.update(mario);
            // remove inactive bullet
            if (!bullet.isActive()) {
                bulletsToRemove.add(bullet);
            }
            // -1 hp for donkey health every time shoot and inactivate the bullet
            if (bullet.isCollide(donkey)){
                donkeyHP--;
                setDonkeyHP(donkeyHP);
                bullet.setActive(false);
            }
            // kill monkey if active bullet touches monkey and inactivate the bullet
            for (Monkey monkey: allMonkeys){
                if (monkey.isAlive()){
                    if (bullet.isCollide(monkey)){
                        monkey.kill();
                        // score gain for shot a monkey
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
                // only update banana for intell monkey
                if (monkey instanceof IntelliMonkey){
                    banana.update((IntelliMonkey) monkey);
                    if (!banana.isActive()) {
                        bananasToRemove.add(banana);
                    }
                    // inactivate banana after hit mario, loss the game
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
                // mario kill monkey by hammer
                if (mario.holdHammer()){
                    monkey.kill();
                    startedScore += MONKEY_SCORE;
                } // mario die, game loss if they touch without hammer
                else{
                    isGameOver = true;
                }
            }
        }
    }

    /**
     * update extra game elements and logics in level2
     * @param input input from keyboard
     */
    @Override
    public void updateExtra(Input input) {
        // load uncollected blasters
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
        // load all monkeys
        for (Monkey monkey: allMonkeys){
            monkey.draw();
            monkey.update(platforms);
        }
    }

    private void donkeyBeShot(){
        for (Bullet bullet: bullets){
            // donkey -1 hp for being hit by bullet
            if (bullet.isCollide(donkey)){
                donkeyHP = getDonkeyHP();
                donkeyHP--;
                bullet.setActive(false);
                setDonkeyHP(donkeyHP);
            }
        }
    }

    /**
     * display bullet info for level 2
     * @param STATUS_FONT font for display text
     * @param DKH_X  x-coordinate to display the bullet info
     * @param DKH_Y  y-coordinate to display the bullet info
     */
    @Override
    public void displayBullet(Font STATUS_FONT, int DKH_X, int DKH_Y) {
        // display bullet info
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
        // handle shot action by mario with blaster
        if (mario.holdBlaster() && bulletCount != 0 && input.wasPressed(Keys.S)){
            bulletCount--;
            mario.setBulletCount(bulletCount);
            bullets.add(new Bullet(mario.x, mario.y));
            // update bullet when there're reamining
            if (!bullets.isEmpty()){
                for (Bullet bullet: bullets){
                    bullet.update(mario);
                }
            }
        }
        // mario not hold blaster when no bullet
        if (bulletCount == 0){
            mario.setHasBlaster(false);
        }
    }

    /**
     * get an array of blasters in level2
     * @return an array of blasters
     */
    public Blaster[] getBlasters() {
        return blasters;
    }

    private void loadMonkeys(boolean isIntell){
        // different basekey for intell and normal monkeys
        String baseKey = (isIntell ? "intelligent": "normal") + "Monkey.level2.";
        // readin number of each type of monkey
        int count = Integer.parseInt(GAME_PROPS.getProperty(baseKey + "count"));
        monkeys = new Monkey[count];
        // readin info for ith monkey (from 1)
        for (int i = 1; i <= count; i++){
            String[] info = GAME_PROPS.getProperty(baseKey + i).split(";");
            String[] coords = info[0].split(","); // start position of monkey
            boolean isFacingRight = (info[1].equals("right")) ? true : false; // direction of monkey
            int lenWalkPattern = (info[2].split(",")).length; // length of walking pattern
            int[] walkPattern = new int[lenWalkPattern];
            // used to store a sequence of distance (in px), reverse direction and walk for next distane
            // once reach the current direction (in loop)
            for (int j = 0; j < lenWalkPattern; j++){
                walkPattern[j] = Integer.parseInt(info[2].split(",")[j]);
            }
            // create a specific type of monkey by readin info
            Monkey monkey = isIntell ? (new IntelliMonkey(Double.parseDouble(coords[0]),
                    Double.parseDouble(coords[1]), isFacingRight, lenWalkPattern, walkPattern)) :
                    (new Monkey(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]),
                            isFacingRight, lenWalkPattern, walkPattern));
            allMonkeys.add(monkey);
        }
    }

    private void initializeGameObjects2() {
        // Load blasters
        this.blasters = loadEntities(GAME_PROPS, "blaster", currLevel, new EntityFactory<Blaster>() {
            @Override
            public Blaster create(String[] parts) {
                String[] coords = parts[0].split(",");
                return new Blaster(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
            }
        }).toArray(new Blaster[0]);
        // load intell monkeys
        loadMonkeys(INTELLMONKEY);
        // load normal monkeys
        loadMonkeys(NORMMONKEY);
    }
}
