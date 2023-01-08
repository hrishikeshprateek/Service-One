package com.aigs.serviceone.annotations;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@IntDef({ExecutionMode.MODE_CALL_TRIGGER_MODE,ExecutionMode.MODE_FOREGROUND_SERVICE})
public @interface ExecutionMode {
    int MODE_CALL_TRIGGER_MODE = 0;
    int MODE_FOREGROUND_SERVICE = 1;
}
