package com.FIT3170.HealthMonitor.database;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/*
    This class keeps track of ECG readings as they are received and uploads to the database every minute

    This is a singleton because there should only be one channel uploading readings to the database

    1. call start() to begin periodic uploads
    2. use addData() to add packets (5 secs of data)
    3. call stop() when session is finished
 */

public class ReadingUploader {

    private static ReadingUploader instance = null;
    private final FirebaseFirestore db;

    private static final String START_TIME_KEY = "startTime";
    private static final String DATA_KEY = "data";
    private static final String PATIENT_KEY = "patientId";
    private final HashMap<String, Object> toUpload = new HashMap<>();
    private HashMap<String, Object> lastUpload = new HashMap<>();
    private boolean isUploading = false;

    private Date currentStartTime;
    private final ArrayList<DataPoint> currentData = new ArrayList<>();
    private static final int ONE_MIN_IN_MILLIS = 60000;
    private final String patientId;
    private final Timer uploadTimer;


    private ReadingUploader() {
        db = FirebaseFirestore.getInstance();
        // get current userId
        patientId = UserProfile.getUid();
        uploadTimer = new Timer();
    }

    public static ReadingUploader getInstance() {
        // create instance if doesn't exist
        if (instance == null) {
            instance = new ReadingUploader();
        }

        return instance;
    }

    private void startUploading() {
        Log.i("d", "Starting To Upload");
        uploadTimer.schedule(new Upload(), ONE_MIN_IN_MILLIS, ONE_MIN_IN_MILLIS);
        isUploading = true;
    }

    private void stopUploading() {
        Log.i("d", "Stopping Upload");
        // stop timer
        uploadTimer.cancel();
        isUploading = false;
    }


    /**
     * Receives incoming EGC data contained in a DataPacket
     */
    public void addData(DataPacket dataPacket) {

        // check if uploading - will not be uploading if this is the first data packet
        if (!isUploading){
            startUploading();
        }

        Log.i("d", "Adding data to uploader");

        // if current start time if null then data has just been uploaded
        if (currentStartTime == null) {
            currentStartTime = new Date();
        }

        // add packet to data to upload
        currentData.addAll(dataPacket.getData());

    }


    class Upload extends TimerTask {
        public void run() {
            Log.i("d", "Uploading");

            toUpload.put(PATIENT_KEY, patientId);
            toUpload.put(START_TIME_KEY, currentStartTime);
            toUpload.put(DATA_KEY, currentData);

            //upload to firebase
            if (currentStartTime != null & !currentData.isEmpty()) {

                db
                        .collection("ECGData")
                        .add(toUpload)
                        .addOnCompleteListener(l -> {
                            if (l.isSuccessful()) {
                                Log.i("d", "upload successful");
                                // update last uploaded
                                lastUpload = toUpload;
                                // clear current data
                                currentData.clear();
                                currentStartTime = null;
                            } else {
                                Log.i("d", "upload unsuccessful");
                            }
                        });
            } else {
                Log.i("d", "there is nothing to upload");

                // this is the end of that data stream so stop uploading
                stopUploading();

            }
        }
    }
}

