package edu.hitsz.prop;

public class BloodSupplyPropFactory implements PropFactory{
    @Override
    public BloodSupplyProp createProp(int locationX, int locationY, int speedX, int speedY){
        return new BloodSupplyProp(locationX, locationY, speedX, speedY);
    }
}
