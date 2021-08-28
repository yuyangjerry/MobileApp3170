package com.FIT3170.HealthMonitor.database;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to communicate between the sensor service and the uploader.
 * This is the structure that is expected to add data to the uploader.  It should contain 5 seconds of data.
 */
public class DataPacket implements ECGAlgorithm{

    private List<DataPoint> dataArray;

    /**
     * This is the constructor that should be used when created by Sensor Service
     *
     * This could be changed to receiving ArrayList of arrays and this constructor could convert to DataPoints
     * @param dataPoints
     */
    public DataPacket(List<DataPoint> dataPoints){
        dataArray = dataPoints;
    }

    /**
     * This is the constructor used during testing - it created some sample data points
     * 250 points to replicate 5 seconds of data
     */
    public DataPacket(){
        // create sample data with values starting at 1 and increasing
        dataArray = new ArrayList();

        int dataValue = 1;
        for (int i = 0; i < 250; i++){

            dataArray.add(new DataPoint(dataValue, System.currentTimeMillis()));

            dataValue += 1;
        }
    }

    public List<DataPoint> getData(){
        return dataArray;
    }

    // This is bad implementation maybe fix later
    public int getPeakCount () {
        int lastPeak = 0;
        final int bound = 500;
        int peakCount = 0;
        ArrayList<int[]> peakDistanceArray = new ArrayList<int[]>();


        // Loop through data to find the peaks of
        for (int i = 2; i <dataArray.size(); i++){
            int currentData = dataArray.get(i).getValue();
            // Find the difference between current value and value from 2 data points before
            int difference = currentData - dataArray.get(i - 2).getValue();
            // If the difference between 3 data points is greater than the bound
            if (difference > bound && lastPeak < i - 1){
                Log.d("debug", "Peak values = " + currentData + " and " + dataArray.get(i - 2));
                Log.d("debug", "At point = " + i + " and " + (i - 2));
                int[] peak = {lastPeak, i};
                peakDistanceArray.add(peak);
                peakCount ++;
                lastPeak = i;
            }
        }
        Log.d("debug", "Peak count = " + peakCount);
        for (int i = 0; i < peakDistanceArray.size(); i++){
            Log.d("debug", "Last peak: " + peakDistanceArray.get(i)[0] + " " + "Current Peak:: " + peakDistanceArray.get(i)[1]);
        }
        return peakCount;
    }

}
