package edu.hitsz.aircraft;

import edu.hitsz.bullet.AbstractBullet;
import edu.hitsz.bullet.EnemyAbstractBullet;
import edu.hitsz.bullet.HeroAbstractBullet;

import java.util.LinkedList;
import java.util.List;

public class StraightShoot implements ShootStrategy{
    public int aircraftLocationX;
    public int aircraftLocationY;
    public int aircraftSpeedY;
    public int direction;
    public int power;
    public int shootNum;

    public StraightShoot(int aircraftLocationX, int aircraftLocationY, int aircraftSpeedY, int power, int direction, int shootNum){
        this.aircraftLocationX = aircraftLocationX;
        this.aircraftLocationY = aircraftLocationY;
        this.aircraftSpeedY = aircraftSpeedY;
        this.power = power;
        this.direction = direction;
        this.shootNum = shootNum;
    }
    @Override
    public List<AbstractBullet> shoot() {
        List<AbstractBullet> res = new LinkedList<>();
        int x = aircraftLocationX;
        int y = aircraftLocationY + direction*2;
        int speedX = 0;
        int speedY = aircraftSpeedY + direction*5;
        AbstractBullet abstractBullet;
        for(int i=0; i<shootNum; i++){
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            if(direction == -1) {
                abstractBullet = new HeroAbstractBullet(x + (i * 2 - shootNum + 1) * 10, y, speedX, speedY, power);
            }
            else{
                abstractBullet = new EnemyAbstractBullet(x + (i * 2 - shootNum + 1) * 10, y, speedX, speedY, power);
            }
            res.add(abstractBullet);
        }
        return res;
    }

    @Override
    public void setLocation(int aircraftLocationX, int aircraftLocationY) {
        this.aircraftLocationX = aircraftLocationX;
        this.aircraftLocationY = aircraftLocationY;
    }
}
