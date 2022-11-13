package com.aigs.serviceone.helpers;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@StringDef({SmsModes.MODE_INBOX,SmsModes.MODE_DRAFT,SmsModes.MODE_OUTBOX})
public @interface SmsModes {
    String MODE_INBOX = "content://sms/inbox";
    String MODE_OUTBOX = "content://sms/sent";
    String MODE_DRAFT = "content://sms/draft";
}
