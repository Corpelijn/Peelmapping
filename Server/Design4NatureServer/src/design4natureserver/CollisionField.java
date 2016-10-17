/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design4natureserver;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bas
 */
public class CollisionField {

    private List<Line> NW;
    private List<Line> NE;
    private List<Line> SW;
    private List<Line> SE;

    private static CollisionField instance;

    private CollisionField() {
        NW = new ArrayList();
        NE = new ArrayList();
        SW = new ArrayList();
        SE = new ArrayList();
    }

    public boolean checkCollisionAndAdd(Line line) {
        boolean result = checkCollision(line);

        addNW(line);
        addNE(line);
        addSW(line);
        addSE(line);

        return result;
    }

    private boolean checkCollision(Line line) {
        return true;
    }

    private boolean addNW(Line line) {
        if (line.getStart().X <= 0 && line.getStart().Y <= 0) {
            NW.add(line);
            return true;
        }

        if (line.getEnd().X <= 0 && line.getStart().Y <= 0) {
            NW.add(line);
            return true;
        }

        return false;
    }

    private boolean addNE(Line line) {
        if (line.getStart().X >= 0 && line.getStart().Y <= 0) {
            NE.add(line);
            return true;
        }

        if (line.getEnd().X >= 0 && line.getStart().Y <= 0) {
            NE.add(line);
            return true;
        }

        return false;
    }

    private boolean addSW(Line line) {
        if (line.getStart().X <= 0 && line.getStart().Y >= 0) {
            SW.add(line);
            return true;
        }

        if (line.getEnd().X <= 0 && line.getStart().Y >= 0) {
            SW.add(line);
            return true;
        }

        return false;
    }

    private boolean addSE(Line line) {
        if (line.getStart().X >= 0 && line.getStart().Y >= 0) {
            SE.add(line);
            return true;
        }

        if (line.getEnd().X >= 0 && line.getStart().Y >= 0) {
            SE.add(line);
            return true;
        }

        return false;
    }

    public static CollisionField instance() {
        if (instance == null) {
            instance = new CollisionField();
        }
        return instance;
    }
}
