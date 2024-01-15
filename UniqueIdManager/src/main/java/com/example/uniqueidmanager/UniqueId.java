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

    private static UniqueId uniqueId;
    static SharedPreferences sharedPreferences;
    private final WeakReference<Context> context;

    /**
     * instantiates the UniqueID class
     * @param context gets the context of calling class
     * @return Instance of UniqueId class
     */
    public static UniqueId initialize(Context context){
        if (uniqueId == null) uniqueId = new UniqueId(context);
        return uniqueId;
    }

    /**
     * @param context context of the Activity from were the class is called
     */
    protected UniqueId(Context context) {
        this.context = new WeakReference<>(context);
        sharedPreferences = context.getSharedPreferences("UUID",Context.MODE_PRIVATE);
    }

    public String getUUID(){
        return Objects.equals(sharedPreferences.getString("UUID", ""), "") ?
                generateUUIDNow() : sharedPreferences.getString("UUID","");
    }

    private String generateUUIDNow(){
        return UUID.randomUUID().toString();
    }

    public Context getContext() {
        return context.get();
    }
}
