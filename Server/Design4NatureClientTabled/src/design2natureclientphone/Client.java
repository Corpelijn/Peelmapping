/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design2natureclientphone;

import design4natureserver.Message;
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

    public Client() {
    }

    public void start() {
        try {
            Scanner input = new Scanner(System.in);
            System.out.print("Client: Enter IP address of server: ");
            String serverAddress = input.nextLine();

            socket = new Socket(serverAddress, 11000);
            sender = new ObjectOutputStream(this.socket.getOutputStream());
            reader = new ObjectInputStream(this.socket.getInputStream());

            startMessageReader();
            startMessageWriter();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void startMessageWriter() {
        Thread t = new Thread(() -> {
            sendMessage("phone");
            sendMessage("c:ready");
            while (true) {
                try {
                    Scanner input = new Scanner(System.in);
                    String newMessage = input.nextLine();

                    sender.writeObject(newMessage);

                    Thread.sleep(10);
                } catch (InterruptedException | IOException ex) {
                    System.out.println(ex.getMessage());
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
                    Object data = reader.readObject();

                    if (data instanceof String) {
                        System.out.println(data.toString());
                        if(data.toString().startsWith("c:start" ))
                        {
                            sendMessage("r:color");
                        }
                    } else {
                        object = (Message) data;
                    }

                } catch (IOException | ClassNotFoundException ex) {
                    continue;
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        t.start();
    }

    public void sendMessage(Message message) {
        try {
            sender.writeObject(message);

        } catch (IOException ex) {
            Logger.getLogger(Client.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendMessage(String message) {
        try {
            sender.writeObject(message);

        } catch (IOException ex) {
            Logger.getLogger(Client.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

}
