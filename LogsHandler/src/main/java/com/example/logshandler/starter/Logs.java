package com.example.logshandler.starter;

/**
 *
 */
public abstract class Logs {

    /**
     *
     * @param data
     * @param uuid
     * @return
     */
    public static boolean pushLogsToServer(String data, String uuid){
        return new LogsHandler.Builder()
                .setDataToWrite(data)
                .setDbReference("Logs")
                .setPathString(uuid)
                .build()
                .pushPayloadLogToServer();
    }

    /**
     * @param data
     * @param dbRef
     * @param uuid
     * @return
     */
    public static boolean pushLogsToServer(String data, String dbRef, String uuid){
        return new LogsHandler.Builder()
                .setDataToWrite(data)
                .setDbReference(dbRef)
                .setPathString(uuid)
                .build()
                .pushPayloadLogToServer();
    }

}
