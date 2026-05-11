package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

import java.util.Random;

public class EliteEnemyFactory implements EnemyAircraftFactory{
    public int speedY = 8;
    public int hp = 50;
    @Override
    public EliteEnemy createEnemyAircraft(){
        int locationX = (int) ( Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth()));
        int locationY = (int) (Math.random() * Main.WINDOW_HEIGHT * 0.2);
        int speedX;
        Random r = new Random();
        int a = r.nextInt(3);
        if(a==0){
            speedX = 0;
        }
        else if(a==1){
            speedX = 5;
        }
        else{
            speedX = -5;
        }
        return new EliteEnemy(locationX, locationY, speedX, speedY, hp);
    }

    public void addEliteSpeedY(int y){
        if(speedY < 20) {
            speedY += y;
        }
    }

    public void addEliteHp(int blood){
        hp += blood;
    }

}
