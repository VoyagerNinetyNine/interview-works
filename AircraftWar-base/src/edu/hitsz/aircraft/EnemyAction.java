package edu.hitsz.aircraft;

import edu.hitsz.bullet.AbstractBullet;

import java.util.List;

public interface EnemyAction {
    public void move();
    public List<AbstractBullet> shoot();
}
