package com.example.aircraftwar.prop;

import com.example.aircraftwar.aircraft.HeroAircraft;

public class BloodSupplyProp extends AbstractProp implements propAction{
    public BloodSupplyProp(int locationX, int locationY, int speedX, int speedY){
        super(locationX,locationY,speedX,speedY);
    }

    @Override
    public void action(){
       HeroAircraft heroAircraft = HeroAircraft.getInstance();
       if(heroAircraft.getHp()+25<= heroAircraft.maxHp) {
           heroAircraft.decreaseHp(-25);
       }
       else{
           heroAircraft.decreaseHp(heroAircraft.getHp()-heroAircraft.maxHp);
       }
    }
}
