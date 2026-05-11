package com.example.aircraftwar.prop;

import com.example.aircraftwar.aircraft.FloatShoot;
import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.aircraft.ShootStrategy;
import com.example.aircraftwar.aircraft.StraightShoot;

public class FireSupplyProp extends AbstractProp implements propAction{
    public FireSupplyProp(int locationX, int locationY, int speedX, int speedY){
        super(locationX,locationY,speedX,speedY);
    }

    @Override
    public void action(){
        System.out.println("FireSupply active!");
        HeroAircraft hero = HeroAircraft.getInstance();
        ShootStrategy strategy = new FloatShoot(hero.getLocationX(), hero.getLocationY(), 15, hero.getSpeedY(), 30, -1, 3);
        hero.setStrategyKind(strategy);
    }

    public void disAction(){
        HeroAircraft hero = HeroAircraft.getInstance();
        ShootStrategy strategy = new StraightShoot(hero.getLocationX(), hero.getLocationY(), hero.getSpeedY(), 30, -1, 1);
        hero.setStrategyKind(strategy);
    }
}
