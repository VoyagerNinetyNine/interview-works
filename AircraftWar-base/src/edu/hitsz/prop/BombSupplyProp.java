package edu.hitsz.prop;

import edu.hitsz.application.GameTemplate;

public class BombSupplyProp extends AbstractProp implements propAction{
    public BombSupplyProp(int locationX, int locationY, int speedX, int speedY){
        super(locationX,locationY,speedX,speedY);
    }

    @Override
    public void action(){
        System.out.println("BombSupply active!");
        GameTemplate.publisher.notifySubscribers();
    }
}
