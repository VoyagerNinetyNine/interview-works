import edu.hitsz.aircraft.MobEnemy;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MobEnemyTest {

    @Test
    void decreaseHp() {
        MobEnemy mob = new MobEnemy( (int) ( Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.2), 0, 10, 30);
        mob.decreaseHp(10);
        assertEquals(mob.getHp(),20);
        mob.decreaseHp(30);
        assertEquals(mob.getHp(),0);
    }

    @Test
    void move() {
        int speedX = 0;
        int speedY = 10;
        MobEnemy mob = new MobEnemy( (int) ( Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.2), speedX, speedY, 30);
        int locationX = mob.getLocationX();
        int locationY = mob.getLocationY();
        mob.move();
        assertEquals(locationX+speedX,mob.getLocationX());
        assertEquals(locationY+speedY,mob.getLocationY());
    }

    @Test
    void shoot() {
        MobEnemy mob = new MobEnemy( (int) ( Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.2), 0, 10, 30);
        assertTrue(mob.shoot().isEmpty());
    }
}