package com.example.logshandler.starter;

import java.util.Objects;

/**
 * @author hrishikeshprateek
 */
public abstract class Logs {

    private static String lastLog = "";

    /**
     * @param data consists of log dat which needs to be written in the DB
     * @param uuid Devices unique identifier
     * @return Returns success status.
     */
    public static boolean pushLogsToServer(String data, String uuid) {
        boolean response = false;
        if (!Objects.equals(lastLog, data)) response = new LogsHandler.Builder()
                .setDataToWrite(data)
                .setDbReference("Logs")
                .setPathString(uuid)
                .build()
                .pushPayloadLogToServer();
        lastLog = data;
        return response;
    }

    /**
     * @param data consists of log dat which needs to be written in the DB.
     * @param dbRef Species the DB Location of the data to be written.
     * @param uuid Devices unique identifier.
     * @return Returns success status.
     */
    public static boolean pushLogsToServer(String data, String dbRef, String uuid) {
        boolean response = false;
        if (!Objects.equals(lastLog, data)) response = new LogsHandler.Builder()
                .setDataToWrite(data)
                .setDbReference(dbRef)
                .setPathString(uuid)
                .build()
                .pushPayloadLogToServer();
        lastLog = data;
        return response;
    }

}
