package com.example.klimo.myapplication;

import java.util.List;

public interface IDatabaseHandler {
    public void addPoints(int points);
    public List<Integer> getAllPoints();
    public int getPointsCount();
    public void deleteAll();
}
