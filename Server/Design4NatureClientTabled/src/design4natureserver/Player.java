/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design4natureserver;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;

/**
 *
 * @author Bas
 */
public class Player {

    private List<Point> path;
    private int playerId;
    private Color color;
    private String name;

    public Player(int id, Color color, String name) {
        playerId = id;
        path = new ArrayList();
        this.color = color;
        this.name = name;
    }

    public synchronized boolean addPlayerPosition(int x, int y) {
        if (path.size() > 0) {
            Point last = path.get(path.size() - 1);
            if (last.X == x && last.Y == y) {
                return false;
            }
        }
        path.add(new Point(x, y));
        //System.out.println(x + "," + y);

        return true;
    }

    public void clearPoints() {
        path.clear();
    }

    public int getId() {
        return playerId;
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public synchronized List<Point> getPoints() {
        return path;
    }

    public synchronized Point getLastPosition() {
        if (path.isEmpty()) {
            return null;
        }
        return path.get(path.size() - 1);
    }

    public synchronized int getLowestX() {
        int x = 0;
        for (Point p : path) {
            if (p.X < x) {
                x = p.X;
            }
        }

        return x;
    }

    public synchronized int getLowestY() {
        int y = 0;
        for (Point p : path) {
            if (p.Y < y) {
                y = p.Y;
            }
        }

        return y;
    }

    public synchronized int getHighestX() {
        int x = 0;
        for (Point p : path) {
            if (p.X > x) {
                x = p.X;
            }
        }

        return x;
    }

    public synchronized int getHighestY() {
        int y = 0;
        for (Point p : path) {
            if (p.Y > y) {
                y = p.Y;
            }
        }

        return y;
    }

    @Override
    public String toString() {
        return "Player: " + this.playerId;
    }

    public void removeLast() {
        path.remove(path.size() - 1);
    }
}
