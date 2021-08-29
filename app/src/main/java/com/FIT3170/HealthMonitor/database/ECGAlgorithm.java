package com.FIT3170.HealthMonitor.database;

import java.util.ArrayList;
import android.util.Log;


interface ECGAlgorithm {
    // interface implementation, not working
    public static float getPeakCount (ArrayList<DataPoint> rawData) {
        // Variable declaration
        final int bound = 500;
        final int freq = 50;
        final int second = 60;
        int lastPeak = 0;
        int peakCount = 0;
        float averageBPM = 0;
        ArrayList<Integer> peakDistanceArray = new ArrayList<Integer>();


        // Loop through data to find the peaks of
        for (int i = 2; i <rawData.size(); i++){
            int currentData = rawData.get(i).getValue();
            // Find the difference between current value and value from 2 data points before
            int difference = currentData - rawData.get(i - 2).getValue();
            // If the difference between 3 data points is greater than the bound
            if (difference > bound && lastPeak < i - 1){
                Log.d("debug", "Peak values = " + currentData + " and " + rawData.get(i - 2));
                Log.d("debug", "At point = " + i + " and " + (i - 2));
                peakDistanceArray.add(i - lastPeak);
                peakCount ++;
                lastPeak = i;
            }
        }
        Log.d("debug", "Peak count = " + peakCount);
        for (int i = 0; i < peakDistanceArray.size(); i++){
            float freqPeak = second * ((float)freq/(float)peakDistanceArray.get(i));
            averageBPM = averageBPM + freqPeak;
            Log.d("debug", "Last peak: " + peakDistanceArray.get(i));
            Log.d("debug","Heart beat between peaks: " + freqPeak);
        }
        Log.d("debug","Sum of Heart beat over 5 second: " + averageBPM);
        return averageBPM/peakCount;
    }
}
