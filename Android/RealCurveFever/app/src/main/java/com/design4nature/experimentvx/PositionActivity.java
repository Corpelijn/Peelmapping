package com.design4nature.experimentvx;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class PositionActivity extends AppCompatActivity implements StepListener,
        SensorEventListener {

    private static final int SERVERPORT = 11000;
    private Socket socket;
    private String serverIp;
    private String name;

    private TextView tvLocation;
    private TextView tvError;
    private ImageView ivMap;
    private ObjectOutputStream out;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);

        // Check GPS permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // If no permissions are granted, show toast message and stop loading activity
            Toast.makeText(this, "Geen toegang tot GPS", Toast.LENGTH_SHORT).show();
            return;
        }

        tvLocation = (TextView) findViewById(R.id.tv_location);
        ivMap = (ImageView) findViewById(R.id.iv_map);
        tvError = (TextView) findViewById(R.id.tv_error);

        // Use the KalmanLocationManager to use predictions on gps location (in beta)
        //KalmanLocationManager locationManager = new KalmanLocationManager(this);
        LocationManager locationManager = (LocationManager) getSystemService(Context
                .LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();

        // Request location updates as often as possible (0,0)
//        locationManager.requestLocationUpdates(KalmanLocationManager.UseProvider.GPS_AND_NET,
//                100, 0, 0, locationListener, true);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                locationListener);

        final EditText etIpAddress = (EditText) findViewById(R.id.et_ip_address);
        final EditText etName = (EditText) findViewById(R.id.et_name);
        final Button btnStart = (Button) findViewById(R.id.btn_start);

        // Create the thread for server communication
        thread = new Thread(new ClientThread());

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverIp = etIpAddress.getText().toString();
                name = etName.getText().toString();
                if (etIpAddress.isEnabled()) {
                    // Start sending data to server
                    if (serverIp != null) {
                        thread.start();
                    }
                    btnStart.setText("Stop");
                    etIpAddress.setEnabled(false);
                    etName.setEnabled(false);
                }
                else {
                    // Stop sending data to server
                    try {
                        socket.close();
                        out.close();
                        thread = new Thread(new ClientThread());
                    } catch (IOException e) {
                        tvError.setText(e.getMessage());
                        e.printStackTrace();
                    }
                    btnStart.setText("Start");
                    etIpAddress.setEnabled(true);
                    etName.setEnabled(true);
                }
                
            }
        });

        // Start detecting steps
//        StepDetector stepDetector = new StepDetector();
//        stepDetector.addStepListener(this);

        // Check proximitysensor to detect if device is in the user's pocket
        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onStep() {
//        stepCount++;
//        Log.d("STEP", String.valueOf(stepCount));
//        Toast.makeText(PositionActivity.this, "Step " + stepCount, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void passValue() {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            boolean inPocket = event.values[0] == 0;
            // If the device was just taken out of the user's pocket and there is a server
            // connection, request the map
            if (!inPocket && socket != null){
                try {
                    out.writeObject("r:map");
//                    inTimeOut = true;
//
//                      // Update the timeout boolean to prevent the user from excessively
//                      // requesting the map
//                    Handler mHandler = new Handler();
//                    Runnable mUpdateTimeTask = new Runnable() {
//                        public void run() {
//                            inTimeOut = false;
//                        }
//                    };
//                    mHandler.postDelayed(mUpdateTimeTask, 30000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            //Log.d("Location updated", loc.getLatitude() + "," + loc.getLongitude());

            String longitude = "Longitude: " + loc.getLongitude();
            String latitude = "Latitude: " + loc.getLatitude();

            tvLocation.setText(loc.getLatitude() + "," + loc.getLongitude());

            try {
                if (socket != null) {
                    // If there is a server connection, send the updated location to it
                    out.writeObject("l:" + loc.getLatitude() + "," + loc.getLongitude());
                    tvError.setText("");
                    Log.d("SOCKET", "sent location to server");
                }
            } catch (IOException e) {
                tvError.setText(e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    class ClientThread implements Runnable {

        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIp);
                socket = new Socket(serverAddr, SERVERPORT);

                // Open the stream to send data
                out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());

                // Send the server your name and a request to get the player's color
                out.writeObject("n:" + name);
                out.writeObject("r:color");

                // Always check for incoming data
                while (true) {
                    try {
                        final Object data = reader.readObject();
                        Log.d("Data received", data.toString());
                        // Split the incoming data to get the command and the data seperately
                        final String[] strings = data.toString().split(":");
                        // If the command is "c", update the color for the player
                        if (strings[0].equals("c")) {
                            runOnUiThread(new Thread(new Runnable() {
                                public void run() {
                                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable
                                            (Color.parseColor(strings[1])));
                                }
                            }));
                        }
                        // If the command is "m", show the map
                        else if (strings[0].equals("m")) {
                            runOnUiThread(new Thread(new Runnable() {
                                public void run() {
                                    byte[] decodedString = Base64.decode(strings[1], Base64
                                            .DEFAULT);

                                    // Create a bitmap of the Base64
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray
                                            (decodedString, 0, decodedString.length);
                                    ivMap.setImageBitmap(decodedByte);

                                    // Remove is again after 5 seconds
                                    Handler mHandler = new Handler();
                                    Runnable mUpdateTimeTask = new Runnable() {
                                        public void run() {
                                            ivMap.setImageBitmap(null);
                                        }
                                    };
                                    mHandler.postDelayed(mUpdateTimeTask, 5000);
                                }
                            }));
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


