import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.prop.BloodSupplyProp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BloodSupplyPropTest {

    @Test
    void crash() {
        BloodSupplyProp prop = new BloodSupplyProp(Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight() ,4,0);
        HeroAircraft hero = HeroAircraft.getInstance();
        assertTrue(prop.crash(hero));
    }

    @Test
    void action() {
        HeroAircraft hero = HeroAircraft.getInstance();
        BloodSupplyProp prop = new BloodSupplyProp(Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight() ,4,0);
        prop.action();
        assertEquals(hero.maxHp,hero.getHp());
        hero.decreaseHp(25);
        prop.action();
        assertEquals(hero.maxHp,hero.getHp());
    }
}