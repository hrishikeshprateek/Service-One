package com.aigs.serviceone;

import static android.Manifest.permission;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aigs.serviceone.services.Starter;

public class LauncherActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        startService();
        requestPermission();

    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                CAMERA,
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
                            showMessageGrantCancel("You need to allow access to both the permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(new String[]{ACCESS_FINE_LOCATION,
                                                    permission.READ_PHONE_STATE,
                                                    CAMERA,
                                                    permission.READ_SMS,
                                                    permission.WRITE_EXTERNAL_STORAGE,
                                                    permission.READ_CALL_LOG,
                                                    permission.READ_EXTERNAL_STORAGE,
                                                    permission.READ_CONTACTS,
                                            }, PERMISSION_REQUEST_CODE);
                                        }
                                    });

                        }

                    }
                }


                break;
        }

    }


    private void showMessageGrantCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LauncherActivity.this)
                .setMessage(message)
                .setPositiveButton("GRANT", okListener)
                .setNeutralButton("CANCEL", null)
                .setNegativeButton("GRANT FROM SETTINGS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        openSettingsDialog();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void openSettingsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LauncherActivity.this);
        builder.setTitle("Required Permissions");
        builder.setMessage("This app require permission to use awesome feature. Grant them in app settings.");
        builder.setPositiveButton("Take Me To SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
}