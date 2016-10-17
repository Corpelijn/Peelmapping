package com.design4nature.experimentvxtablet;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends Activity {

    private SocketService mBoundService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            mBoundService = ((SocketService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            mBoundService = null;
        }
    };
    private boolean mIsBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context
                .MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        Button btnConnect = (Button) findViewById(R.id.btn_connect);
        final EditText etIpAddress = (EditText) findViewById(R.id.et_ip_address);
        etIpAddress.setText(sharedPreferences.getString("IPAddress", ""));

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("IPAddress", etIpAddress.getText().toString()).apply();
                doBindService();
                startService(new Intent(SettingsActivity.this, SocketService.class));

                Intent i = new Intent(SettingsActivity.this, DemoActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        doUnbindService();
    }

    private void doBindService() {
        bindService(new Intent(SettingsActivity.this, SocketService.class), mConnection, Context
                .BIND_AUTO_CREATE);
        mIsBound = true;
        if (mBoundService != null) {
            mBoundService.IsBoundable();
            doUnbindService();
        }
    }

    private void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }
}
