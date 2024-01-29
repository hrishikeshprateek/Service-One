package com.example.uniqueidmanager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

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

}
