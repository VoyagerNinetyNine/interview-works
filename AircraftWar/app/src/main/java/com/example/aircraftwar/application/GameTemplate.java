package com.example.aircraftwar.application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.aircraftwar.ObserverPattern.Publisher;
import com.example.aircraftwar.R;
import com.example.aircraftwar.aircraft.BossEnemy;
import com.example.aircraftwar.aircraft.BossEnemyFactory;
import com.example.aircraftwar.aircraft.EliteEnemy;
import com.example.aircraftwar.aircraft.EliteEnemyFactory;
import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.aircraft.MobEnemy;
import com.example.aircraftwar.aircraft.MobEnemyFactory;
import com.example.aircraftwar.basic.AbstractFlyingObject;
import com.example.aircraftwar.bullet.AbstractBullet;
import com.example.aircraftwar.prop.BloodSupplyProp;
import com.example.aircraftwar.prop.BloodSupplyPropFactory;
import com.example.aircraftwar.prop.BombSupplyProp;
import com.example.aircraftwar.prop.BombSupplyPropFactory;
import com.example.aircraftwar.prop.FireSupplyProp;
import com.example.aircraftwar.prop.FireSupplyPropFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 游戏主面板，游戏启动
 *
 * @author hitsz
 */
public abstract class GameTemplate extends SurfaceView implements SurfaceHolder.Callback,Runnable{

    private int backGroundTop = 0;
    private final ScheduledExecutorService executorService;
    protected final int timeInterval = 40;
    protected final Random r = new Random();
    protected final HeroAircraft heroAircraft;
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

    protected boolean gameOverFlag;
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
    protected int startBossBgm = 0;
    public int actioningFirePropNumber = 0;

    public static Publisher publisher;
    /**
     * 周期（ms)
     * 指示子弹的发射、敌机的产生频率
     */
    public int cycleDuration = 600;
    public int eliteGeneratePossibility = 30;

