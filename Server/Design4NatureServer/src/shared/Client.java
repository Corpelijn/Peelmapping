/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared;

import design4nature.Server;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;
import javafx.scene.paint.Color;

/**
 *
 * @author Bas
 */
public class Client implements Serializable {
    
    private Socket socket;
    private ObjectOutputStream sender;
    private ObjectInputStream reader;
    private int clientID;
    private String name;
    private boolean isTabled;
    
    private static int clientCounter = 0;
    
    public Client(Socket socket, List<Client> knownClients) {
        this.socket = socket;
        name = null;
        isTabled = false;
        
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
            sender.writeObject(message.getData());
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
                    
                    object = new Message(this, (String) reader.readObject());
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
                // Check if the client is a player or a viewer
                if (object.getData().startsWith("tablet")) {
                    isTabled = true;
                    System.out.println("A tabled client connected");

                    // Stop the message reader, we dont need it
                    break;
                }

                // Get the name of the phone player
                if (object.getData().startsWith("n:")) {
                    name = object.getData().replaceAll("n:", "");
                    System.out.println("Client connected: " + name);
                } // Get a GPS coordinate from the client
                else if (object.getData().startsWith("l:")) {
                    object.setData(object.getData().replaceAll("l:", ""));
                    //Server.receivedMessages.add(object);
                    Server.broadcast(object);
                } // The client requests his color
                else if (object.getData().equals("r:color")) {
                    Color[] colors = new Color[]{Color.RED, Color.CYAN, Color.LIME, Color.YELLOW, Color.MAGENTA, Color.CYAN};
                    Color color = colors[this.clientID];
                    sendMessage(new Message(this, String.format("c:#%02x%02x%02x", (int) color.getRed() * 255, (int) color.getGreen() * 255, (int) color.getBlue() * 255)));
                }
            }
        });
        t.start();
    }
    
    public String getName() {
        return name;
    }
    
    public int getId() {
        return this.clientID;
    }
    
    public boolean isTabled() {
        return isTabled;
    }
}
