package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.bullet.AbstractBullet;
import com.example.aircraftwar.bullet.EnemyAbstractBullet;
import com.example.aircraftwar.bullet.HeroAbstractBullet;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class FloatShoot implements ShootStrategy{
    public int aircraftLocationX, aircraftLocationY, bulletSpeedX, aircraftSpeedY, direction, power, shootNum;

    public FloatShoot(int aircraftLocationX, int aircraftLocationY, int bulletSpeedX, int aircraftSpeedY, int power, int direction, int shootNum){
        this.aircraftLocationX = aircraftLocationX;
        this.aircraftLocationY = aircraftLocationY;
        this.bulletSpeedX = bulletSpeedX;
        this.aircraftSpeedY = aircraftSpeedY;
        this.power = power;
        this.direction = direction;
        this.shootNum = shootNum;
    }

    @Override
    public List<AbstractBullet> shoot(){
        List<AbstractBullet> res = new LinkedList<>();
        int x = aircraftLocationX;
        int y = aircraftLocationY + direction*2;
        int speedX = bulletSpeedX;
        int speedY = aircraftSpeedY + direction*15;
        AbstractBullet abstractBullet;
        Random r = new Random();
        for(int i=0; i<shootNum; i++){
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            if(direction == -1) {
                abstractBullet = new HeroAbstractBullet(x + (i * 2 - shootNum + 1) * 10, y, speedX*(i-1), speedY, power);
            }
            else{
                abstractBullet = new EnemyAbstractBullet(x + (i * 2 - shootNum + 1) * 10, y, speedX*(i-1), speedY, power);
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