    protected int bossThreshold = 200;
    private final SurfaceHolder surfaceHolder;
    protected PlayMusic music;
    private MediaPlayer bgmplayer;
    private MediaPlayer bossbgmplayer;
    protected final Paint paint;
    private final Thread gameActioning;
    protected Handler handler;
    protected String datetime;
    protected int difficulity;
    protected MainActivity mainActivity;
    private int heroimagenumber = 1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public GameTemplate(Context context) {
        super(context);
        ImageManager.loadimg();
        heroAircraft = HeroAircraft.getInstance();
        heroAircraft.initialize();
        score = 0;
        gameOverFlag = false;
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
        mainActivity = (MainActivity) context;
        if(MainActivity.seIsChecked) {
            music = new PlayMusic(mainActivity);
            music.initSound();
            bgmplayer = MediaPlayer.create(context, R.raw.bgm);
            bossbgmplayer = MediaPlayer.create(context,R.raw.bgm_boss);
        }
        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);
        this.setFocusable(true);
        paint = new Paint();
        Typeface font = Typeface.create("Microsoft YaHei UI",Typeface.NORMAL);
        paint.setColor(Color.BLUE);
        paint.setTypeface(font);
        paint.setAlpha(160);
        paint.setTextSize(70);
        paint.setTypeface(Typeface.DEFAULT);
        paint.setStrokeWidth(12);
        gameActioning = new Thread(this);
        handler = new Handler(message -> {
            if(message.what == 1){
                mainActivity.setSavescoreView(score, path, datetime, difficulity);
                return false;
            }
            return true;
        });
    }

    /**
     * 游戏启动入口，执行游戏逻辑
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
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
            paint();
            if(heroAircraft.getHp() <= 0){
                gameOver = 1;
            }
            playMusic();

            // 游戏结束检查
            if (heroAircraft.getHp() <= 0) {
                // 游戏结束
                executorService.shutdown();
                gameOverFlag = true;
                if(MainActivity.seIsChecked) {
                    bgmplayer.stop();
                    bossbgmplayer.stop();
                    bgmplayer.release();
                    bossbgmplayer.release();
                }
                Date date = new Date();
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
                System.out.println("Game Over!");
                initalize();
                displayRank(dateFormat.format(date));
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
                            bloodProps.add(bloodPropFactory.createProp(elite.getLocationX(), elite.getLocationY(),0,14));
                        }
                        else if(a <= 5){
                            fireProps.add(firePropFactory.createProp(elite.getLocationX(), elite.getLocationY(),0,14));
                        }
                        else if(a <= 8){
                            bombProps.add(bombPropFactory.createProp(elite.getLocationX(), elite.getLocationY(),0,14));
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
                        if(MainActivity.seIsChecked) {
                            bossbgmplayer.pause();
                        }
                        if(r.nextInt(2)==1){
                            bombProps.add(bombPropFactory.createProp(boss.getLocationX(), boss.getLocationY(),0,14));
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
    @RequiresApi(api = Build.VERSION_CODES.N)
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
     */
    public void paint() {
        Canvas canvas = surfaceHolder.lockCanvas();

        // 绘制背景,图片滚动
        canvas.drawBitmap(ImageManager.BACKGROUND_IMAGE,0, this.backGroundTop - MainActivity.WINDOW_HEIGHT, null);
        canvas.drawBitmap(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop, null);
        this.backGroundTop += 1;
        if (this.backGroundTop == MainActivity.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
        }

        // 先绘制子弹，后绘制飞机
        // 这样子弹显示在飞机的下层
        paintImageWithPositionRevised(canvas, enemyAbstractBullets);
        paintImageWithPositionRevised(canvas, heroAbstractBullets);

        paintImageWithPositionRevised(canvas, mobEnemy);
        paintImageWithPositionRevised(canvas, eliteEnemy);
        paintImageWithPositionRevised(canvas, fireProps);
        paintImageWithPositionRevised(canvas, bombProps);
        paintImageWithPositionRevised(canvas, bloodProps);
        paintImageWithPositionRevised(canvas, bossEnemy);
        canvas.drawBitmap(ImageManager.HEROIMAGES.get(heroimagenumber - 1), heroAircraft.getLocationX() - (float)ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - (float)ImageManager.HERO_IMAGE.getHeight() / 2, null);

        //绘制得分和生命值
        paintScoreAndLife(canvas);
        surfaceHolder.unlockCanvasAndPost(canvas);
        if(heroimagenumber == MainActivity.heroImageNumber){
            heroimagenumber = 1;
        }
        else{
            heroimagenumber ++;
        }

    }

    private void paintImageWithPositionRevised(Canvas g, List<? extends AbstractFlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }
        for (AbstractFlyingObject object : objects) {
            Bitmap image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            g.drawBitmap(image, object.getLocationX() - (float)image.getWidth() / 2,
                    object.getLocationY() - (float)image.getHeight() / 2, null);
        }
    }

    public void paintScoreAndLife(Canvas g) {
        float x = 20;
        float y = 60;
        g.drawText("SCORE:" + score, x, y, paint);
        y = y + 60;
        g.drawText("LIFE:" + this.heroAircraft.getHp(), x, y, paint);
    }

    public abstract void displayRank(String date);

    public final void playMusic(){
        if(MainActivity.seIsChecked){
            if(gameBegin > 0){
                bgmplayer.setLooping(true);
                bgmplayer.start();
                gameBegin = 0;
            }
            if(gameOver > 0){
                music.playSound(4,0);
                gameOver = 0;
            }
            if(heroBulletHit > 0){
                for(int i = 0 ; i < heroBulletHit ; i++) {
                    music.playSound(3,0);
                }
                heroBulletHit = 0;
            }
            if(bombExplode > 0){
                for(int i = 0 ; i < bombExplode ; i++) {
                    music.playSound(1,0);
                }
                bombExplode = 0;
            }
            if(heroGetSupply > 0){
                for(int i = 0 ; i < heroGetSupply ; i++){
                    music.playSound(5,0);
                }
                heroGetSupply = 0;
            }
            if(bossAppeared == 1){
                if(startBossBgm == 1) {
                    bossbgmplayer.setLooping(true);
                    bossbgmplayer.start();
                    bossbgmplayer.seekTo(0);
                    startBossBgm = 0;
                }
            }
            if(heroShoot > 0){
                for(int i = 0; i<heroShoot ; i++) {
                    music.playSound(2,0);
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

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        gameActioning.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        gameOverFlag = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void run(){
        action();
    }

    public void initalize(){
        heroAircraft.initialize();
        mobEnemy.clear();
        eliteEnemy.clear();
        enemyAbstractBullets.clear();
        bossEnemy.clear();
    }
}
