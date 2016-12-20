/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design4natureserver;

import static design4natureserver.Server.connectedClients;
import static design4natureserver.Server.killedClients;
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
    private boolean isTablet;
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
        isTablet = false;
        isReady = false;
        isDead = false;
        lastPosition = null;

        this.clientID = clientCounter;
        clientCounter += 1;

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
            if (ecx.getMessage().equals("Connection reset by peer: socket write error")) {
                // Completly terminate the client
                Server.teminateClient(this);
                System.out.println(ecx.getMessage());
            } else if (ecx.getMessage().equals("Software caused connection abort: socket write error")) {
                // Completly terminate the client
                Server.teminateClient(this);
                System.out.println(ecx.getMessage());
            }
        }
    }

    public void sendMessage(String message) {
        // The message now has a client name
        // Send the message
        try {
            sender.writeObject(message);
        } catch (Exception ecx) {
            if (ecx.getMessage().equals("Connection reset by peer: socket write error")) {
                // Completly terminate the client
                Server.teminateClient(this);
                System.out.println(ecx.getMessage());
            } else if (ecx.getMessage().equals("Software caused connection abort: socket write error")) {
                // Completly terminate the client
                Server.teminateClient(this);
                System.out.println(ecx.getMessage());
            }
        }
    }

    /**
     * Start a new reader for the incomming messages
     */
    private void startMessageReader() {
        Thread t = new Thread(() -> {
            while (true) {
                // If the kill value is true, terminate the message reader
                if (isDead) {
                    break;
                }   // Read a new message
                Message object;
                try {
                    if (reader == null) {
                        System.out.println("Cannot start reading messages:\n\tThe reader is null");
                        break;
                    }

                    object = new Message(Server.getClientID(clientID), (String) reader.readObject());
                } catch (IOException | ClassNotFoundException ex) {
                    if (ex.getMessage() == null) {
                        continue;
                    }
                    if (ex.getMessage().equals("Connection reset")) {
                        System.out.println("Cannot read messages:\n\tThe connection was reset");
                        break;
                    }
                    continue;
                }

                // Check if the client is a viewer
                if (object.getData().startsWith("tablet")) {
                    isTablet = true;
                    System.out.println("A tablet client connected (id:" + clientID + ")");

                    for (Client client : connectedClients) {
                        if (!client.isTablet()) {
                            Server.broadcast("p:" + Server.getClientID(client.getId()) + "," + (client.getName() != null ? client.getName() : "") + "," + client.getColor());
                        }
                    }
                } // Check if the client is a player
                else if (object.getData().startsWith("phone")) {
                    System.out.println("A phone client connected (id:" + clientID + ")");
                } // The client notifies it is ready
                else if (object.getData().startsWith("c:ready")) {
                    isReady = true;
                    System.out.println(clientID + ": ready");
                    // Trigger the onReady event for all the listeners
                    triggerEvent(0);
                } // Get the name of the phone player
                else if (object.getData().startsWith("t:")) {
                    String[] msg = object.getData().replaceAll("t:", "").split(",");
                    teamname = msg[0];
                    name1 = msg[1];
                    name2 = msg[2];
                    System.out.println("Client connected: " + teamname + "(" + name1 + "," + name2 + ")");

                    for (Client client : connectedClients) {
                        if (!client.isTablet()) {
                            Server.broadcast("p:" + Server.getClientID(client.getId()) + "," + (client.getName() != null ? client.getName() : "") + "," + client.getColor());
                        }
                    }
                } // Get a GPS coordinate from the client
                else if (object.getData().startsWith("l:")) {
                    String message = convertMessage(object.getData());
                    if (message == null || message.equals("buiten bereik")) {
                        continue;
                    }
                    System.out.println(message);
                    if (Server.gameStarted) {
                        checkCollision(message);
                    }
                    Server.broadcast(message);
                } else if (object.getData().equals("r:ranking")) {
                    for (Client c : connectedClients) {
                        if (!c.isTablet()) {
                            Client.this.sendMessage("r:" + (Server.getPhoneCount() - killedClients.indexOf(c)) + "," + c.toString());
                        }
                    }
                } // The client requests his color
                else if (object.getData().equals("r:color")) {
                    Color color = Colors.getColor(Server.getClientID(clientID));
                    sendMessage(String.format("c:#%02x%02x%02x", (int) (color.getRed() * 255f), (int) (color.getGreen() * 255f), (int) (color.getBlue() * 255f)));
                } else if (object.getData().equals("r:players")) {
                    System.out.println("ID: " + clientID + " -> requested players");
                    for (Client client : connectedClients) {
                        if (!client.isTablet()) {
                            sendMessage("p:" + Server.getClientID(client.getId()) + "," + (client.getName() != null ? client.getName() : "") + "," + client.getColor());
                        }
                    }
                } else if (object.getData().equals("k:killall")) {
                    for (Client c : connectedClients) {
                        if (!c.isTablet()) {
                            c.sendMessage("c:kill");
                        }
                    }
                }
            }
        });
        t.start();
    }

    private void checkCollision(String message) {
        String[] msg = message.replaceAll("l:", "").split(",");

        Point newPoint = new Point(Integer.parseInt(msg[0]), Integer.parseInt(msg[1]));
        if (lastPosition == null) {
            lastPosition = newPoint;
            return;
        }

        boolean collision = CollisionField.instance().checkCollisionAndAdd(new Line(clientID, lastPosition, newPoint));
        //System.out.println("collision: " + collision);
        lastPosition = newPoint;
        if (collision) {
            this.sendMessage("c:kill");
            isDead = true;
            this.triggerEvent(1);
        }
    }

    private static Point correction;

    private String convertMessageDEBUG(String message) {
        message += "," + Server.getClientID(clientID);

        String[] msg = message.replaceAll("l:", "").split(",");
        int x = Integer.parseInt(msg[0]);
        int y = -Integer.parseInt(msg[1]);

        if (correction == null) {
            correction = new Point(x, y);
            x = 0;
            y = 0;
        } else {
            x -= correction.X;
            y -= correction.Y;
        }

        if (lastPosition != null && x == lastPosition.X && y == lastPosition.Y) {
            return null;
        }

        message = "l:" + x + "," + y + "," + Server.getClientID(clientID);

        //System.out.println(message);
        return message;
    }

    private String convertMessage(String message) {
        // Get the Lat en Lon positions as local positions
        String[] msg = message.replaceAll("l:", "").split(",");
        float lat = Float.parseFloat(msg[0]);
        float lon = Float.parseFloat(msg[1]);

        //System.out.println(message);
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

        if (lastPosition != null && x == lastPosition.X && y == lastPosition.Y) {
            return null;
        }

        message = "l:" + x + "," + y + "," + Server.getClientID(clientID);
        //System.out.println(message);

        return message;
    }

    public String getName() {
        return teamname;
    }

    public int getId() {
        return this.clientID;
    }

    public boolean isTablet() {
        return isTablet;
    }

    public boolean isReady() {
        return isReady;
    }

    public String getColor() {
        Color color = Colors.getColor(Server.getClientID(clientID));
        return String.format("#%02x%02x%02x", (int) (color.getRed() * 255f), (int) (color.getGreen() * 255f), (int) (color.getBlue() * 255f));
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

    public Point getLastPosition() {
        return lastPosition;
    }

    public void terminate() {
        try {
            isDead = true;
            sender.close();
            reader.close();
            socket.close();

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void dispose() {
        this.lastPosition = null;
        sender = null;
        reader = null;
        socket = null;
        name1 = null;
        name2 = null;
        teamname = null;
    }
}
