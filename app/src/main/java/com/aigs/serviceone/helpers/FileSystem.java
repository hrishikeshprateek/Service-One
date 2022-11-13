package com.aigs.serviceone.helpers;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;

public class FileSystem {

    private Context context;
    private @SmsModes String mode;

    public FileSystem(Context context) {
        this.context = context;
    }

    public static FileSystem createInstance(Context context){
        return new FileSystem(context);
    }

    public FileSystem smsOperationType(@SmsModes String mode){
        this.mode = mode;
        return this;
    }

    public synchronized File writeSmsData(String data){
        String name = "null";
        switch (mode){
            case SmsModes.MODE_INBOX:
                name = "sms_received.json";
                break;
            case SmsModes.MODE_OUTBOX:
                name = "sms_sent.json";
                break;
            case SmsModes.MODE_DRAFT:
                name = "sms_draft.json";
                break;
        }

        File file = new File(context.getFilesDir(), "text");
        File gpxfile = null;
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            gpxfile = new File(file, name);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return gpxfile;
    }

    public synchronized File writeCallData(String data){
        File file = new File(context.getFilesDir(), "text");
        File gpxfile = null;
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            gpxfile = new File(file,"calls_history.json");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return gpxfile;
    }

}
