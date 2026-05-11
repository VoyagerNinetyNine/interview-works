package com.example.aircraftwar.bullet;

import com.example.aircraftwar.ObserverPattern.Subscribers;
import com.example.aircraftwar.application.MainActivity;
import com.example.aircraftwar.basic.AbstractFlyingObject;

/**
 * 子弹类。
 * 也可以考虑不同类型的子弹
 *
 * @author hitsz
 */
public abstract class AbstractBullet extends AbstractFlyingObject implements Subscribers {

    private int power = 10;

    public AbstractBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY);
        this.power = power;
    }

    @Override
    public void forward() {
        super.forward();

        // 判定 y 轴出界
        if (speedY > 0 && locationY >= MainActivity.WINDOW_HEIGHT ) {
            // 向下飞行出界
            vanish();
        }else if (locationY <= 0){
            // 向上飞行出界
            vanish();
        }
    }

    public int getPower() {
        return power;
    }

    @Override
    public void update(){
        super.vanish();
    }
}
