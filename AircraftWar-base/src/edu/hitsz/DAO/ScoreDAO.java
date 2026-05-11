package edu.hitsz.DAO;

import java.util.List;

public interface ScoreDAO {
    public void findByName(String name);
    public List<ScoreDocument> getAllScores();
    void doAdd(ScoreDocument score);
    void doDelete(String time);
}
