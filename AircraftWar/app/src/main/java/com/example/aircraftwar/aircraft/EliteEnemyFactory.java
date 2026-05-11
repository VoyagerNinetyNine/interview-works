package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.application.ImageManager;
import com.example.aircraftwar.application.MainActivity;

import java.util.Random;

public class EliteEnemyFactory implements EnemyAircraftFactory{
    public int speedY = 24;
    public int hp = 50;
    @Override
    public EliteEnemy createEnemyAircraft(){
        int locationX = (int) ( Math.random() * (MainActivity.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth()));
        int locationY = (int) (Math.random() * MainActivity.WINDOW_HEIGHT * 0.2);
        int speedX;
        Random r = new Random();
        int a = r.nextInt(3);
        if(a==0){
            speedX = 0;
        }
        else if(a==1){
            speedX = 15;
        }
        else{
            speedX = -15;
        }
        return new EliteEnemy(locationX, locationY, speedX, speedY, hp);
    }

    public void addEliteSpeedY(int y){
        if(speedY < 60) {
            speedY += y;
        }
    }

    public void addEliteHp(int blood){
        hp += blood;
    }

}
