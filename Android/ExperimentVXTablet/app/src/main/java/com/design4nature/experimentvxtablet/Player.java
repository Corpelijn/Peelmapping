package com.design4nature.experimentvxtablet;

import java.util.ArrayList;
/**
 * Created by Ruben on 17-10-2016.
 */
public class Player {
    private int id;
    private String name;
    private int color;
    private ArrayList<Point> points;

    public Player(int id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = color;
        points = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public void addPoint(Point point){
        points.add(point);
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void clearPoints(){
        points.clear();
    }
}
