package com.design4nature.experimentvxtablet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private IntentFilter filter;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        filter = new IntentFilter();
        filter.addAction("EndGame"); // Location has been updated

        // Handle the events of the broadcasts. Updates opened fragments if needed
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String str = intent.getAction();
                if (str.equalsIgnoreCase("StartCountDown")) {
                    Intent i = new Intent(GameActivity.this, EndActivity.class);
                    startActivity(i);
                }
            }
        };
        registerReceiver(receiver, filter);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }
}
