package design4natureserver;

import javafx.scene.paint.Color;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Bas
 */
public class Colors {

    private static Color[] colors = new Color[]{Color.RED, Color.CYAN, Color.LIME, Color.PURPLE, Color.MAGENTA};

    public static Color getColor(int client) {
        return colors[client];
    }
}
