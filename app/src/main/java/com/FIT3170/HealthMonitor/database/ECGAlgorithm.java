package com.FIT3170.HealthMonitor.database;

import java.util.ArrayList;
import android.util.Log;


interface ECGAlgorithm {
    // interface implementation, not working
    public static int getPeakCount (ArrayList<DataPoint> rawData) {
        int lastPeak = 0;
        final int bound = 500;
        int peakCount = 0;
        ArrayList<int[]> peakDistanceArray = new ArrayList<int[]>();


        // Loop through data to find the peaks of
        for (int i = 2; i <rawData.size(); i++){
            int currentData = rawData.get(i).getValue();
            // Find the difference between current value and value from 2 data points before
            int difference = currentData - rawData.get(i - 2).getValue();
            // If the difference between 3 data points is greater than the bound
            if (difference > bound && lastPeak < i - 1){
                Log.d("debug", "Peak values = " + currentData + " and " + rawData.get(i - 2));
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
