/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design4natureserver;

import static design4natureserver.Server.connectedClients;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;

/**
 *
 * @author Bas
 */
public class Client extends DataProvider implements Serializable {

    private Socket socket;
    private ObjectOutputStream sender;
    private ObjectInputStream reader;
    private int clientID;
    private String name1;
    private String name2;
    private String teamname;
    private boolean isTabled;
    private boolean isReady;
    private Point lastPosition;
    private boolean isDead;

    private static int clientCounter = 0;

    /**
     * Creates a new client from the server
     *
     * @param socket The socket connection to the client
     */
    public Client(Socket socket) {
        this.socket = socket;
        isTabled = false;
        isReady = false;
        isDead = false;
        lastPosition = new Point(-1, -1);

        try {
            sender = new ObjectOutputStream(this.socket.getOutputStream());
            reader = new ObjectInputStream(this.socket.getInputStream());
        } catch (Exception ex) {
            System.out.println("Something went wrong while accepting a client:\n\t" + ex.getMessage());
        }

        startMessageReader();
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void sendMessage(Message message) {
        // The message now has a client name
        // Send the message
        try {
            sender.writeObject("l:" + message.getSender() + ":" + message.getData());
        } catch (Exception ecx) {
            System.out.println(ecx.getMessage());
        }
    }

    public void sendMessage(String message) {
        // The message now has a client name
        // Send the message
        try {
            sender.writeObject(message);
        } catch (Exception ecx) {
            System.out.println(ecx.getMessage());
        }
    }

    /**
     * Start a new reader for the incomming messages
     */
    private void startMessageReader() {
        Thread t = new Thread(() -> {
            boolean kill = false;
            while (true) {
                // If the kill value is true, terminate the message reader
                if (kill) {
                    break;
                }

                // Read a new message
                Message object;
                try {
                    if (reader == null) {
                        System.out.println("Cannot start reading messages:\n\tThe reader is null");
                        break;
                    }

                    object = new Message(this.clientID, (String) reader.readObject());
                } catch (IOException | ClassNotFoundException ex) {
                    if (ex.getMessage().equals("Connection reset")) {
                        System.out.println("Cannot read messages:\n\tThe connection was reset");
                        break;
                    }
                    continue;
                }

                //if (name != null) {
                //System.out.println("<" + name + ", " + this.clientID + "> " + object.getData());
                //}
                // Check if the client is a viewer
                if (object.getData().startsWith("tablet")) {
                    isTabled = true;
                    System.out.println("A tabled client connected");

                    for (Client client : connectedClients) {
                        if (!client.isTabled()) {
                            Server.broadcast("p:" + client.getId() + "," + (client.getName() != null ? client.getName() : "") + "," + client.getColor());
                        }
                    }
                } // Check if the client is a player
                else if (object.getData().startsWith("phone")) {
                    this.clientID = clientCounter;
                    clientCounter += 1;
                    System.out.println("A phone client connected (id:" + clientID + ")");

                    for (Client client : connectedClients) {
                        if (!client.isTabled()) {
                            Server.broadcast("p:" + client.getId() + "," + (client.getName() != null ? client.getName() : "") + "," + client.getColor());
                        }
                    }
                } // The client notifies it is ready
                else if (object.getData().startsWith("c:ready")) {
                    isReady = true;
                    // Trigger the onReady event for all the listeners
                    triggerEvent(0);
                } // Get the name of the phone player
                else if (object.getData().startsWith("t:")) {
                    String[] msg = object.getData().replaceAll("t:", "").split(",");
                    teamname = msg[0];
                    name1 = msg[1];
                    name2 = msg[2];
                    System.out.println("Client connected: " + teamname + "(" + name1 + "," + name2 + ")");
                } // Get a GPS coordinate from the client
                else if (object.getData().startsWith("l:")) {
                    String message = convertMessage(object.getData());
                    if (Server.gameStarted) {
                        checkCollision(message);
                    }
                    Server.broadcast(message);
                } // The client requests his color
                else if (object.getData().equals("r:color")) {
                    Color color = Colors.getColor(clientID);
                    sendMessage(String.format("c:#%02x%02x%02x", (int) color.getRed() * 255, (int) color.getGreen() * 255, (int) color.getBlue() * 255));
                }
            }
        });
        t.start();
    }

    private void checkCollision(String message) {
        String[] msg = message.replaceAll("l:", "").split(",");

        boolean collision = CollisionField.instance().checkCollisionAndAdd(new Line(this, lastPosition, new Point(Integer.parseInt(msg[0]), Integer.parseInt(msg[1]))));
        if (collision) {
            this.sendMessage("c:kill");
            isDead = true;
            this.triggerEvent(1);
        }
    }

    private static Point correction;

    private String convertMessage(String message) {
        // Get the Lat en Lon positions as local positions
        String[] msg = message.replaceAll("l:", "").split(",");
        float lat = Float.parseFloat(msg[0]);
        float lon = Float.parseFloat(msg[1]);

        String[] xy = PQRS.d2xy(lat, lon).split(",");
        if (xy[0].equals("buiten bereik")) {
            return "buiten bereik";
        }

        int x = Integer.parseInt(xy[0]);
        int y = -Integer.parseInt(xy[1]);

        if (correction == null) {
            correction = new Point(x, y);
            x = 0;
            y = 0;
        } else {
            x -= correction.X;
            y -= correction.Y;
        }

        if (x == lastPosition.X && y == lastPosition.Y) {
            return null;
        }

        lastPosition = new Point(x, y);
        message = "l:" + x + "," + y + "," + clientID;
        System.out.println(message);

        return message;
    }

    public String getName() {
        return teamname;
    }

    public int getId() {
        return this.clientID;
    }

    public boolean isTabled() {
        return isTabled;
    }

    public boolean isReady() {
        return isReady;
    }

    public String getColor() {
        Color color = Colors.getColor(clientID);
        return String.format("#%02x%02x%02x", (int) color.getRed() * 255, (int) color.getGreen() * 255, (int) color.getBlue() * 255);
    }

    public void killPlayer() {
        try {
            sender.writeObject("c:kill");
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String toString() {
        return teamname + "," + name1 + "," + name2;
    }

    public boolean isDead() {
        return isDead;
    }
}
