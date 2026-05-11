package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.bullet.AbstractBullet;

import java.util.List;

public interface EnemyAction {
    public void move();
    public List<AbstractBullet> shoot();
}
