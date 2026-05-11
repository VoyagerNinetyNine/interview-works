package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

public class BossEnemyFactory implements EnemyAircraftFactory{
    public int hp = 210;
    @Override
    public BossEnemy createEnemyAircraft(){
        int locationX = (int) ( Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth()));
        int locationY = (int) (Math.random() * Main.WINDOW_HEIGHT * 0.2);
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
