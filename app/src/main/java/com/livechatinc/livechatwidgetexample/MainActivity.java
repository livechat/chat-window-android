package com.livechatinc.livechatwidgetexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void startCurrentExample(View view) {
        Intent intent = new Intent(this, LegacyActivity.class);

        startActivity(intent);
    }

    public void startLegacyExample(View view) {
        final Intent intent = new Intent(this, LegacyActivity.class);

        startActivity(intent);
    }
}
