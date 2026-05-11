package edu.hitsz.application;

import edu.hitsz.DAO.ScoreDAOImpl;
import edu.hitsz.DAO.ScoreDocument;
import edu.hitsz.SaveScore;
import edu.hitsz.SimpleTable;

public class HardModeGame extends GameTemplate{
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
        path = "src/edu/hitsz/DAO/hardscore.txt";
        ScoreDAOImpl dao = new ScoreDAOImpl(path);
        dao.sortByScore();
        if(save){
            dao.doAdd(new ScoreDocument(score,name,date));
        }
        dao.save();
        SimpleTable.main(args);
    }

    @Override
    public void bossGenerate() {
        if(score - bossAppearScore >= bossThreshold && bossEnemy.size()==0){
            if(bossAppeared == 1){
                bossEnemyFactory.addBossHp();
            }
            bossEnemy.add(bossEnemyFactory.createEnemyAircraft());
            bossAppeared = 1;
            bossOnStageThread.recoverInterrupttedBgm();
            System.out.println("boss机出现，血量为"+bossEnemyFactory.getHp());
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
