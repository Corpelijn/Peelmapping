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
public interface Listener {

    void onCollision(PlayerCollision collisionInfo);
    
    void onAddPlayer(Player player);
}
