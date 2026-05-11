package com.example.aircraftwar.application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;

import com.example.aircraftwar.DAO.ScoreDocument;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("ViewConstructor")
public class NetworkingGame extends GameTemplate{
    protected PrintWriter writer;
    protected int rivalscore = 0;
    protected int rivalhp = heroAircraft.maxHp;
    private final Paint rivalpaint;
    private final String myName = mainActivity.playerName;
    private ScoreDocument rivalfinalscore;
    private boolean updateRank = true;
    private final int sotimeout = 2000;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NetworkingGame(Context context, Socket socket) {
        super(context);
        rivalpaint = new Paint();
        Typeface font = Typeface.create("Microsoft YaHei UI",Typeface.NORMAL);
        rivalpaint.setColor(Color.RED);
        rivalpaint.setTypeface(font);
        rivalpaint.setAlpha(160);
        rivalpaint.setTextSize(70);
        rivalpaint.setTypeface(Typeface.DEFAULT);
        rivalpaint.setStrokeWidth(12);
        handler = new Handler(message -> {
            if(message.what == 1){
                ScoreDocument myscore = new ScoreDocument(score, mainActivity.playerName, datetime);
                mainActivity.networkingScoreRank(myscore, rivalfinalscore, updateRank);
                return false;
            }
            return true;
        });
        try {
            socket.setSoTimeout(sotimeout);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)),true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Runnable runnable = ()->{
            BufferedReader in;
            String[] stringlist;
            while(rivalhp!=0){
                try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    stringlist = in.readLine().split(",");
                    if(stringlist.length == 4) {
                        rivalscore = Integer.parseInt(stringlist[2]);
                        rivalhp = Integer.parseInt(stringlist[3]);
                        rivalfinalscore = new ScoreDocument(Integer.parseInt(stringlist[2]), stringlist[0], stringlist[1]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e){
                    e.printStackTrace();
                    break;
                }
            }
            updateRank = false;
        };
        Runnable r = ()->{
            while(!gameOverFlag) {
                Date date = new Date();
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
                writer.println(myName + ',' + dateFormat.format(date) + ',' + score + ',' + heroAircraft.getHp());
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r).start();
        new Thread(runnable).start();
    }

    @Override
    public void displayRank(String date) {
        Runnable runnable = ()->{
            String myfinalscore = myName + ',' + date + ',' + score + ',' + 0;
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int i = 0;i<3; i++) {
                writer.println(myfinalscore);
            }
        };
        new Thread(runnable).start();
        datetime = date;
        handler.sendEmptyMessage(1);
    }

    @Override
    public void bossGenerate() {
        if(score - bossAppearScore >= bossThreshold && bossEnemy.size()==0){
            if(bossAppeared == 1){
                bossEnemyFactory.addBossHp();
            }
            bossEnemy.add(bossEnemyFactory.createEnemyAircraft());
            bossAppeared = 1;
            startBossBgm = 1;
        }
    }

    @Override
    public void upgradeDifficulty() {
        if(time % 10200 == 0 && time != 0 && eliteGeneratePossibility <= 70){
            eliteGeneratePossibility+=2;
        }
        if(time % 30000 == 0 && time != 0 && enemyMaxNumber <= 10){
            enemyMaxNumber++;
        }
        if(time % 15000 == 0 && time != 0){
            mobEnemyFactory.addMobHp(6);
            eliteEnemyFactory.addEliteHp(6);
        }
        if(time % 30000 == 0 && time != 0){
            mobEnemyFactory.addMobSpeedY(10);
            eliteEnemyFactory.addEliteSpeedY(10);
        }
        if(time % 20400 == 0 && time != 0 && bossThreshold > 0){
            bossThreshold -= 10;
        }
        if(time % 30000 == 0 && time != 0){
            if(cycleDuration > timeInterval){
                cycleDuration -= timeInterval;
            }
        }
    }

    @Override
    public void paintScoreAndLife(Canvas g) {
        float x = 20;
        float y = 60;
        g.drawText("SCORE:" + score, x, y, paint);
        y = y + 60;
        g.drawText("LIFE:" + this.heroAircraft.getHp(), x, y, paint);
        x = 1060;
        y= 60;
        g.drawText("SCORE:" + rivalscore, x, y, rivalpaint);
        y += 60;
        g.drawText("LIFE:" + rivalhp , x, y, rivalpaint);
    }
}
