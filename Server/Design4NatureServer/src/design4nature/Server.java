/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design4nature;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.*;

/**
 *
 * @author Bas
 */
public class Server {

    private static List<Client> connectedClients;
    public static List<Message> receivedMessages;

    public Server() {
        connectedClients = new ArrayList<>();
        Server.receivedMessages = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket listener = new ServerSocket(11000)) {
            writeMessage("Server is online on IP: " + listener.getInetAddress());

            while (true) {
                System.out.println("Waiting for clients to connect...");

                Socket socket = listener.accept();
                writeMessage("Client " + socket.getInetAddress() + " connected");

                try {

                    Client c = new Client(socket, connectedClients);
                    connectedClients.add(c);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                } finally {
                    //socket.close();
                }

                Thread.sleep(10);
            }
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeMessage(String message) {
        System.out.println(message);
    }

    public static Message getNextReceivedMessage() {
        if (receivedMessages == null || receivedMessages.isEmpty()) {
            return null;
        }

        for (int i = 0; i < receivedMessages.size(); i++) {
            Message message = receivedMessages.get(i);
            receivedMessages.remove(i);
            return message;
        }

        return null;
    }

    public static void killClient(int id) {
        for (Client c : connectedClients) {
            if (c.getId() == id) {
                c.killPlayer();
            }
        }
    }
}
