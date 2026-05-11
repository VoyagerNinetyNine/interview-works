package edu.hitsz.application;

import edu.hitsz.DAO.ScoreDAOImpl;
import edu.hitsz.DAO.ScoreDocument;
import edu.hitsz.SaveScore;
import edu.hitsz.SimpleTable;

public class EasyModeGame extends GameTemplate{

    @Override
    public void displayRank(String date) throws InterruptedException {
        String[] args={};
        SaveScore saveScore = new SaveScore();
        saveScore.myMain(args);
        do{
            Thread.sleep(100);
        }while(!saveScore.getFrameNotValid());
        String name = saveScore.getName();
        boolean save = saveScore.getSave();
        path = "src/edu/hitsz/DAO/easyscore.txt";

        ScoreDAOImpl dao = new ScoreDAOImpl(path);
        dao.sortByScore();
        if(save){
            dao.doAdd(new ScoreDocument(score,name,date));
        }
        dao.save();
        SimpleTable.main(args);
    }

    @Override
    public void bossGenerate() {}

    @Override
    public void upgradeDifficulty() {}

}
