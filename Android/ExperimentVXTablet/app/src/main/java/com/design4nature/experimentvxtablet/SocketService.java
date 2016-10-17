package com.design4nature.experimentvxtablet;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class SocketService extends Service {
    public String serverIp = "";
    public static final int SERVERPORT = 11000;
    public ArrayList<Player> players;
    private ObjectOutputStream out;
    private Socket socket;
    private InetAddress serverAddr;
    private ObjectInputStream reader;

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
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context
                .MODE_PRIVATE);
        serverIp = sharedPreferences.getString("IPAddress", "");
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
                serverAddr = InetAddress.getByName(serverIp);
                Log.e("TCP Client", "C: Connecting...");
                //create a socket to make the connection with the server

                socket = new Socket(serverAddr, SERVERPORT);

                try {
                    //send the message to the server
                    out = new ObjectOutputStream(socket.getOutputStream());
                    reader = new ObjectInputStream(socket.getInputStream());
                    out.writeObject("tablet");

                    while (true) {
                        try {
                            Object data = reader.readObject();
                            Log.d("Data received", data.toString());
                            // Split the incoming data to get the command and the data seperately
                            String[] strings = data.toString().split(":");
                            if (strings[0].equals("c")) {
                                if (strings[1].equals("ready")) {
                                    Intent intent = new Intent();
                                    intent.setAction("StartCountDown");
                                    sendBroadcast(intent);
                                }
                                else if (strings[1].equals("start")) {
                                    Intent intent = new Intent();
                                    intent.setAction("StartGame");
                                    sendBroadcast(intent);
                                }
                                else if (strings[1].equals("end")) {
                                    Intent intent = new Intent();
                                    intent.setAction("EndGame");
                                    sendBroadcast(intent);
                                }
                            }
                            else if (strings[0].equals("l")) {
                                // x,y,id
                                String updateInfo[] = strings[1].split(",");

                                for (Player p : players) {
                                    if (p.getId() == Integer.parseInt(updateInfo[2])) {
                                        int x = Integer.parseInt(updateInfo[0]);
                                        int y = Integer.parseInt(updateInfo[1]);
                                        y = -y;
                                        p.addPoint(new Point(x, y));

                                        Log.d("LOCATIONUPDATE", "Updated player " + p.getId() +
                                                " to location " + x + "," +
                                                y);
                                        break;
                                    }
                                }

                                Intent intent = new Intent();
                                intent.setAction("UpdateLocation");
                                sendBroadcast(intent);
                            }
                            else if (strings[0].equals("p")) {
                                String playerInfo[] = strings[1].split(",");
                                Player p = new Player(Integer.parseInt(playerInfo[0]),
                                        playerInfo[1], Color.parseColor(playerInfo[2]));
                                players.add(p);
                            }

                            else if (strings[0].equals("r")){
                                Intent intent = new Intent();
                                intent.setAction("Rank");
                                intent.putExtra("Rank", strings[1]);
                                intent.putExtra("TeamName", strings[2]);
                                intent.putExtra("Name1", strings[3]);
                                intent.putExtra("Name2", strings[4]);
                                sendBroadcast(intent);
                            }
                        } catch (NullPointerException ex) {
                            //ignore
                        } catch (EOFException ex) {
                            //ignore
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        try {
                            Thread.sleep(10);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
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