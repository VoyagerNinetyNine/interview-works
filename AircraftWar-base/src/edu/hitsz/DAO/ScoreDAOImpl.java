package edu.hitsz.DAO;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScoreDAOImpl implements ScoreDAO{
    private List<ScoreDocument> scores;
    private String path;

    public ScoreDAOImpl(String path){
        scores = new ArrayList<ScoreDocument>();
        this.path = path;
        int score;
        String name,time;
        try {
            if(!Files.exists(Path.of(path))){
                Files.createFile(Path.of(path));
            }
            List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
            for(String inf : lines){
                String[] stringlist = inf.split(",");
                score = Integer.parseInt(stringlist[0]);
                name = stringlist[1];
                time = stringlist[2];
                scores.add(new ScoreDocument(score,name,time));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void findByName(String name){
        int finded = 0;
        for(ScoreDocument scoreDocument: scores){
            if(scoreDocument.getName() == name){
                System.out.println("Find Document\n"+"Name:"+scoreDocument.getName()+"  Score:"+scoreDocument.getScore()+"  Time:"+scoreDocument.getTime());
                finded = 1;
            }
        }
        if(finded == 0){
            System.out.println("Can not find this document!");
        }
    }

    @Override
    public List<ScoreDocument> getAllScores(){
        return scores;
    }

    @Override
    public void doAdd(ScoreDocument score){
        scores.add(score);
    }

    @Override
    public void doDelete(String time){
        for(ScoreDocument scoreDocument: scores){
            if(Objects.equals(scoreDocument.getTime(), time)){
                scores.remove(scoreDocument);
                return;
            }
        }
        System.out.println("Can not find this Document!");
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

    public void save(){
        Path of = Path.of(path);
        try {
            Files.delete(of);
            Files.createFile(of);
            for(ScoreDocument score : scores){
                String doc = score.getScore() + "," + score.getName() + "," + score.getTime() + "\n";
                Files.write(of,doc.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByRank(int rank){
        scores.remove(rank);
    }
}
