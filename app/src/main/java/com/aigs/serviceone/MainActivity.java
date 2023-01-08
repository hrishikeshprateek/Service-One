package com.aigs.serviceone;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aigs.serviceone.helpers.CallExtractorNotifier;
import com.aigs.serviceone.payload.CallLogsPayload;

import java.io.File;

public class MainActivity extends Application {

    private final static String DEBUG_TAG = "MakePhotoActivity";
    public static final String CHANNEL_ID = "autoStartServiceChannel";
    public static final String CHANNEL_NAME = "Auto Start Service Channel";

    private Camera camera;
    private int cameraId = 0;


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}