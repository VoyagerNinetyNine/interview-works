package com.example.aircraftwar.application;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.example.aircraftwar.aircraft.HeroAircraft;


/**
 * 英雄机控制类
 * 监听鼠标，控制英雄机的移动
 *
 * @author hitsz
 */
public class HeroController {
    private final GameTemplate game;
    private final HeroAircraft heroAircraft;
    private final OnTouchListener dragListener;

    public HeroController(GameTemplate game, HeroAircraft heroAircraft){
        this.game = game;
        this.heroAircraft = heroAircraft;

        dragListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                int x = (int) e.getX();
                int y = (int) e.getY();
                if (x < 0 || x > MainActivity.WINDOW_WIDTH || y < 0 || y > MainActivity.WINDOW_HEIGHT) {
                    // 防止超出边界
                    return true;
                }
                heroAircraft.setLocation(x, y);
                return true;
            }
        };

        game.setOnTouchListener(dragListener);
    }


}
