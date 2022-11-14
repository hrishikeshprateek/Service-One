package com.aigs.serviceone.helpers;

import java.io.File;

public interface ContactsPayloadListner {
    void onDataExtracted(File file, String rawData);
}
