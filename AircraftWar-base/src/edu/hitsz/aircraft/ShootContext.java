package edu.hitsz.aircraft;

import edu.hitsz.bullet.AbstractBullet;

import java.util.List;

public class ShootContext {
    public ShootStrategy strategy;

    public ShootContext(){}

    public void setStrategy(ShootStrategy strategy){
        this.strategy = strategy;
    }

    public List<AbstractBullet> executeStrategy(){
        return strategy.shoot();
    }
}
