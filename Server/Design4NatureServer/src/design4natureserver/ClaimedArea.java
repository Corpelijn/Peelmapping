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
public class ClaimedArea {

    public Point point;
    public Player player;

    public ClaimedArea(Point point, Player player) {
        this.point = point;
        this.player = player;
    }
}
