package com.example.aircraftwar.prop;

public class BombSupplyPropFactory implements PropFactory{
    @Override
    public BombSupplyProp createProp(int locationX, int locationY, int speedX, int speedY){
        return new BombSupplyProp(locationX, locationY, speedX, speedY);
    }
}
