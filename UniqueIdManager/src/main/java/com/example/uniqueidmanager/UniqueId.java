package com.example.uniqueidmanager;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.UUID;

/**
 * @author hrishikeshprateek
 */
public class UniqueId {

    static SharedPreferences sharedPreferences;
    private final WeakReference<Context> context;

    /**
     * instantiates the UniqueID class
     * @param context gets the context of calling class
     * @return Instance of UniqueId class
     */
    public static UniqueId initialize(Context context){

        return new UniqueId(context);
    }

    /**
     * @param context context of the Activity from were the class is called
     */
    protected UniqueId(Context context) {
        this.context = new WeakReference<>(context);
        sharedPreferences = context.getSharedPreferences("UUID_STORE",Context.MODE_PRIVATE);
    }

    public String getUUID(){
        return Objects.equals(sharedPreferences.getString("UUID", ""), "") ?
                generateUUIDNow() : sharedPreferences.getString("UUID","");
    }

    private String generateUUIDNow(){
        String uuid = UUID.randomUUID().toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString("UUID",uuid);
        editor.apply();
        return uuid;
    }

    public Context getContext() {
        return context.get();
    }
}
