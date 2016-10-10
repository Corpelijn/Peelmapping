package com.design4nature.realcurvefever;

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

    private TextView tvLongitude;
    private TextView tvLatitude;
    private TextView tvError;
    private ObjectOutputStream out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);

        tvLatitude = (TextView) findViewById(R.id.tv_latitude);
        tvLongitude = (TextView) findViewById(R.id.tv_longitude);
        tvError = (TextView) findViewById(R.id.tv_error);

        //KalmanLocationManager locationManager = new KalmanLocationManager(this);
        LocationManager locationManager = (LocationManager) getSystemService(Context
                .LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                 Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;

        }
//        locationManager.requestLocationUpdates(KalmanLocationManager.UseProvider.GPS_AND_NET,
//                100, 0, 0, locationListener, true);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                locationListener);


        final EditText etIpAddress = (EditText) findViewById(R.id.et_ip_address);
        final Button btnSetIpAddress = (Button) findViewById(R.id.btn_set_ip);
        final Thread thread = new Thread(new ClientThread());
        btnSetIpAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverIp = etIpAddress.getText().toString();
                if (etIpAddress.isEnabled()) {
                    Toast.makeText(PositionActivity.this, "IP Address set to " + serverIp, Toast
                              .LENGTH_SHORT).show();

                    if (serverIp != null) {
                        thread.start();
                    }
                    btnSetIpAddress.setText("Reset IP address");
                    etIpAddress.setEnabled(false);
                }
                else {
                    serverIp = null;
                    try {
                        out.close();
                    } catch (IOException e) {
                        tvError.setText(e.getMessage());
                        e.printStackTrace();
                    }
                    btnSetIpAddress.setText("Set IP address");
                    etIpAddress.setEnabled(true);
                }
            }
        });
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            Log.d("SOCKET", "Location updated");

            String longitude = "Longitude: " + loc.getLongitude();
            String latitude = "Latitude: " + loc.getLatitude();

            tvLatitude.setText(latitude);
            tvLongitude.setText(longitude);

            try {
                if (socket != null) {
                    out.writeObject(loc.getLatitude() + "," + loc.getLongitude());
                    tvError.setText("");
                    Log.d("SOCKET", "Sent to server");
                }
            } catch (IOException e){
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
            } catch (IOException e) {
                tvError.setText(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}


