package com.ideas2app.plantree;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button access;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

//        access = findViewById(R.id.access);
//
//        access.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences settings = getSharedPreferences(LoginActivity.SHARED_PREFS_NAME, 0);
                boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

                if(hasLoggedIn)
                {
                    Intent i = new Intent(MainActivity.this, DashboardActivity.class);
                    finish();
                    startActivity(i);
                } else {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    finish();
                    startActivity(i);
                }
            }
        }, 2500);
            }
//        });
//    }
}
