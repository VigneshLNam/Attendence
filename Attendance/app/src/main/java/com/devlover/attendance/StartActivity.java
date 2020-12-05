package com.devlover.attendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
    ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        pBar = (ProgressBar)findViewById(R.id.pBar);
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String log = sharedPreferences.getString("check_log","false");
        final String username = sharedPreferences.getString("username","no");
        final String password = sharedPreferences.getString("pword","no");
        if(log.equals("true")){
            pBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 1000);
        }else{
            pBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(StartActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 1000);
        }
    }
}
