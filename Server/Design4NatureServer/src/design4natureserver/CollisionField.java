/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design4natureserver;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides collision checks for all the lines in the game field.
 * @author Bas
 */
public class CollisionField {

    private CollisionArea NW;
    private CollisionArea NE;
    private CollisionArea SW;
    private CollisionArea SE;

    private static CollisionField instance;

    /**
     * Creates a new instance of the CollisionField class
     */
    private CollisionField() {
        NW = new CollisionArea();
        NE = new CollisionArea();
        SW = new CollisionArea();
        SE = new CollisionArea();
    }

    /**
     * Checks if there is a collision somewhere on the field and add the line to the field
     * @param line The line to check the collisions for
     * @return Returns true if there is a collision somewhere; otherwise false
     */
    public boolean checkCollisionAndAdd(Line line) {
        boolean result = checkCollision(line);

        if (isInNW(line)) {
            NW.addLine(line);
        }
        if (isInNE(line)) {
            NE.addLine(line);
        }
        if (isInSW(line)) {
            SW.addLine(line);
        }
        if (isInSE(line)) {
            SE.addLine(line);
        }

        return result;
    }

    /**
     * Check if there is a collision with the given line with some other line somewhere on the field
     * @param line The line to check the collision for
     * @return Returns true if the line collides somewhere; otherwise false
     */
    private boolean checkCollision(Line line) {
        // Check in what areas the line is
        List<CollisionArea> directions = new ArrayList();
        if (isInNW(line)) {
            directions.add(NW);
        }
        if (isInNE(line)) {
            directions.add(NE);
        }
        if (isInSW(line)) {
            directions.add(SW);
        }
        if (isInSE(line)) {
            directions.add(SE);
        }

        // Loop through all the areas we need to check
        for (CollisionArea area : directions) {
            boolean result = area.checkCollisionInArea(line);
            if(result)
                return true;
        }

        return false;
    }

    /**
     * Checks if the given line is in the NW area of the field
     * @param line The line to check
     * @return Returns true if the line is in the nW area; otherwise false
     */
    private boolean isInNW(Line line) {
        if (line.getStart().X <= 0 && line.getStart().Y <= 0) {
            return true;
        }

        return line.getEnd().X <= 0 && line.getStart().Y <= 0;
    }

    /**
     * Checks if the given line is in the NE area of the field
     * @param line The line to check
     * @return Returns true if the line is in the NE area; otherwise false
     */
    private boolean isInNE(Line line) {
        if (line.getStart().X >= 0 && line.getStart().Y <= 0) {
            return true;
        }

        return line.getEnd().X >= 0 && line.getStart().Y <= 0;
    }

    /**
     * Checks if the given line is in the SW area of the field
     * @param line The line to check
     * @return Returns true if the line is in the SW area; otherwise false
     */
    private boolean isInSW(Line line) {
        if (line.getStart().X <= 0 && line.getStart().Y >= 0) {
            return true;
        }

        return line.getEnd().X <= 0 && line.getStart().Y >= 0;
    }

    /**
     * Checks if the given line is in the SE area of the field
     * @param line The line to check
     * @return Returns true if the line is in the SE area; otherwise false
     */
    private boolean isInSE(Line line) {
        if (line.getStart().X >= 0 && line.getStart().Y >= 0) {
            return true;
        }

        return line.getEnd().X >= 0 && line.getStart().Y >= 0;
    }

    /**
     * Returns the static instance of the CollisionField class
     * @return The static instance of this class
     */
    public static CollisionField instance() {
        if (instance == null) {
            instance = new CollisionField();
        }
        return instance;
    }

}
