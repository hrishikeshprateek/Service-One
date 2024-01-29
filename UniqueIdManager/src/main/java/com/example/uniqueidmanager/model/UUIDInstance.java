package com.example.uniqueidmanager.model;

public class UUIDInstance {

    private String UUID;
    private String lastStartedTime;
    private String primaryAccountName;

    public UUIDInstance() {
    }

    public UUIDInstance(String UUID, String lastStartedTime, String primaryAccountName) {
        this.UUID = UUID;
        this.lastStartedTime = lastStartedTime;
        this.primaryAccountName = primaryAccountName;
    }

    public String getUUID() {
        return UUID;
    }

    public String getLastStartedTime() {
        return lastStartedTime;
    }

    public String getPrimaryAccountName() {
        return primaryAccountName;
    }
}
