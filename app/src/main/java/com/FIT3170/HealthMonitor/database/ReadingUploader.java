package com.FIT3170.HealthMonitor.database;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

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

    private Date currentStartTime;
    private final ArrayList currentData = new ArrayList<>();
    private static final int ONE_MIN_IN_MILLIS = 60000;
    private final String patientId;
    private final Timer uploadTimer;

    //TODO: remove this timer
    private final Timer mockSensorTime;

    private ReadingUploader() {
        db = FirebaseFirestore.getInstance();
        // get current userId
        patientId = UserProfile.getUid();
        uploadTimer = new Timer();

        //TODO:remove this
        mockSensorTime = new Timer();
    }

    public static ReadingUploader getInstance() {
        // create instance if doesn't exist
        if (instance == null) {
            instance = new ReadingUploader();
        }

        return instance;
    }

    public void start() {

        uploadTimer.schedule(new Upload(), ONE_MIN_IN_MILLIS, ONE_MIN_IN_MILLIS);

        //TODO: remove this
        // this is adding data every 5 seconds
        mockSensorTime.schedule(new MockSensorService(), 0, 5000);
    }

    public void stop() {
        // stop timer
        uploadTimer.cancel();
        //TODO remove this line
        mockSensorTime.cancel();

        // run upload one most time to post remaining data
        new Upload().run();
    }


    /**
     * Receives incoming EGC data contained in a DataPacket
     * @param dataPacket
     */
    public void addData(DataPacket dataPacket) {

        Log.i("ECG DATA", "Adding data");

        if (currentStartTime == null) {
            currentStartTime = new Date();
        }

        currentData.addAll(dataPacket.getDataArray());

    }


    class Upload extends TimerTask {
        public void run() {
            Log.i("ECG DATA", "Uploading");

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
                                Log.i("EGC DATA", "upload successful");
                                // update last uploaded
                                lastUpload = toUpload;
                                // clear current data
                                currentData.clear();
                                currentStartTime = null;
                            } else {
                                Log.i("EGC DATA", "upload unsuccessful");
                            }
                        });
            } else {
                Log.i("ECG DATA", "toUpload is null");
            }
        }
    }


    // This is used to mimic receiving packets from Sensor service
    // can be removed once integrated
    static class MockSensorService extends TimerTask {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            ReadingUploader.getInstance().addData(new DataPacket());
        }
    }
}

