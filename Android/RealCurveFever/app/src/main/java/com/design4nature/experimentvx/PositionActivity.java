package com.design4nature.experimentvx;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class PositionActivity extends AppCompatActivity {

    private final int SERVERPORT = 11000;
    private Socket socket;
    private String serverIp;
    private String name;

    private TextView tvLongitude;
    private TextView tvLatitude;
    private TextView tvError;
    private ObjectOutputStream out;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Geen toegang tot GPS", Toast.LENGTH_SHORT).show();
            return;
        }

        tvLatitude = (TextView) findViewById(R.id.tv_latitude);
        tvLongitude = (TextView) findViewById(R.id.tv_longitude);
        tvError = (TextView) findViewById(R.id.tv_error);

        //KalmanLocationManager locationManager = new KalmanLocationManager(this);
        LocationManager locationManager = (LocationManager) getSystemService(Context
                .LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();

//        locationManager.requestLocationUpdates(KalmanLocationManager.UseProvider.GPS_AND_NET,
//                100, 0, 0, locationListener, true);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                locationListener);

        final EditText etIpAddress = (EditText) findViewById(R.id.et_ip_address);
        final EditText etName = (EditText) findViewById(R.id.et_name);
        final Button btnStart = (Button) findViewById(R.id.btn_start);

        thread = new Thread(new ClientThread());
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverIp = etIpAddress.getText().toString();
                name = etName.getText().toString();
                if (etIpAddress.isEnabled()) {
                    if (serverIp != null) {
                        thread.start();
                    }
                    btnStart.setText("Stop");
                    etIpAddress.setEnabled(false);
                    etName.setEnabled(false);
                }
                else {
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
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            Log.d("Location updated: ", loc.getLatitude() + "," + loc.getLongitude());

            String longitude = "Longitude: " + loc.getLongitude();
            String latitude = "Latitude: " + loc.getLatitude();

            tvLatitude.setText(latitude);
            tvLongitude.setText(longitude);

            try {
                if (socket != null) {
                    out.writeObject(loc.getLatitude() + "," + loc.getLongitude());
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
                out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


