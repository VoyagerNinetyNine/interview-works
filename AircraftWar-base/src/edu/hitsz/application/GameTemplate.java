package edu.hitsz.application;

import edu.hitsz.ObserverPattern.Publisher;
import edu.hitsz.aircraft.*;
import edu.hitsz.bullet.AbstractBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.prop.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * 游戏主面板，游戏启动
 *
 * @author hitsz
 */
public abstract class GameTemplate extends JPanel {

    private int backGroundTop = 0;

    /**
     * Scheduled 线程池，用于任务调度
     */
    private final ScheduledExecutorService executorService;

    /**
     * 时间间隔(ms)，控制刷新频率
     */
    protected final int timeInterval = 40;
    protected final Random r = new Random();

    private final HeroAircraft heroAircraft;
    private final List<AbstractBullet> heroAbstractBullets;
    private final List<AbstractBullet> enemyAbstractBullets;
    private final List<FireSupplyProp> fireProps;
    private final List<BloodSupplyProp> bloodProps;
    private final List<BombSupplyProp> bombProps;
    protected final List<MobEnemy> mobEnemy;
    protected final List<EliteEnemy> eliteEnemy;
    protected final List<BossEnemy> bossEnemy;
    private final BloodSupplyPropFactory bloodPropFactory = new BloodSupplyPropFactory();
    private final BombSupplyPropFactory bombPropFactory = new BombSupplyPropFactory();
    private final FireSupplyPropFactory firePropFactory = new FireSupplyPropFactory();
    protected final BossEnemyFactory bossEnemyFactory = new BossEnemyFactory();
    protected final MobEnemyFactory mobEnemyFactory = new MobEnemyFactory();
    protected final EliteEnemyFactory eliteEnemyFactory = new EliteEnemyFactory();

    protected int enemyMaxNumber = 5;

    private boolean gameOverFlag = false;
    protected static int score = 0;
    protected int time = 0;
    protected int bossAppearScore = 0;
    private int cycleTime = 0;
    public static String path;

    private int gameBegin ;
    private int heroBulletHit = 0;
    private int bombExplode = 0;
    private int heroGetSupply = 0;
    private int gameOver = 0;
    protected int bossAppeared = 0;
    private int heroShoot = 0;
    private int bossBgmOn = 0;
    public int actioningFirePropNumber = 0;
    private final MusicThread gameBeginThread = new MusicThread("src/videos/bgm.wav");
    private final MusicThread heroBulletHitThread = new MusicThread("src/videos/bullet_hit.wav");
    private final MusicThread bombExplodeThread = new MusicThread("src/videos/bomb_explosion.wav");
    private final MusicThread heroGetSupplyThread = new MusicThread("src/videos/get_supply.wav");
    private final MusicThread gameOverThread = new MusicThread("src/videos/game_over.wav");
    protected final MusicThread bossOnStageThread = new MusicThread("src/videos/bgm_boss.wav");
    private final MusicThread heroShootThread = new MusicThread("src/videos/bullet.wav");

    public static Publisher publisher;
    /**
     * 周期（ms)
     * 指示子弹的发射、敌机的产生频率
     */
    public int cycleDuration = 600;
    public int eliteGeneratePossibility = 20;

    protected int bossThreshold = 200;

