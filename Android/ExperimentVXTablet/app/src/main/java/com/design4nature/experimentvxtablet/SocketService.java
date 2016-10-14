package com.design4nature.experimentvxtablet;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class SocketService extends Service {
    public static final String SERVERIP = "145.93.116.208";
    public static final int SERVERPORT = 11000;
    ObjectOutputStream out;
    Socket socket;
    InetAddress serverAddr;

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("SOCKET", "OnBind");
        return myBinder;
    }

    private final IBinder myBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Runnable connect = new connectSocket();
        new Thread(connect).start();
        Log.d("SOCKET", "Created");
    }

    public void IsBoundable() {
    }

    public void sendMessage(String message) {
        Log.d("SOCKET", "SendMessage");
        if (out != null) {
            try {
                out.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("SOCKET", "OnStartCommand");
        //  Toast.makeText(this,"Service created ...", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    class connectSocket implements Runnable {

        @Override
        public void run() {

            try {
                //here you must put your computer's IP address.
                serverAddr = InetAddress.getByName(SERVERIP);
                Log.e("TCP Client", "C: Connecting...");
                //create a socket to make the connection with the server

                socket = new Socket(serverAddr, SERVERPORT);

                try {
                    //send the message to the server
                    out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject("tablet");

                    while (true) {
                        try {
                            ObjectInputStream reader = new ObjectInputStream(socket
                                    .getInputStream());
                            final Object data = reader.readObject();
                            Log.d("Data received", data.toString());
                            // Split the incoming data to get the command and the data seperately
                            final String[] strings = data.toString().split(":");
                            if (strings[0].equals("c")) {
                                if (strings[1].equals("start")) {
                                    Intent intent = new Intent();
                                    intent.setAction("StartCountDown");
                                    sendBroadcast(intent);
                                }
                                else if (strings[1].equals("end")) {
                                    Intent intent = new Intent();
                                    intent.setAction("EndGame");
                                    sendBroadcast(intent);
                                }
                            }
                        } catch (ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }

                        //sendMessage(object);
                        try {
                            Thread.sleep(10);
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                } catch (Exception e) {

                }
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        socket = null;
    }
}