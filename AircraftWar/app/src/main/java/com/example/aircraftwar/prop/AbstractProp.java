package com.example.aircraftwar.prop;

import com.example.aircraftwar.application.MainActivity;
import com.example.aircraftwar.basic.AbstractFlyingObject;

public abstract class AbstractProp extends AbstractFlyingObject {
    public AbstractProp(int locationX, int locationY, int speedX, int speedY){
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void forward(){
        super.forward();
        if (locationY >= MainActivity.WINDOW_HEIGHT ) {
            vanish();
        }
    }
}
