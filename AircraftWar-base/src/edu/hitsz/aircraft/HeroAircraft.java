package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.AbstractBullet;

import java.util.List;

/**
 * 英雄飞机，游戏玩家操控
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft {

    private static HeroAircraft instance = null;
    private ShootStrategy strategy = new StraightShoot(this.getLocationX(), this.getLocationY(), this.getSpeedY(), 30, -1, 1);;

    /**
     * @param locationX 英雄机位置x坐标
     * @param locationY 英雄机位置y坐标
     * @param speedX 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param speedY 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param hp    初始生命值
     */
    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    @Override
    /**
     * 通过射击产生子弹
     * @return 射击出的子弹List
     */
    public List<AbstractBullet> shoot() {
        strategy.setLocation(this.locationX,this.locationY);
        shootContext.setStrategy(strategy);
        return shootContext.executeStrategy();
    }

    public void setStrategyKind(ShootStrategy strategyKind){
        this.strategy = strategyKind;
    }

    public static HeroAircraft getInstance(){
        if(instance == null){
            synchronized(HeroAircraft.class){
                instance = new HeroAircraft(Main.WINDOW_WIDTH / 2,
                        Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight() ,
                        0, 0, 1000);
            }
        }
        return instance;
    }

}
