/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import design4natureserver.CollisionField;
import design4natureserver.Line;
import design4natureserver.Point;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Bas
 */
public class CollisionTest {

    @Test
    public void testCollisions() {
//        CollisionField field = CollisionField.instance();
//
//        Line v = new Line(0, new Point(2, 2), new Point(2, 5));
//        Line h = new Line(0, new Point(1, 4), new Point(4, 3));
//        field.checkCollisionAndAdd(v);
//        boolean collision = field.checkCollisionAndAdd(h);
//        assertTrue("There should be a collision", collision);
//
//        v = new Line(0, new Point(-5, -5), new Point(-10, -10));
//        h = new Line(0, new Point(-7, -5), new Point(-12, -10));
//        collision = field.checkCollisionAndAdd(v);
//        collision = field.checkCollisionAndAdd(h);
//        assertFalse("There should be no collision", collision);
//
//        v = new Line(0, new Point(0, 0), new Point(1, -2));
//        h = new Line(0, new Point(2, 0), new Point(0, 1));
//
//        collision = field.checkCollisionAndAdd(v);
//        collision = field.checkCollisionAndAdd(h);
//        assertTrue("There should be a collision", collision);
    }

    @Test
    public void testPath() {
        CollisionField field = CollisionField.instance();

        Point[] points = new Point[]{new Point(-2, 4),
            new Point(-1, 3), new Point(-2, 3), new Point(-1, 4)};

        Point lastPoint = null;
        for (Point point : points) {
            if (lastPoint == null) {
                lastPoint = point;
                continue;
            }
            Line line = new Line(0, lastPoint, point);
            System.out.println(line);
            boolean collision = field.checkCollisionAndAdd(line);
            if (collision) {
                System.out.println(line);
                fail("There is a collision");
            }
            lastPoint = point;
        }
    }
}
