/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design4natureserver;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
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
    private boolean drawGrid;
    
    private List<Player> playerTracking;
    private List<ClaimedArea> claimedArea;
    
    private List<Listener> listeners;

    /**
     * Creates a new instance of a map
     *
     * @param canvasWidth The width of the canvas to draw on
     * @param canvasHeight The height of the canvas to draw on
     * @param grid Defines if the grid should be drawn
     */
    public Map(int canvasWidth, int canvasHeight, boolean grid) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.correction = null;
        this.drawGrid = grid;
        this.claimedArea = new ArrayList();

        // Do a first calculation of the map size
        calculateMapSize(false);
        
        playerTracking = new ArrayList();
        listeners = new ArrayList();
    }

    /**
     * (Re)Calculates the size of the map
     *
     * @param calculate Defines if the method actualy needs to calculate or
     * reset the map size
     */
    private void calculateMapSize(boolean calculate) {
        if (!calculate) {
            mapWidth = 100;
            mapHeight = 100;
            return;
        }

//        if (playerTracking.isEmpty()) {
//            return;
//        }
//
//        // Get the lowest and highest values of each player
//        Point low = new Point(0, 0), high = new Point(0, 0);
//        for (Player p : playerTracking) {
//            if (p.getLowestX() < low.X) {
//                low.X = p.getLowestX();
//            }
//            if (p.getLowestY() < low.Y) {
//                low.Y = p.getLowestY();
//            }
//            if (p.getHighestX() > high.X) {
//                high.X = p.getHighestX();
//            }
//            if (p.getHighestY() > high.Y) {
//                high.Y = p.getHighestY();
//            }
//        }
//
//        // Calucate the actual size of the map
//        mapWidth = (int) (high.X - low.X);
//        mapHeight = (int) (high.Y - low.Y);
//
//        // Get the highest and override the other
//        if (mapWidth > mapHeight) {
//            mapHeight = mapWidth;
//        } else {
//            mapWidth = mapHeight;
//        }
//
//        // Add a border of 10px to each side
//        mapWidth += 20;
//        mapHeight += 20;
    }

    /**
     * (Re)Calculates the size of the map
     */
    private void calculateMapSize() {
        calculateMapSize(true);
    }

    /**
     * Adds a player to the grid
     *
     * @param id The id of the player
     */
    public void addPlayer(int id, String name) {
        Color[] colors = new Color[]{Color.RED, Color.LIGHTBLUE, Color.LIGHTGREEN, Color.YELLOW, Color.MAGENTA};
        playerTracking.add(new Player(id, colors[playerTracking.size()], name));
    }

    /**
     * Adds a position to a player
     *
     * @param player The player id of the player that moved
     * @param x The x position of the new position
     * @param y The y position of the new position
     * @return Returns true if the player was found; otherwise false
     */
    public synchronized boolean addPathToPlayer(int player, int x, int y) {
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
                found = true;
                Point lastPointPlayer = p.getLastPosition();
                
                if (!p.addPlayerPosition(x, y)) {
                    break;
                }
                
                boolean breakFor = false;
                if (lastPointPlayer != null) {
                    for (Player allPlayers : playerTracking) {
                        // Check if there is any collision with any other player
                        if (allPlayers.getId() != player) {
//                            List<Point> playerPoints = allPlayers.getPoints();
//                            for (int i = 0; i < playerPoints.size() - 1; i++) {
//                                boolean result = intersect(playerPoints.get(i), playerPoints.get(i + 1), lastPointPlayer, new Point(x, y));
//                                if (result) {
//                                    onCollision(new PlayerCollision(p, allPlayers, playerPoints.get(i), playerPoints.get(i + 1), lastPointPlayer, new Point(x, y)));
//                                    p.removeLast();
//                                    breakFor = true;
//                                    break;
//                                }
//                            }
                        } // Check if there is a collision with the player itself
                        else {
//                            List<Point> playerPoints = allPlayers.getPoints();
//                            for (int i = 0; i < playerPoints.size() - 2; i++) {
//                                boolean result = intersect(playerPoints.get(i), playerPoints.get(i + 1), lastPointPlayer, new Point(x, y));
//                                if (result) {
//                                    onCollision(new PlayerCollision(p, allPlayers, playerPoints.get(i), playerPoints.get(i + 1), lastPointPlayer, new Point(x, y)));
//                                    p.removeLast();
//                                    breakFor = true;
//                                    break;
//                                }
//                            }
                        }
                        
                        if (breakFor) {
                            break;
                        }
                    }
                }
                
                break;
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
    public synchronized void draw(GraphicsContext canvas) {
        // Clear the canvas
        canvas.setFill(Color.BLACK);
        canvas.fillRect(0, 0, canvasWidth, canvasHeight);

        // Draw a grid
        if (drawGrid) {
            canvas.setStroke(Color.LIGHTGRAY);
            canvas.setLineWidth(1);
            for (int i = 0; i < mapWidth; i++) {
                drawLine(canvas, getPixel(new Point(i - mapWidth / 2, -mapHeight / 2)), getPixel(new Point(i - mapWidth / 2, mapHeight / 2)));
                drawLine(canvas, getPixel(new Point(-mapWidth / 2, i - mapHeight / 2)), getPixel(new Point(mapWidth / 2, i - mapHeight / 2)));
            }
        }

        // Draw each player onto the canvas
        int height = 0;
        for (Player p : playerTracking) {
            canvas.setStroke(p.getColor());
            
            drawName(canvas, p.getName(), height);
            height += 20;
            
            canvas.setLineWidth(7);

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
    
    private void drawName(GraphicsContext canvas, String name, int height) {
        canvas.setLineWidth(1);
        canvas.strokeText(name, 10, 50 + height);
    }

    /**
     * Draws a line between two points on the map
     *
     * @param canvas The canvas to draw on
     * @param start The start position of the line
     * @param end The end position of the line
     */
    private void drawLine(GraphicsContext canvas, Point start, Point end) {
        canvas.strokeLine(
                start.X, start.Y,
                end.X, end.Y
        );
    }

    /**
     * Converts a point in the map to a pixel on the canvas
     *
     * @param source A point on the map
     * @return A new point containing the correct pixel position on the canvas
     */
    private Point getPixel(Point source) {
        float stepWidth = /*canvasWidth*/ canvasHeight / mapWidth;
        float stepHeight = canvasHeight / mapHeight;
        
        float centerX = canvasWidth / 2;
        float centerY = canvasHeight / 2;
        
        return new Point((int) (centerX + source.X * stepWidth), (int) (centerY + source.Y * stepHeight));
    }

    /**
     * Calculates the line between two points
     *
     * @param p0 The start point
     * @param p1 The end point
     * @return Returns an array of positions that form a line
     */
    private Point[] getBresenhamLine(Point p0, Point p1, int lineWidth) {
        int x0 = p0.X;
        int y0 = p0.Y;
        int x1 = p1.X;
        int y1 = p1.Y;

        // Calculate the distance between the 2 points for x and y
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        // Define a direction to move
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;

        // 
        int err = dx - dy;
        
        List<Point> points = new ArrayList();
        
        while (true) {
            // Add the current point to the list
            points.add(new Point(x0, y0));
            for (int i = 0; i < lineWidth / 2; i++) {
                points.add(new Point(x0 + sx * i, y0));
                points.add(new Point(x0 - sx * i, y0));
                
                points.add(new Point(x0, y0 + sy * i));
                points.add(new Point(x0, y0 - sy * i));
                
                points.add(new Point(x0 + sx * i, y0 + sy * i));
                points.add(new Point(x0 - sx * i, y0 + sy * i));
                points.add(new Point(x0 + sx * i, y0 - sy * i));
                points.add(new Point(x0 - sx * i, y0 - sy * i));
            }

            // If the current point equals to the end position, break the loop
            if (x0 == x1 && y0 == y1) {
                break;
            }
            
            int e2 = 2 * err;
            if (e2 > -dy) {
                err = err - dy;
                x0 = x0 + sx;
            }
            if (e2 < dx) {
                err = err + dx;
                y0 = y0 + sy;
            }
        }
        
        Set<Point> uniquePoints = new HashSet<>(points);
        
        return uniquePoints.toArray(new Point[uniquePoints.size()]);
    }

    /**
     * Adds a listener to the current map for collision events
     *
     * @param l The listener to add
     */
    public void addListener(Listener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }
    
    private void onCollision(PlayerCollision collision) {
        listeners.stream().forEach((l) -> {
            l.onCollision(collision);
        });
    }
    
    public static int orientation(Point p, Point q, Point r) {
        double val = (q.Y - p.Y) * (r.X - q.X)
                - (q.X - p.X) * (r.Y - q.Y);
        
        if (val == 0.0) {
            return 0; // colinear
        }
        return (val > 0) ? 1 : 2; // clock or counterclock wise
    }
    
    public static boolean intersect(Point p1, Point q1, Point p2, Point q2) {
        
        if (q1 == p2) {
            return false;
        }
        
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);
        
        if (o1 != o2 && o3 != o4) {
            return true;
        }
        
        return false;
    }
}
