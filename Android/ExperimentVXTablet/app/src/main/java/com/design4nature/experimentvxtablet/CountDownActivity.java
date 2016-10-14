package com.design4nature.experimentvxtablet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class CountDownActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down);
    }

    public void startGame(View view) {
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
    }
}
