package com.example.aircraftwar.DAO;

public class ScoreDocument {
    private int score;
    private String name;
    private String time;
    public ScoreDocument(int score, String name, String time){
        this.name = name;
        this.score = score;
        this.time = time;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public int getScore(){
        return score;
    }

    public String getTime(){
        return time;
    }
}
