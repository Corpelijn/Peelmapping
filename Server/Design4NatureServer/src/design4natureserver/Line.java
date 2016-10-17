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
public class Line {

    private Point start;
    private Point end;
    private Client owner;

    public Line(Client owner, Point start, Point end) {
        this.start = start;
        this.end = end;
        this.owner = owner;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

    public Client getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return "Start=" + start + ", End=" + end;
    }
}
