/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared;

import design4nature.Server;
import design4natureserver.Map;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javafx.application.Platform;
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
    private boolean kill;
    private String name;

    private static int clientCounter = 0;

    public Client(Socket socket, List<Client> knownClients) {
        this.socket = socket;
        clientCounter += 1;
        this.clientID = clientCounter;
        kill = false;
        name = null;

        try {
            sender = new ObjectOutputStream(this.socket.getOutputStream());
            reader = new ObjectInputStream(this.socket.getInputStream());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
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

    private void startMessageReader() {
        Thread t = new Thread(() -> {
            while (true) {
                Message object = null;
                if (kill) {
                    break;
                }
                try {
                    //System.out.println("ID: " + this.clientID);
                    if (reader == null) {
                        System.out.println("reader is null");
                    }

                    Object data = reader.readObject();
                    object = new Message(this, (String) data);
                } catch (IOException | ClassNotFoundException ex) {
                    if (ex.getMessage().equals("Connection reset")) {
                        break;
                    }
                    continue;
                }

                //if (name != null) {
                //System.out.println("<" + name + ", " + this.clientID + "> " + object.getData());
                //}
                
                if (object.getData().startsWith("n:")) {
                    name = object.getData().replaceAll("n:", "");
                    System.out.println("Client: " + name);
                } else if (object.getData().startsWith("l:")) {
                    object.setData(object.getData().replaceAll("l:", ""));
                    Server.receivedMessages.add(object);
                } else if (object.getData().equals("r:map")) {
                    // Phone requests a map
                    Platform.runLater(() -> {
                        sendMessage(new Message(this, "m:" + Map.instance.getImage()));
                    });
                } else if (object.getData().equals("r:color")) {
                    Color[] colors = new Color[]{Color.RED, Color.CYAN, Color.LIME, Color.YELLOW, Color.MAGENTA, Color.CYAN};
                    Color color = colors[this.clientID - 1];
                    sendMessage(new Message(this, String.format("c:#%02x%02x%02x", (int) color.getRed() * 255, (int) color.getGreen() * 255, (int) color.getBlue() * 255)));
                }

                //sendMessage(object);
                try {
                    Thread.sleep(10);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        t.start();
    }

    public String getName() {
        return name;
    }

    public void killPlayer() {
        kill = true;
    }

    public int getId() {
        return this.clientID;
    }
}
