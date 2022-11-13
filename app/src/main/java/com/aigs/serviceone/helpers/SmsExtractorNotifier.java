package com.aigs.serviceone.helpers;

import java.io.File;

public interface SmsExtractorNotifier {
    void onSmsRetrieved(File file, String sms);
    void onResponseEmpty();
}
