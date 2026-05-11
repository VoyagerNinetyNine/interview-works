package edu.hitsz.aircraft;

import edu.hitsz.ObserverPattern.Subscribers;
import edu.hitsz.application.GameTemplate;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.AbstractBullet;

import java.util.List;

public class EliteEnemy extends AbstractAircraft implements EnemyAction, Subscribers {

    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public void move() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= Main.WINDOW_HEIGHT ) {
            vanish();
        }
    }

    @Override
    public List<AbstractBullet> shoot() {
        StraightShoot straightShoot = new StraightShoot(this.getLocationX(), this.getLocationY(), this.getSpeedY(), 25, 1, 1);
        shootContext.setStrategy(straightShoot);
        return shootContext.executeStrategy();
    }

    @Override
    public void update(){
        if(!notValid()) {
            super.vanish();
            GameTemplate.addScore(10);
        }
    }
}
