package com.example.logshandler.starter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LogsHandler {

    private final Builder dataClass;

    protected LogsHandler(Builder dataClass) {
        this.dataClass = dataClass;
    }

    /**
     * @return status of the update
     */
    public boolean pushPayloadLogToServer(){
        if (dataClass == null){
            Log.e("LogsHandler.Builder", "Builder class is null, Is LogsHandler.Builder() called");
            return false;
        }

        HashMap<String, Object> dbUpdate = new HashMap<>();
        dbUpdate.put(dataClass.getDbReference() + "/" +
                        dataClass.getPathString() + "/" +
                        System.currentTimeMillis() ,
                dataClass.getDataToWrite());

        FirebaseDatabase.getInstance()
                .getReference()
                .updateChildren(dbUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

        return true;
    }

    public static class Builder {
        private String DbReference;
        private String dataToWrite;
        private String pathString;

        public Builder() {
        }

        private String getDbReference() {
            return DbReference;
        }

        public Builder setDbReference(String dbReference) {
            DbReference = dbReference;
            return this;
        }

        private String getDataToWrite() {
            return dataToWrite;
        }

        public Builder setDataToWrite(String dataToWrite) {
            this.dataToWrite = dataToWrite;
            return this;
        }

        private String getPathString() {
            return pathString;
        }

        public Builder setPathString(String pathString) {
            this.pathString = pathString;
            return this;
        }

        public LogsHandler build(){
            return new LogsHandler(this);
        }
    }
}


