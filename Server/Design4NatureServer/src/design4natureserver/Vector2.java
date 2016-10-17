/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design4natureserver;

/**
 *
 * @author Bas
 */
public class Vector2 {

    public float x;
    public float y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(Point point) {
        this.x = point.X;
        this.y = point.Y;
    }

    @Override
    public String toString() {
        return "X=" + x + ",Y=" + y;
    }

    public Vector2 substract(Vector2 value) {
        return new Vector2(this.x - value.x, this.y - value.y);
    }

    public Vector2 add(Vector2 value) {
        return new Vector2(this.x + value.x, this.y + value.y);
    }
}
