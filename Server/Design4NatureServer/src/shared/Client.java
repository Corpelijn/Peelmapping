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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
            sender.writeObject(message);
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

                if (name == null) {
                    name = object.getData();
                    System.out.println(name);
                    continue;
                }
                Server.receivedMessages.add(object);
                //System.out.println("<" + name + ", " + this.clientID + "> " + object.getData());

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
