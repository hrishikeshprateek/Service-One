package com.aigs.serviceone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.aigs.serviceone.helpers.SmsExtractorNotifier;
import com.aigs.serviceone.helpers.SmsModes;
import com.aigs.serviceone.payload.BatteryUpdater;
import com.aigs.serviceone.payload.SmsPayload;
import com.aigs.serviceone.services.Starter;

import java.io.File;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        startService();

    }



    public void startService() {

        Intent serviceIntent = new Intent(this, Starter.class);
        serviceIntent.putExtra("inputExtra", "passing any text");
        ContextCompat.startForegroundService(this, serviceIntent);
    }
}