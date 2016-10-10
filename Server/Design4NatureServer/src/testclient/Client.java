/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testclient;

import shared.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bas
 */
public class Client {

    private Socket socket;
    private ObjectOutputStream sender;
    private ObjectInputStream reader;

    private int clientID = -1;

    public Client() {
    }

    public void start() throws IOException {
        Scanner input = new Scanner(System.in);
        System.out.print("Client: Enter IP address of server: ");
        String serverAddress = input.nextLine();

        socket = new Socket(serverAddress, 11000);
        sender = new ObjectOutputStream(this.socket.getOutputStream());
        reader = new ObjectInputStream(this.socket.getInputStream());

        startMessageReader();
        startMessageWriter();
    }

    private void startMessageWriter() {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Scanner input = new Scanner(System.in);
                    String newMessage = input.nextLine();

                    sender.writeObject(newMessage);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.start();
    }

    private void startMessageReader() {
        Thread t = new Thread(() -> {
            while (true) {
                Message object = null;
                try {
                    object = (Message) reader.readObject();
                } catch (IOException | ClassNotFoundException ex) {
                    continue;
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.start();
    }

    public void sendMessage(Message message) {
        try {
            sender.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getClientId() {
        return this.clientID;
    }
}
