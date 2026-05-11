package com.example.aircraftwar.prop;

public interface PropFactory {
    public propAction createProp(int locationX, int locationY, int speedX, int speedY);
}
