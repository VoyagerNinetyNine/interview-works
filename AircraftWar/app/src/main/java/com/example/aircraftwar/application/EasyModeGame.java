package com.example.aircraftwar.application;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.aircraftwar.R;

public class EasyModeGame extends GameTemplate{
    public MainActivity mainActivity;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public EasyModeGame(Context context) {
        super(context);
        mainActivity = (MainActivity)context;
    }

    @Override
    public void displayRank(String date){
        datetime = date;
        difficulity = R.string.easy;
        path = "easyscore.txt";
        handler.sendEmptyMessage(1);
    }

    @Override
    public void bossGenerate() {}

    @Override
    public void upgradeDifficulty() {}

}
