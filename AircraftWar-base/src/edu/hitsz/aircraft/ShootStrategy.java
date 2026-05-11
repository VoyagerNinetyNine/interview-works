package edu.hitsz.aircraft;

import edu.hitsz.bullet.AbstractBullet;

import java.util.List;

public interface ShootStrategy {
    public List<AbstractBullet> shoot();
    void setLocation(int aircraftLocationX, int aircraftLocationY);
}