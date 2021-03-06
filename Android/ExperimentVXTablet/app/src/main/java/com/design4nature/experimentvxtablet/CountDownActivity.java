package com.design4nature.experimentvxtablet;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class CountDownActivity extends AppCompatActivity {
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

    private IntentFilter filter;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down);

        doBindService();

        filter = new IntentFilter();
        filter.addAction("StartGame");

        // Handle the events of the broadcasts. Updates opened fragments if needed
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String str = intent.getAction();
                if (str.equalsIgnoreCase("StartGame")) {
                    Intent i = new Intent(CountDownActivity.this, GameActivity.class);
                    startActivity(i);
                }
            }
        };
        registerReceiver(receiver, filter);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    private void doBindService() {
        bindService(new Intent(CountDownActivity.this, SocketService.class), mConnection, Context
                .BIND_AUTO_CREATE);
        mIsBound = true;
        if (mBoundService != null) {
            mBoundService.IsBoundable();
        }
    }

    private void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    public void startGame(View view) {
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
    }
}
