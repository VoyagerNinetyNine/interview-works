import edu.hitsz.aircraft.HeroAircraft;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeroAircraftTest {

    @Test
    void getImage() {
        HeroAircraft hero = HeroAircraft.getInstance();
        assertNotNull(hero.getImage());
    }

    @Test
    void shoot() {
        HeroAircraft hero = HeroAircraft.getInstance();
        assertNotNull(hero.shoot());
    }

    @Test
    void getInstance() {
        HeroAircraft hero = HeroAircraft.getInstance();
        assertEquals(hero, HeroAircraft.getInstance());
    }
}