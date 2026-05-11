package com.example.aircraftwar.DAO;

import java.util.List;

public interface DAO {
    public boolean findByElement(String name);
    public List<ScoreDocument> getAllDocuments();
    void doAdd(ScoreDocument score);
}
