package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.bullet.AbstractBullet;

import java.util.List;

public interface ShootStrategy {
    public List<AbstractBullet> shoot();
    void setLocation(int aircraftLocationX, int aircraftLocationY);
}