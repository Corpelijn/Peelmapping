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
public class Point {

    public int X;
    public int Y;

    public Point(int x, int y) {
        X = x;
        Y = y;
    }

    public Point substract(Point x) {
        return new Point(this.X - x.X, this.Y - x.Y);
    }

    public Point add(Point x) {
        return new Point(this.X + x.X, this.Y + x.Y);
    }

    public Point absolute() {
        return new Point(Math.abs(X), Math.abs(Y));
    }

    @Override
    public String toString() {
        return "X=" + X + ",Y=" + Y;
    }
}
