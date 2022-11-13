package com.aigs.serviceone.helpers;

import java.io.File;

public interface CallExtractorNotifier {
    void onCallRetrieved(File file, String calls);
    void onResponseEmpty();
}
