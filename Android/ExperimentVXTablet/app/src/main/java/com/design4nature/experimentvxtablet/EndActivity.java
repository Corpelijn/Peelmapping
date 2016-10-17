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
import android.widget.TextView;

import java.util.ArrayList;

public class EndActivity extends AppCompatActivity {
    private SocketService mBoundService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            mBoundService = ((SocketService.LocalBinder) service).getService();
            mBoundService.players = new ArrayList<>();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            mBoundService = null;
        }
    };
    private IntentFilter filter;
    private BroadcastReceiver receiver;
    private boolean mIsBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        doBindService();

        final TextView tvWinner = (TextView) findViewById(R.id.tv_winner);
        final TextView tvSecond = (TextView) findViewById(R.id.tv_second);
        final TextView tvThird = (TextView) findViewById(R.id.tv_third);
        final TextView tvFourth = (TextView) findViewById(R.id.tv_fourth);

        final TextView tvWinnerNames = (TextView) findViewById(R.id.tv_winner_names);
        final TextView tvSecondNames = (TextView) findViewById(R.id.tv_second_names);
        final TextView tvThirdNames = (TextView) findViewById(R.id.tv_third_names);
        final TextView tvFourthNames = (TextView) findViewById(R.id.tv_fourth_names);

        filter = new IntentFilter();
        filter.addAction("EndGame");
        filter.addAction("UpdateLocation");

        // Handle the events of the broadcasts. Updates opened fragments if needed
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String str = intent.getAction();
                if (str.equalsIgnoreCase("Rank")) {
                    if (intent.getStringExtra("Rank").equals("1")) {
                        tvWinner.setText(String.format("Team %s heeft gewonnen!", intent
                                .getStringExtra("TeamName")));
                        tvWinnerNames.setText(String.format("%s en %s", intent.getStringExtra
                                ("Name1"), intent.getStringExtra("Name2")));
                    }
                    else if (intent.getStringExtra("Rank").equals("2")) {
                        tvSecond.setText(String.format("2. Team %s", intent.getStringExtra
                                ("TeamName")));
                        tvSecondNames.setText(String.format("%s en %s", intent.getStringExtra
                                ("Name1"), intent.getStringExtra("Name2")));
                    }
                    else if (intent.getStringExtra("Rank").equals("3")) {
                        tvThird.setText(String.format("3. Team %s", intent.getStringExtra
                                ("TeamName")));
                        tvThirdNames.setText(String.format("%s en %s", intent.getStringExtra
                                ("Name1"), intent.getStringExtra("Name2")));
                    }
                    else if (intent.getStringExtra("Rank").equals("4")) {
                        tvFourth.setText(String.format("4. Team %s", intent.getStringExtra
                                ("TeamName")));
                        tvFourthNames.setText(String.format("%s en %s", intent.getStringExtra
                                ("Name1"), intent.getStringExtra("Name2")));
                    }
                }
            }
        };
        registerReceiver(receiver, filter);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        doUnbindService();
    }

    private void doBindService() {
        bindService(new Intent(EndActivity.this, SocketService.class), mConnection, Context
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

    public void restartGame(View view) {
        Intent i = new Intent(EndActivity.this, DemoActivity.class);
        startActivity(i);
    }
}
