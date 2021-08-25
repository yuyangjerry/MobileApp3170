package com.FIT3170.HealthMonitor.database;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.LinkedList;

/*
This class keeps track of ECG readings as they are received and uploads to the database every minute
 */

public class ReadingUploader {

    private FirebaseFirestore db;

    public HashMap<String,Object> toUpload= new HashMap<>();
    public HashMap<String,Object> lastUpload= new HashMap<>();

    private Long currentStartTime;
    private Long nextStartTime;
    private LinkedList<Integer> currentData = new LinkedList<>();
    private static final int ONE_MIN_IN_MILLIS = 60000;

    /**
     * Receives incoming EGC data as an integer and attaches a timestamp
     * Data will be collated in currentData and then uploaded to the Database every minute
     */
    public void addData(Integer ECGReading){

        // get current time
        long currentTimeMillis = System.currentTimeMillis();

        if (currentStartTime == null){
            currentStartTime = currentTimeMillis;
            nextStartTime = currentStartTime + ONE_MIN_IN_MILLIS;

        }else if(currentTimeMillis >= nextStartTime){

            // create Map to store data to be uploaded
            toUpload.put("startTime", toUNIX(currentStartTime));
            toUpload.put("data", currentData);

            upload();

            // update current and next StartTime
            currentStartTime = nextStartTime;
            nextStartTime += ONE_MIN_IN_MILLIS;
        }

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

        // get current userId
        String patientId = UserProfile.getUid();

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
                            Log.i("uploadProblem", "upload unsuccessful");
                        }
                    });
        }else{
            Log.i("uploadProblem", "toUpload is null");
        }
    }

}
