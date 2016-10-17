/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design4natureserver;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bas
 */
public class Server implements IListener {

    public static List<Client> connectedClients;
    public static List<Client> killedClients;

    public static boolean gameStarted = false;

    private static boolean canConnect = true;
    //public static List<Message> receivedMessages;

    public Server() {
        connectedClients = new ArrayList<>();
        killedClients = new ArrayList();
        //Server.receivedMessages = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket listener = new ServerSocket(11000)) {
            //writeMessage("Server is online on IP: " + listener.getInetAddress());
            writeMessage("Server is online on the following IP's: ");
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    if (Inet4Address.class == i.getClass()) {
                        writeMessage(i.getHostAddress());
                    }
                }
            }

            while (canConnect) {
                System.out.println("Waiting for clients to connect...");

                Socket socket = listener.accept();
                writeMessage("Client " + socket.getInetAddress() + " connected");

                try {

                    Client c = new Client(socket);
                    connectedClients.add(c);
                    c.addListener(this);
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

    public static void broadcast(String message) {
        for (Client client : connectedClients) {
            if (client.isTabled()) {
                client.sendMessage(message);
            }
        }
    }

    public static void killClient(int id) {
        for (Client c : connectedClients) {
            if (c.getId() == id) {
                c.killPlayer();
            }
        }
    }

    @Override
    public void onReady() {
        // Check if all the clients have send the message "ready"
        boolean allReady = true;
        for (Client client : Server.connectedClients) {
            if (!client.isReady()) {
                allReady = false;
                break;
            }
        }

        // If one of the client is not ready, return
        if (!allReady) {
            return;
        }

        // When all clients are ready, disable the possibility to let new clients connect
        canConnect = false;
        // Send a message to all clients to start the intro video
        for (Client client : Server.connectedClients) {
            client.sendMessage("c:ready");
        }

        // Check if 15 seconds have past
        Date currentTime = new Date();
        Calendar newTime = Calendar.getInstance();
        newTime.add(Calendar.SECOND, 15);

        while (currentTime.before(newTime.getTime())) {
            currentTime = new Date();
            System.out.println(currentTime);
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // If the countdown is finished send the message start
        for (Client client : Server.connectedClients) {
            client.sendMessage("c:start");
        }

        gameStarted = true;
    }

    @Override
    public void onKillPlayer() {
        for (Client client : connectedClients) {
            if (!client.isTabled()) {
                if (client.isDead()) {
                    if (!killedClients.contains(client)) {
                        killedClients.add(client);
                    }
                }
            }
        }

        if (killedClients.size() >= connectedClients.size() / 2 - 1) {
            for (Client client : connectedClients) {
                client.sendMessage("c:end");
                for (Client c : connectedClients) {
                    if (!c.isTabled()) {
                        client.sendMessage("r:" + (connectedClients.size() / 2 - killedClients.indexOf(c)) + "," + c.toString());
                    }
                }
            }
        }
    }
}
