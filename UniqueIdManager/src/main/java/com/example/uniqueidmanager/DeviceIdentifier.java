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
        if (uuidInstance != null) FirebaseDatabase
                .getInstance()
                .getReference("DEVICES")
                .child(uuidInstance.getUUID())
                .setValue(uuidInstance)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

}
