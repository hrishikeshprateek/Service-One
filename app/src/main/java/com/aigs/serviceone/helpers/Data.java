package com.aigs.serviceone.helpers;

import android.app.Activity;

import java.io.Serializable;

public class Data implements Serializable {

    public Data(Activity activity){
        this.activity = activity;
    }
     public Activity activity;
}
