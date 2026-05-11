package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.application.ImageManager;
import com.example.aircraftwar.application.MainActivity;

public class BossEnemyFactory implements EnemyAircraftFactory{
    public int hp = 210;
    @Override
    public BossEnemy createEnemyAircraft(){
        int locationX = (int) ( Math.random() * (MainActivity.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth()));
        int locationY = (int) (Math.random() * MainActivity.WINDOW_HEIGHT * 0.2);
        int speedX = 10;
        int speedY = 0;
        return new BossEnemy(locationX, locationY, speedX, speedY, hp);
    }

    public void addBossHp(){
        hp += 15;
    }

    public int getHp(){
        return hp;
    }
}