    public GameTemplate() {
        heroAircraft = HeroAircraft.getInstance();

        mobEnemy = new LinkedList<>();
        heroAbstractBullets = new LinkedList<>();
        enemyAbstractBullets = new LinkedList<>();
        fireProps = new LinkedList<>();
        bloodProps = new LinkedList<>();
        bombProps = new LinkedList<>();
        eliteEnemy = new LinkedList<>();
        bossEnemy = new LinkedList<>();

        //Scheduled 线程池，用于定时任务调度
        ThreadFactory threadFactory = new SimpleThreadFactory();
        executorService = new ScheduledThreadPoolExecutor(10,threadFactory);

        //启动英雄机鼠标监听
        new HeroController(this, heroAircraft);
        gameBegin = 1;

    }

    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public final void action() {

        // 定时任务：绘制、对象产生、碰撞判定、击毁及结束判定
        Runnable task = () -> {

            time += timeInterval;
            upgradeDifficulty();

            // 周期性执行（控制频率）
            if (timeCountAndNewCycleJudge()) {
                System.out.println(time);
                // 新敌机产生
                if (mobEnemy.size()+ eliteEnemy.size() < enemyMaxNumber) {
                    if( r.nextInt(100) < eliteGeneratePossibility){
                        eliteEnemy.add(eliteEnemyFactory.createEnemyAircraft());
                    }
                    else {
                        mobEnemy.add(mobEnemyFactory.createEnemyAircraft());
                    }
                }
                if(score - bossAppearScore >= bossThreshold && bossEnemy.size()==0){
                    bossGenerate();
                }
                // 飞机射出子弹
                shootAction();
            }

            // 子弹移动
            bulletsMoveAction();

            //道具移动
            propMoveAction();

            // 飞机移动
            aircraftsMoveAction();

            // 撞击检测
            crashCheckAction();

            // 后处理
            postProcessAction();

            //每个时刻重绘界面
            repaint();
            if(heroAircraft.getHp() <= 0){
                gameOver = 1;
            }
            playMusic();

            // 游戏结束检查
            if (heroAircraft.getHp() <= 0) {
                // 游戏结束
                executorService.shutdown();
                gameOverFlag = true;
                gameBeginThread.interruptBgm();
                bossOnStageThread.interruptBgm();
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
                System.out.println("Game Over!");
                try {
                    displayRank(dateFormat.format(date));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        };

        /*
         * 以固定延迟时间进行执行
         * 本次任务执行完成后，需要延迟设定的延迟时间，才会执行新的任务
         */
        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);

    }

    //***********************
    //      Action 各部分
    //***********************

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private void shootAction() {
        // TODO 敌机射击
        for(EliteEnemy elite : eliteEnemy){
            enemyAbstractBullets.addAll(elite.shoot());
        }
        for(BossEnemy boss : bossEnemy) {
            enemyAbstractBullets.addAll(boss.shoot());
        }
        // 英雄射击
        heroAbstractBullets.addAll(heroAircraft.shoot());
        heroShoot++;
    }

    private void bulletsMoveAction() {
        for (AbstractBullet abstractBullet : heroAbstractBullets) {
            abstractBullet.forward();
        }
        for (AbstractBullet abstractBullet : enemyAbstractBullets) {
            abstractBullet.forward();
        }
    }

    private void propMoveAction(){
        for (FireSupplyProp prop : fireProps){
            prop.forward();
        }
        for (BombSupplyProp prop : bombProps){
            prop.forward();
        }
        for (BloodSupplyProp prop : bloodProps){
            prop.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (MobEnemy mob : mobEnemy) {
            mob.move();
        }
        for(EliteEnemy elite : eliteEnemy){
            elite.move();
        }
        for (BossEnemy boss : bossEnemy) {
            boss.move();
        }
    }


    /**
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() {
        // TODO 敌机子弹攻击英雄
        for(AbstractBullet enemyAbstractBullet : enemyAbstractBullets){
            if(heroAircraft.crash(enemyAbstractBullet)){
                heroAircraft.decreaseHp(enemyAbstractBullet.getPower());
                enemyAbstractBullet.vanish();
            }
        }

        // 英雄子弹攻击敌机
        for (AbstractBullet abstractBullet : heroAbstractBullets) {
            if (abstractBullet.notValid()) {
                continue;
            }
            for (EliteEnemy elite : eliteEnemy) {
                if (elite.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (elite.crash(abstractBullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    elite.decreaseHp(abstractBullet.getPower());
                    abstractBullet.vanish();
                    if (elite.notValid()) {
                        // TODO 获得分数，产生道具补给
                        heroBulletHit++;
                        score += 10;
                        int a = r.nextInt(10);
                        if(a <= 2){
                            bloodProps.add(bloodPropFactory.createProp(elite.getLocationX(), elite.getLocationY(),0,4));
                        }
                        else if(a <= 5){
                            fireProps.add(firePropFactory.createProp(elite.getLocationX(), elite.getLocationY(),0,4));
                        }
                        else if(a <= 8){
                            bombProps.add(bombPropFactory.createProp(elite.getLocationX(), elite.getLocationY(),0,4));
                        }
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (elite.crash(heroAircraft) || heroAircraft.crash(elite)) {
                    elite.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
            for(MobEnemy mob : mobEnemy){
                if(mob.notValid()){
                    continue;
                }
                if(mob.crash(abstractBullet)){
                    mob.decreaseHp(abstractBullet.getPower());
                    abstractBullet.vanish();
                    if(mob.notValid()){
                        score += 10;
                        heroBulletHit++;
                    }
                }
                if (mob.crash(heroAircraft) || heroAircraft.crash(mob)){
                    mob.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
            for(BossEnemy boss : bossEnemy) {
                if (boss.notValid()) {
                    bossAppearScore = score;
                    continue;
                }
                if (boss.crash(abstractBullet)) {
                    boss.decreaseHp(abstractBullet.getPower());
                    abstractBullet.vanish();
                    if (boss.notValid()) {
                        score += 30;
                        bossOnStageThread.interruptBgm();
                        if(r.nextInt(2)==1){
                            bombProps.add(bombPropFactory.createProp(boss.getLocationX(), boss.getLocationY(),0,4));
                        }
                    }
                }
                if (boss.crash(heroAircraft) || heroAircraft.crash(boss)) {
                    boss.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // Todo: 我方获得道具，道具生效
        for(FireSupplyProp prop : fireProps){
            if(prop.crash(heroAircraft)){
                prop.vanish();
                heroGetSupply++;
                actioningFirePropNumber ++;
                prop.action();
                Runnable r = ()->{
                    actioningFirePropNumber --;
                    if(actioningFirePropNumber == 0){
                        prop.disAction();
                    }
                };
                executorService.schedule(r,10,TimeUnit.SECONDS);
            }
        }
        for(BombSupplyProp prop : bombProps){
            if(prop.crash(heroAircraft)){
                publisher = new Publisher();
                publisher.addSubscribers(mobEnemy);
                publisher.addSubscribers(eliteEnemy);
                publisher.addSubscribers(enemyAbstractBullets);
                prop.action();
                bombExplode++;
                prop.vanish();
            }
        }
        for(BloodSupplyProp prop : bloodProps){
            if(prop.crash(heroAircraft)){
                prop.action();
                prop.vanish();
                heroGetSupply++;
            }
        }
    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 检查英雄机生存
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    private void postProcessAction() {
        enemyAbstractBullets.removeIf(AbstractFlyingObject::notValid);
        heroAbstractBullets.removeIf(AbstractFlyingObject::notValid);
        mobEnemy.removeIf(AbstractFlyingObject::notValid);
        fireProps.removeIf(AbstractFlyingObject::notValid);
        bloodProps.removeIf(AbstractFlyingObject::notValid);
        bombProps.removeIf(AbstractFlyingObject::notValid);
        eliteEnemy.removeIf(AbstractFlyingObject::notValid);
        bossEnemy.removeIf(BossEnemy::notValid);
    }


    //***********************
    //      Paint 各部分
    //***********************

    /**
     * 重写paint方法
     * 通过重复调用paint方法，实现游戏动画
     *
     * @param  g
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // 绘制背景,图片滚动
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop, null);
        this.backGroundTop += 1;
        if (this.backGroundTop == Main.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
        }

        // 先绘制子弹，后绘制飞机
        // 这样子弹显示在飞机的下层
        paintImageWithPositionRevised(g, enemyAbstractBullets);
        paintImageWithPositionRevised(g, heroAbstractBullets);

        paintImageWithPositionRevised(g, mobEnemy);
        paintImageWithPositionRevised(g, eliteEnemy);
        paintImageWithPositionRevised(g, fireProps);
        paintImageWithPositionRevised(g, bombProps);
        paintImageWithPositionRevised(g, bloodProps);
        paintImageWithPositionRevised(g, bossEnemy);

        g.drawImage(ImageManager.HERO_IMAGE, heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);

        //绘制得分和生命值
        paintScoreAndLife(g);

    }

    private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            BufferedImage image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, null);
        }
    }

    private void paintScoreAndLife(Graphics g) {
        int x = 10;
        int y = 25;
        g.setColor(new Color(16711680));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE:" + score, x, y);
        y = y + 20;
        g.drawString("LIFE:" + this.heroAircraft.getHp(), x, y);
    }

    public abstract void displayRank(String date) throws InterruptedException;

    public final void playMusic(){
        if(Objects.equals(Main.sound, "开")){
            if(gameBegin > 0){
                executorService.scheduleAtFixedRate(gameBeginThread,0,10,TimeUnit.MILLISECONDS);
                gameBegin = 0;
            }
            if(gameOver > 0){
                executorService.execute(gameOverThread);
                gameOver = 0;
            }
            if(heroBulletHit > 0){
                for(int i = 0 ; i < heroBulletHit ; i++) {
                    executorService.execute(heroBulletHitThread);
                }
                heroBulletHit = 0;
            }
            if(bombExplode > 0){
                for(int i = 0 ; i < bombExplode ; i++) {
                    executorService.execute(bombExplodeThread);
                }
                bombExplode = 0;
            }
            if(heroGetSupply > 0){
                for(int i = 0 ; i < heroGetSupply ; i++){
                    executorService.execute(heroGetSupplyThread);
                }
                heroGetSupply = 0;
            }
            if(bossAppeared == 1){
                if(bossBgmOn == 0) {
                    executorService.scheduleAtFixedRate(bossOnStageThread, 0, 100, TimeUnit.MILLISECONDS);
                    bossBgmOn = 1;
                }
            }
            if(heroShoot > 0){
                for(int i = 0; i<heroShoot ; i++) {
                    executorService.execute(heroShootThread);
                }
                heroShoot = 0;
            }
        }
    }

    public static void addScore(int number){
        score += number;
    }

    public abstract void bossGenerate();

    public abstract void upgradeDifficulty();
}