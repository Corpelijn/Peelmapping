/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design4natureserver;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Bas
 */
public class Map {

    private int canvasWidth;
    private int canvasHeight;

    private int mapWidth;
    private int mapHeight;

    private Point correction;

    private List<Player> playerTracking;

    public Map(int canvasWidth, int canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.correction = null;

        // Do a first calculation of the map size
        calculateMapSize(false);

        playerTracking = new ArrayList();
    }

    private void calculateMapSize(boolean calculate) {
        if (!calculate) {
            mapWidth = 10;
            mapHeight = 10;
            return;
        }

        if (playerTracking.isEmpty()) {
            return;
        }

        // Get the lowest and highest values of each player
        Point low = new Point(0, 0), high = new Point(0, 0);
        for (Player p : playerTracking) {
            if (p.getLowestX() < low.X) {
                low.X = p.getLowestX();
            }
            if (p.getLowestY() < low.Y) {
                low.Y = p.getLowestY();
            }
            if (p.getHighestX() > high.X) {
                high.X = p.getHighestX();
            }
            if (p.getHighestY() > high.Y) {
                high.Y = p.getHighestY();
            }
        }

        // Calucate the actual size of the map
        mapWidth = (int) (high.X - low.X);
        mapHeight = (int) (high.Y - low.Y);

        // Get the highest and override the other
        if (mapWidth > mapHeight) {
            mapHeight = mapWidth;
        } else {
            mapWidth = mapHeight;
        }

        // Add a border of 10px to each side
        mapWidth += 20;
        mapHeight += 20;
    }

    private void calculateMapSize() {
        calculateMapSize(true);
    }

    public void addPlayer(int id) {
        Color[] colors = new Color[]{Color.RED, Color.BLUE, Color.GREEN, Color.PURPLE, Color.BLACK, Color.ORANGE, Color.MAGENTA};
        playerTracking.add(new Player(id, colors[playerTracking.size()]));
    }

    public boolean addPathToPlayer(int player, int x, int y) {
        if (correction == null) {
            correction = new Point(x, y);
            x = 0;
            y = 0;
        } else {
            x -= correction.X;
            y -= correction.Y;
        }

        boolean found = false;
        for (Player p : playerTracking) {
            if (p.getId() == player) {
                p.addPlayerPosition(x, y);
                found = true;
            }
        }

        calculateMapSize();

        return found;
    }

    /**
     * Draws the current players onto the canvas
     *
     * @param canvas The GraphicsContext to draw on
     */
    public void draw(GraphicsContext canvas) {
        // Clear the canvas
        canvas.clearRect(0, 0, canvasWidth, canvasHeight);

        // Get the correction from the side
        Point smallest = new Point(0, 0);
        for (Player p : playerTracking) {
            if (p.getLowestX() < smallest.X) {
                smallest.X = p.getLowestX();
            }

            if (p.getLowestY() < smallest.Y) {
                smallest.Y = p.getLowestY();
            }
        }

        // Draw a grid
        canvas.setStroke(Color.LIGHTGRAY);
        canvas.setLineWidth(1);
        for (int i = 0; i < mapWidth; i++) {
            drawLine(canvas, getPixel(new Point(i - mapWidth / 2, -mapHeight / 2)), getPixel(new Point(i - mapWidth / 2, mapHeight / 2)));
            drawLine(canvas, getPixel(new Point(-mapWidth / 2, i - mapHeight / 2)), getPixel(new Point(mapWidth / 2, i - mapHeight / 2)));
        }

        // Draw each player onto the canvas
        for (Player p : playerTracking) {
            canvas.setStroke(p.getColor());
            canvas.setLineWidth(3);

            // Draw each position
            Point last = null;
            for (Point point : p.getPoints()) {
                if (last != null) {
                    drawLine(canvas, getPixel(last), getPixel(point));
                }
                last = point;
            }
        }
    }

    private void drawLine(GraphicsContext canvas, Point start, Point end) {
        canvas.strokeLine(
                start.X, start.Y,
                end.X, end.Y
        );
    }

    private Point getPixel(Point source) {
        float stepWidth = /*canvasWidth*/ canvasHeight / mapWidth;
        float stepHeight = canvasHeight / mapHeight;

        float centerX = canvasWidth / 2;
        float centerY = canvasHeight / 2;

        return new Point((int) (centerX + source.X * stepWidth), (int) (centerY + source.Y * stepHeight));
    }
}
