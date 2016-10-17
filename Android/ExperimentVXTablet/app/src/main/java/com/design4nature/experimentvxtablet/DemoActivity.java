package com.design4nature.experimentvxtablet;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
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
            mBoundService.players = new ArrayList<>();
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
    private Canvas canvas;
    private LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        doBindService();

        filter = new IntentFilter();
        filter.addAction("StartCountDown");
        filter.addAction("UpdateLocation");

        // Handle the events of the broadcasts. Updates opened fragments if needed
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String str = intent.getAction();
                if (str.equalsIgnoreCase("StartCountDown")) {
                    Intent i = new Intent(DemoActivity.this, CountDownActivity.class);
                    startActivity(i);
                }
                else if (str.equalsIgnoreCase("UpdateLocation")) {
                    draw();
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
        Bitmap bg = Bitmap.createBitmap(1200, 800, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bg);
        canvas.drawColor(Color.WHITE);
        ll = (LinearLayout) findViewById(R.id.ll_canvas);
        ll.setBackgroundDrawable(new BitmapDrawable(bg));
    }

    private void draw() {
        for (Player p : mBoundService.players) {
            Point lastPoint = null;
            Paint paint = new Paint();
            paint.setStrokeWidth(5);
            paint.setColor(p.getColor());
            for (Point point : p.getPoints()) {
                if (lastPoint == null) {
                    lastPoint = point;
                }
                else {
                    canvas.drawLine(canvas.getWidth() / 100 * 50 + lastPoint.x * canvas.getWidth
                            () / 100, canvas.getHeight() / 100 * 50 + lastPoint.y * canvas
                            .getHeight() / 100, canvas.getWidth() / 100 * 50 + point.x * canvas
                            .getWidth() / 100, canvas.getHeight() / 100 * 50 + point.y * canvas
                            .getHeight() / 100, paint);

                    Log.d("CANVAS", "Drew line");

                    lastPoint = point;

                    ll.invalidate();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        doUnbindService();
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
}
