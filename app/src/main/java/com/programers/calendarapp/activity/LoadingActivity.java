package com.programers.calendarapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.programers.calendarapp.R;
import com.victor.loading.rotate.RotateLoading;

public class LoadingActivity extends Activity {

    RotateLoading rotateLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        rotateLoading = (RotateLoading) findViewById(R.id.rotateLoading);
        rotateLoading.start();

        startLoading();
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1300);
    }
}
