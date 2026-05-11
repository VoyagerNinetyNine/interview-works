package edu.hitsz.prop;

public class FireSupplyPropFactory implements PropFactory{
    @Override
    public FireSupplyProp createProp(int locationX, int locationY, int speedX, int speedY){
        return new FireSupplyProp(locationX, locationY, speedX, speedY);
    }
}
