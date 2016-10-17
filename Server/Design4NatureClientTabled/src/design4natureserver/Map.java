/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design4natureserver;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javax.imageio.ImageIO;

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
    private boolean drawSlugs;

    private List<Player> playerTracking;

    private List<Listener> listeners;

    private static Map instance;
    private GraphicsContext canvas;

    /**
     * Creates a new instance of a map
     *
     * @param canvasWidth The width of the canvas to draw on
     * @param canvasHeight The height of the canvas to draw on
     * @param canvas The canvas to draw on
     * @param grid Defines if the grid should be drawn
     */
    private Map(int canvasWidth, int canvasHeight, GraphicsContext canvas, boolean grid) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.correction = null;
        this.drawGrid = grid;
        this.drawSlugs = false;
        this.canvas = canvas;

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
     * @param name The name of the player
     */
    public void addPlayer(int id, String name) {
        for (Player p : playerTracking) {
            if (p.getId() == id) {
                p.setName(name);
                return;
            }
        }
        Color[] colors = new Color[]{Color.RED, Color.CYAN, Color.LIME, Color.YELLOW, Color.MAGENTA};
        Player player = new Player(id, colors[playerTracking.size()], name);
        playerTracking.add(player);

        onAddPlayer(player);
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
//        if (correction == null) {
//            correction = new Point(x, y);
//            x = 0;
//            y = 0;
//        } else {
//            x -= correction.X;
//            y -= correction.Y;
//        }

        boolean found = false;
        for (Player p : playerTracking) {
            if (p.getId() == player) {
                found = true;
                Point lastPointPlayer = p.getLastPosition();

                if (!p.addPlayerPosition(x, y)) {
                    break;
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
     */
    public synchronized void draw() {
        // Clear the canvas
//        canvas.setFill(Color.BLACK);
//        canvas.fillRect(0, 0, canvasWidth, canvasHeight);
        canvas.clearRect(0, 0, canvasWidth, canvasHeight);

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

            drawName(canvas, p.getName(), height, p.getColor());
            height += 30;

            canvas.setLineWidth(3);

            // Draw each position
            Point last = null;
            int direction = 0;
            for (Point point : p.getPoints()) {
                if (last != null) {
                    drawLine(canvas, getPixel(last), getPixel(point));

                    Point dir = point.substract(last);
                    if (dir.X > dir.Y && dir.X > 0) {
                        direction = 1;
                    } else if (dir.X < dir.Y && dir.X < 0) {
                        direction = 3;
                    } else if (dir.Y > dir.X && dir.Y > 0) {
                        direction = 2;
                    } else if (dir.Y < dir.X && dir.Y < 0) {
                        direction = 0;
                    }
                }
                last = point;
            }

            if (drawSlugs) {
                drawSlug(canvas, p.getId(), getPixel(last), direction);
            }
        }
    }

    private void drawSlug(GraphicsContext canvas, int client, Point point, int direction) {
        canvas.drawImage(new Image("/Images/" + client + direction + ".png"), point.X - 11, point.Y - 11, 22, 22);
    }

    private void drawName(GraphicsContext canvas, String name, int height, Color color) {
        //canvas.setLineWidth(1);
        canvas.setFill(color);
        canvas.setFont(new Font("Tahoma", 25));
        canvas.fillText(name, 20, 55 + height);
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

    private void onAddPlayer(Player player) {
        listeners.stream().forEach((l) -> {
            l.onAddPlayer(player);
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

    public String getImage() {
        draw();
        WritableImage image = canvas.getCanvas().snapshot(null, null);
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);

        ByteArrayOutputStream barray = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, "png", barray);
        } catch (IOException ex) {
        }

        return Base64.getEncoder().encodeToString(barray.toByteArray());
    }

    /**
     * Create a new instance of a map
     *
     * @param canvas The canvas to draw on
     * @param grid Defines if the grid needs to be drawn
     * @return Returns true if the Map instance was created; otherwise false
     */
    public static boolean create(Canvas canvas, boolean grid) {
        if (instance == null) {
            instance = new Map((int) canvas.getWidth(), (int) canvas.getHeight(), canvas.getGraphicsContext2D(), grid);
            return true;
        }
        return false;
    }

    /**
     * Gets the current instance of the Map
     *
     * @return The current instance of the Map
     */
    public static Map instance() {
        return instance;
    }

    public void setGrid(boolean value) {
        drawGrid = value;
    }

    public void setSlugs(boolean value) {
        drawSlugs = value;
    }
}
