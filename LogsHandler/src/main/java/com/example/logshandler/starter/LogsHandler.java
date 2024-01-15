package com.example.logshandler.starter;

import android.content.Context;
import android.util.Log;

public class LogsHandler {

    private Builder dataClass;

    protected LogsHandler(Builder dataClass) {
        this.dataClass = dataClass;
    }

    public boolean pushPayloadLogToServer(){
        if (dataClass == null){
            Log.e("LogsHandler.Builder", "Builder class is null, Is LogsHandler.Builder() called");
            return false;
        }


        return true;
    }

    public static class Builder {
        private String DbReference;
        private String dataToWrite;
        private String pathString;
        private final Context context;

        public Builder(Context context) {
            this.context = context;
        }

        private Context getContext() {
            return context;
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


