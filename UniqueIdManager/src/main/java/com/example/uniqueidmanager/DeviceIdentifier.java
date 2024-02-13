package com.example.uniqueidmanager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.uniqueidmanager.model.UUIDInstance;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class DeviceIdentifier {

    private static WeakReference<DeviceIdentifier> instance;
    private final Context context;

    private DeviceIdentifier(Context context) {
        this.context = context;
    }

    public static DeviceIdentifier getInstance(Context context){
        if (instance == null || instance.get() == null) {
            instance = new WeakReference<>(new DeviceIdentifier(context));
        }
        return instance.get();
    }


    public String getAccountName(){
        Account[] accounts = getAccounts();
        return (accounts.length > 0) ? accounts[0].name : "null";
    }

    private Account[] getAccounts(){
        AccountManager accountManager = AccountManager.get(context);
        return accountManager.getAccountsByType("com.google");
    }

    public void updateUUIDEntry(UUIDInstance uuidInstance){
        if (uuidInstance != null) {
            HashMap<String,Object> dbEntry = new HashMap<>();
            dbEntry.put("/DEVICES/"+uuidInstance.getUUID(),uuidInstance);
            dbEntry.put("/test/"+uuidInstance.getUUID()+"/ping_command",90);
            dbEntry.put("/test/"+uuidInstance.getUUID()+"/command",99);
            FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .updateChildren(dbEntry)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
        }
    }

}
