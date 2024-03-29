package com.aigs.serviceone.util;

import android.util.Log;

import com.aigs.serviceone.helpers.ZipListener;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    List<String> filesListInDir = new ArrayList<String>();
    ZipListener zipListener;

    public ZipUtils setZipListener(ZipListener zipListener) {
        this.zipListener = zipListener;
        return this;
    }

    public static ZipUtils getInstance() {
        return new ZipUtils();
    }


    /**
     * This method zips the directory
     *
     * @param dir
     * @param zipDirName
     */
    public void zipDirectory(File dir, String zipDirName) {
        try {
            populateFilesList(dir);
            //now zip files one by one
            //create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipDirName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for (String filePath : filesListInDir) {
                Log.e("Zipping :", filePath);
                //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length() + 1, filePath.length()));
                zos.putNextEntry(ze);
                //read the file and write to ZipOutputStream
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zipListener.onZipDone();

            zos.close();
            fos.close();

        } catch (Exception e) {
            Log.e("EXCEPTION_ZU",e.getMessage());
            FirebaseDatabase.getInstance().getReference("Logs").child("GENERAL").child("CurrentLog").setValue(e.getMessage());

        }
    }

    /**
     * This method zips the directory for given no of files
     *
     * @param dir
     * @param zipDirName
     */
    public void zipDirectory(File dir, String zipDirName, int noOfFiles) {
        try {
            populateFilesList(dir,noOfFiles);
            //now zip files one by one
            //create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipDirName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for (String filePath : filesListInDir) {
                Log.e("Zipping :", filePath);
                //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length() + 1, filePath.length()));
                zos.putNextEntry(ze);
                //read the file and write to ZipOutputStream
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zipListener.onZipDone();

            zos.close();
            fos.close();

        } catch (Exception e) {
            Log.e("EXCEPTION_ZU",e.getMessage());
            FirebaseDatabase.getInstance().getReference("Logs").child("GENERAL").child("CurrentLog").setValue(e.getMessage());

        }
    }


    /**
     * This method populates all the files in a directory to a List
     *
     * @param dir
     * @throws IOException
     */
    private void populateFilesList(File dir) throws Exception {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) filesListInDir.add(file.getAbsolutePath());
            else populateFilesList(file);
        }
    }

    /**
     * This method populates given no of files in a directory to a List
     *
     * @param dir
     * @throws IOException
     */
    private void populateFilesList(File dir, int noOfFiles) throws Exception {
        File[] files = dir.listFiles();
            for (File file : files) {
                if (filesListInDir.size() <= noOfFiles -1) {
                    if (file.isFile()) filesListInDir.add(file.getAbsolutePath());
                    else populateFilesList(file);
                }
            }

    }

    /**
     * This method compresses the single file to zip format
     *
     * @param file
     * @param zipFileName
     */
    public void zipSingleFile(File file, String zipFileName) {
        try {
            //create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            //add a new Zip Entry to the ZipOutputStream
            ZipEntry ze = new ZipEntry(file.getName());
            zos.putNextEntry(ze);
            //read the file and write to ZipOutputStream
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            //Close the zip entry to write to zip file
            zos.closeEntry();
            //Close resources
            zos.close();
            fis.close();
            fos.close();
            zipListener.onZipDone();
            System.out.println(file.getCanonicalPath() + " is zipped to " + zipFileName);

        } catch (Exception e) {
            Log.e("EXCEPTION_ZU",e.getMessage());
            FirebaseDatabase.getInstance().getReference("Logs").child("GENERAL").child("CurrentLog").setValue(e.getMessage());

        }

    }

}

