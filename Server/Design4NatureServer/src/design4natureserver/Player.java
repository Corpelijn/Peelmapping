/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design4natureserver;

import java.util.ArrayList;
import java.util.Iterator;
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

    public Player(int id, Color color) {
        playerId = id;
        path = new ArrayList();
        this.color = color;
    }

    public void addPlayerPosition(int x, int y) {
        if (path.size() > 0) {
            Point last = path.get(path.size() - 1);
            if (last.X == x && last.Y == y) {
                return;
            }
        }
        path.add(new Point(x, y));
        System.out.println(x + "," + y);
    }

    public int getId() {
        return playerId;
    }

    public Color getColor() {
        return color;
    }

    public List<Point> getPoints() {
        return path;
    }

    public int getLowestX() {
        int x = 0;
        for (Point p : path) {
            if (p.X < x) {
                x = p.X;
            }
        }

        return x;
    }

    public int getLowestY() {
        int y = 0;
        for (Point p : path) {
            if (p.Y < y) {
                y = p.Y;
            }
        }

        return y;
    }

    public int getHighestX() {
        int x = 0;
        for (Point p : path) {
            if (p.X > x) {
                x = p.X;
            }
        }

        return x;
    }

    public int getHighestY() {
        int y = 0;
        for (Point p : path) {
            if (p.Y > y) {
                y = p.Y;
            }
        }

        return y;
    }
}
