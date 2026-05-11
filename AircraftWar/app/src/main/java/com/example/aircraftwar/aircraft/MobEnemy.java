package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.ObserverPattern.Subscribers;
import com.example.aircraftwar.application.GameTemplate;
import com.example.aircraftwar.application.MainActivity;
import com.example.aircraftwar.bullet.AbstractBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 普通敌机
 * 不可射击
 *
 * @author hitsz
 */
public class MobEnemy extends AbstractAircraft implements EnemyAction, Subscribers {

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public void move() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= MainActivity.WINDOW_HEIGHT ) {
            vanish();
        }
    }

    @Override
    public List<AbstractBullet> shoot() {
        return new LinkedList<>();
    }

    @Override
    public void update(){
        if(!notValid()) {
            super.vanish();
            GameTemplate.addScore(10);
        }
    }
}
