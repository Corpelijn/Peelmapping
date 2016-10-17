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
        CollisionField field = CollisionField.instance();

        Line v = new Line(null, new Point(2, 2), new Point(2, 5));
        Line h = new Line(null, new Point(1, 4), new Point(4, 3));
        field.checkCollisionAndAdd(v);
        assertTrue("There should be a collision", field.checkCollisionAndAdd(h));

        v = new Line(null, new Point(-5, -5), new Point(-10, -10));
        h = new Line(null, new Point(-7, -5), new Point(-12, -10));
        field.checkCollisionAndAdd(v);
        assertFalse("There should be no collision", field.checkCollisionAndAdd(h));

        v = new Line(null, new Point(0, 0), new Point(-5, 5));
        h = new Line(null, new Point(0, 0), new Point(-3, 0));

        field.checkCollisionAndAdd(v);
        assertTrue("There should be a collision", field.checkCollisionAndAdd(h));
    }

}
