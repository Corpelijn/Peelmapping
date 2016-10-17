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
public class PlayerCollision {

    public Player player;
    public Player collidingPlayer;
    public Point p1;
    public Point p2;
    public Point p3;
    public Point p4;

    public PlayerCollision(Player player, Player collidingPlayer, Point p1, Point p2, Point p3, Point p4) {
        this.player = player;
        this.collidingPlayer = collidingPlayer;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
    }

    @Override
    public String toString() {
        return player + " -> " + collidingPlayer + " (" + p1 + "," + p2 + "," + p3 + "," + p4 + ")";
    }
}
