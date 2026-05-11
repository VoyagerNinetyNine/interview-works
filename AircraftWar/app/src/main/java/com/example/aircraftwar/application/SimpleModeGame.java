package com.example.aircraftwar.application;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.aircraftwar.R;

public class SimpleModeGame extends GameTemplate{

    public MainActivity mainActivity;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public SimpleModeGame(Context context) {
        super(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void displayRank(String date) {
        datetime = date;
        path = "simplescore.txt";
        difficulity = R.string.simple;
        handler.sendEmptyMessage(1);
    }

    @Override
    public void bossGenerate() {
        if(score - bossAppearScore >= bossThreshold && bossEnemy.size()==0){
            bossEnemy.add(bossEnemyFactory.createEnemyAircraft());
            bossAppeared = 1;
            startBossBgm = 1;
        }
    }

    @Override
    public void upgradeDifficulty() {
        if(time % 10200 == 0 && time != 0 && eliteGeneratePossibility <= 70){
            eliteGeneratePossibility++;
            System.out.println("难度提高，精英机出现概率提升为0."+eliteGeneratePossibility);
        }
        if(time % 30000 == 0 && time != 0 && enemyMaxNumber <= 10){
            enemyMaxNumber++;
            System.out.println("难度提高，敌机数量上限提高为"+enemyMaxNumber);
        }
        if(time % 15000 == 0 && time != 0){
            mobEnemyFactory.addMobHp(2);
            eliteEnemyFactory.addEliteHp(2);
            System.out.println("难度提高，敌机血量增加2");
        }
        if(time % 30000 == 0 && time != 0){
            mobEnemyFactory.addMobSpeedY(1);
            eliteEnemyFactory.addEliteSpeedY(1);
            System.out.println("难度提高，敌机速度增加1");
        }
        if(time % 20400 == 0 && time != 0 && bossThreshold > 0){
            bossThreshold -= 10;
            System.out.println("难度提高，BOSS机出现阈值降低为"+bossThreshold);
        }
        if(time % 30000 == 0 && time != 0){
            if(cycleDuration > timeInterval){
                cycleDuration -= timeInterval;
                System.out.println("难度提高，敌机产生及射击周期为"+cycleDuration+"ms");
            }
        }
    }
}
