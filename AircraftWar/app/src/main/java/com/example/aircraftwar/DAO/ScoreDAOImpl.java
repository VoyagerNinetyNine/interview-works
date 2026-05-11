package com.example.aircraftwar.DAO;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScoreDAOImpl implements DAO {
    private List<ScoreDocument> scores;
    private final String path;
    private final Context context;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ScoreDAOImpl(String path, Context context){
        scores = new ArrayList<>();
        this.path = path;
        int score;
        this.context = context;
        String name,time;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(path, Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileInputStream fileInputStream = null;
        Reader reader = null;
        BufferedReader bufferedReader = null;
        try {
            fileInputStream = context.openFileInput(path);
            reader = new InputStreamReader(fileInputStream);// 字符流
            bufferedReader = new BufferedReader(reader); //缓冲流
            String temp;
            while ((temp = bufferedReader.readLine()) != null) {
                String[] stringlist = temp.split(",");
                score = Integer.parseInt(stringlist[0]);
                name = stringlist[1];
                time = stringlist[2];
                scores.add(new ScoreDocument(score,name,time));
            }
            Log.i("MainActivity", "result:" + "");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean findByElement(String name){
        boolean finded = false;
        for(ScoreDocument scoreDocument: scores){
            if(Objects.equals(scoreDocument.getName(), name)){
                System.out.println("Find Document\n"+"Name:"+scoreDocument.getName()+"  Score:"+scoreDocument.getScore()+"  Time:"+scoreDocument.getTime());
                finded = true;
            }
        }
        if(!finded){
            System.out.println("Can not find this document!");
        }
        return finded;
    }

    @Override
    public List<ScoreDocument> getAllDocuments(){
        return scores;
    }

    @Override
    public void doAdd(ScoreDocument score){
        scores.add(score);
    }

    public void sortByScore(){
        int n = scores.size();
        ScoreDocument temp;
        for(int j = 1 ; j<n ; j++) {
            temp = scores.get(j);
            int i;
            for (i = j-1 ; i >= 0; i--) {
                if (scores.get(i).getScore() < temp.getScore()) {
                    scores.set(i + 1, scores.get(i));
                } else {
                    break;
                }
            }
            scores.set(i + 1, temp);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void save(){
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(path, Context.MODE_PRIVATE);
            for(ScoreDocument score : scores) {
                String text = score.getScore() + "," + score.getName() + "," + score.getTime() + "\n";
                fileOutputStream.write(text.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void deleteByRank(int rank){
        scores.remove(rank);
    }
}
