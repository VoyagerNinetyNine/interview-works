package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.AbstractBullet;
import edu.hitsz.bullet.EnemyAbstractBullet;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BossEnemy extends AbstractAircraft implements EnemyAction{

    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp){
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public List<AbstractBullet> shoot(){
        FloatShoot floatShoot = new FloatShoot(this.getLocationX(), this.getLocationY(), 5, this.getSpeedY(), 25, 1, 3);
        shootContext.setStrategy(floatShoot);
        return shootContext.executeStrategy();
    }

    @Override
    public void move(){
        locationX += speedX;
        locationY += speedY;
        if (locationX <= 0 || locationX >= Main.WINDOW_WIDTH) {
            // 横向超出边界后反向
            speedX = -speedX;
        }
    }
}
