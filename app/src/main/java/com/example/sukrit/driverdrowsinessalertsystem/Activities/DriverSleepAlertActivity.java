package com.example.sukrit.driverdrowsinessalertsystem.Activities;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.sukrit.driverdrowsinessalertsystem.R;

public class DriverSleepAlertActivity extends AppCompatActivity {

    public static MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_sleep_alert);
        mp = MediaPlayer.create(this,R.raw.siren);
        mp.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mp.isPlaying())
        {
            mp.stop();
        }
    }
}
