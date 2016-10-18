/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design4natureserver;

import java.util.ArrayList;
import java.util.List;

/**
 * This class check if any lines within the area collide with eachother
 *
 * @author Bas
 */
public class CollisionArea {

    private List<Line> lines;

    /**
     * Creates a new instance of an CollisionArea
     */
    public CollisionArea() {
        lines = new ArrayList();
    }

    /**
     * Adds a line to the collisionArea
     *
     * @param line The line object to add
     */
    public void addLine(Line line) {
        lines.add(line);
    }

    /**
     * Check if the given line collides with any of the lines in the area
     *
     * @param line The line to check the collisions for
     * @return Returns true if the line collides with any other line; otherwise
     * false
     */
    public boolean checkCollisionInArea(Line line) {
        for (Line currentLine : lines) {
            // Check if we need to skip the line (previous line needs to be skipped because there will always be a collision
            if (currentLine.getOwner() == line.getOwner()
                    && currentLine.getEnd() == line.getStart()) {
                continue;
            }

            // Check if the line has a collision
            boolean result = checkLineCollision(currentLine, line);
            if (result) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if two lines collides somewhere
     *
     * @param line1 The first line
     * @param line2 The second line
     * @return Returns true if the lines collide somewhere; otherwise false
     */
    private boolean checkLineCollision(Line line1, Line line2) {
        Vector2 crossingPoint = lineIntersectionPoint(
                new Vector2(line1.getStart()), new Vector2(line1.getEnd()),
                new Vector2(line2.getStart()), new Vector2(line2.getEnd()));

        if (crossingPoint == null) {
            return false;
        } else {
            boolean l1 = isPointOnLine(new Vector2(line1.getStart()), new Vector2(line1.getEnd()), crossingPoint);
            boolean l2 = isPointOnLine(new Vector2(line2.getStart()), new Vector2(line2.getEnd()), crossingPoint);
            return l1 && l2;
        }
    }

    /**
     * Check if point C is somewhere on the line between A and B
     *
     * @param A The first point of the line (start)
     * @param B The second point of the line (end)
     * @param C The point to check if it is somewhere on the line between A and
     * B
     * @return Returns true is the point is somewhere on the line; otherwise
     * false
     */
    private boolean isPointOnLine(Vector2 A, Vector2 B, Vector2 C) {
        return Vector2.distance(A, C) + Vector2.distance(B, C) == Vector2.distance(A, B);
    }

    /**
     * Gets the position on a 2D plane where two lines cross
     *
     * @param ps1 The start position of the first line
     * @param pe1 The end position of the first line
     * @param ps2 The start position of the second line
     * @param pe2 The end position of the second line
     * @return Returns the position on a 2D plane where the two lines cross.
     * This crossing could be outside the point given. Returns null if the lines
     * are parallel
     */
    private Vector2 lineIntersectionPoint(Vector2 ps1, Vector2 pe1, Vector2 ps2,
            Vector2 pe2) {
        // Get A,B,C of first line - points : ps1 to pe1
        float A1 = pe1.y - ps1.y;
        float B1 = ps1.x - pe1.x;
        float C1 = A1 * ps1.x + B1 * ps1.y;

        // Get A,B,C of second line - points : ps2 to pe2
        float A2 = pe2.y - ps2.y;
        float B2 = ps2.x - pe2.x;
        float C2 = A2 * ps2.x + B2 * ps2.y;

        // Get delta and check if the lines are parallel
        float delta = A1 * B2 - A2 * B1;
        if (delta == 0) {
            return null;
        }

        // now return the Vector2 intersection point
        return new Vector2(
                (B2 * C1 - B1 * C2) / delta,
                (A1 * C2 - A2 * C1) / delta
        );
    }
}
