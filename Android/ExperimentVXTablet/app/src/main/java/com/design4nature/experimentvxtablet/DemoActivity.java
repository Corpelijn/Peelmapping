package com.design4nature.experimentvxtablet;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
/**
 * Created by Ruben on 14-10-2016.
 */
public class DemoActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_demo);

        doBindService();
        startService(new Intent(DemoActivity.this, SocketService.class));

        filter = new IntentFilter();
        filter.addAction("StartCountDown"); // Location has been updated

        // Handle the events of the broadcasts. Updates opened fragments if needed
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String str = intent.getAction();
                if (str.equalsIgnoreCase("StartCountDown")) {
                    Intent i = new Intent(DemoActivity.this, CountDownActivity.class);
                    startActivity(i);
                }
            }
        };
        registerReceiver(receiver, filter);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);



        Button btnStart = (Button) findViewById(R.id.btn_ready);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBoundService.sendMessage("c:ready");
            }
        });

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#CD5C5C"));
        Bitmap bg = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bg);
        canvas.drawColor(Color.GRAY);
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll_canvas);
        ll.setBackgroundDrawable(new BitmapDrawable(bg));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_demo, menu);
        return true;
    }

    private void doBindService() {
        bindService(new Intent(DemoActivity.this, SocketService.class), mConnection, Context
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_set_ip:

                AlertDialog.Builder alert = new AlertDialog.Builder(DemoActivity.this);
                final EditText edittext = new EditText(DemoActivity.this);
                alert.setTitle("Set IP address");

                alert.setView(edittext);
                alert.setPositiveButton("Set IP", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //What ever you want to do with the value
                        if (edittext.getText().toString() != null) {

                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                alert.show();

                return true;
        }
        return true;
    }
}
