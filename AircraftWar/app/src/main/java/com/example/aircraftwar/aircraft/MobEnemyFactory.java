package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.application.ImageManager;
import com.example.aircraftwar.application.MainActivity;

public class MobEnemyFactory implements EnemyAircraftFactory{
    public int speedY = 21;
    public int hp = 20;
    @Override
    public MobEnemy createEnemyAircraft(){
        int locationX = (int) ( Math.random() * (MainActivity.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth()));
        int locationY = (int) (Math.random() * MainActivity.WINDOW_HEIGHT * 0.2);
        int speedX = 0;
        return new MobEnemy(locationX, locationY, speedX, speedY, hp);
    }

    public void addMobSpeedY(int y){
        if(speedY < 60) {
            speedY += y;
        }
    }

    public void addMobHp(int blood){
        hp += blood;
    }
}
