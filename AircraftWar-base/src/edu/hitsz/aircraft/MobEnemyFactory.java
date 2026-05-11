package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

public class MobEnemyFactory implements EnemyAircraftFactory{
    public int speedY = 7;
    public int hp = 20;
    @Override
    public MobEnemy createEnemyAircraft(){
        int locationX = (int) ( Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth()));
        int locationY = (int) (Math.random() * Main.WINDOW_HEIGHT * 0.2);
        int speedX = 0;
        return new MobEnemy(locationX, locationY, speedX, speedY, hp);
    }

    public void addMobSpeedY(int y){
        if(speedY < 20) {
            speedY += y;
        }
    }

    public void addMobHp(int blood){
        hp += blood;
    }
}
