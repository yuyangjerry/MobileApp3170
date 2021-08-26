package com.FIT3170.HealthMonitor.database;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.LinkedList;

/*
This class keeps track of ECG readings as they are received and uploads to the database every minute

This is a singleton because there should only be one channel uploading readings to the database
 */

public class ReadingUploader {

    private static ReadingUploader instance = null;
    private FirebaseFirestore db;

    private static final String START_TIME_KEY = "startTime";
    private static final String DATA_KEY = "data";
    private HashMap<String,Object> toUpload= new HashMap<>();
    private HashMap<String,Object> lastUpload= new HashMap<>();

    private Long currentStartTime;
    private Long nextStartTime;
    private LinkedList<Integer> currentData = new LinkedList<>();
    private static final int ONE_MIN_IN_MILLIS = 60000;
    private String patientId;

    private ReadingUploader(){
        db = FirebaseFirestore.getInstance();
        // get current userId
        patientId = UserProfile.getUid();
    }

    public static ReadingUploader getInstance(){
        // create instance if doesn't exist
        if (instance == null){
            instance = new ReadingUploader();
        }

        return instance;
    }

    /**
     * Receives incoming EGC data as an integer and attaches a timestamp
     * Data will be collated in currentData and then uploaded to the Database every minute
     */
    public void addData(Integer ECGReading){

        Log.i("ECG DATA", "Adding some data" );

        // get current time
        long currentTimeMillis = System.currentTimeMillis();


        if (currentStartTime == null){
            currentStartTime = currentTimeMillis;
            nextStartTime = currentStartTime + ONE_MIN_IN_MILLIS;

        }else if(currentTimeMillis >= nextStartTime){

            // create Map to store data to be uploaded
            toUpload.put(START_TIME_KEY, toUNIX(currentStartTime));
            toUpload.put(DATA_KEY, currentData);

            upload();

            // update current and next StartTime
            currentStartTime = nextStartTime;
            nextStartTime += ONE_MIN_IN_MILLIS;
        }

        Log.i("ECG DATA", "currentTime: " + currentTimeMillis + " next: " + nextStartTime + " Diff: " + (nextStartTime-currentTimeMillis) );
        currentData.add(ECGReading);
    }

    public void stopUploading(){
        //TODO: upload last part of data
    }

    /*
    converts time in milliseconds to Unix time
     */
    private Long toUNIX(Long timeInMillis){
        return timeInMillis/1000;
    }

    /*
        pushed readings to the database
        TODO: callback
     */
    private void upload()  {

        Log.i("ECG DATA", "Uploading" );

        //upload to firebase
        if(toUpload != null){

            db
                    .collection("patients/" + patientId + "/readings")
                    .add(toUpload)
                    .addOnCompleteListener(l -> {
                        if(l.isSuccessful()){
                            // update last uploaded
                            lastUpload = toUpload;
                            // clear current data
                            currentData.clear();
                        }else{
                            Log.i("EGC DATA", "upload unsuccessful");
                        }
                    });
        }else{
            Log.i("ECG DATA", "toUpload is null");
        }
    }

}
