package com.FIT3170.HealthMonitor.database;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
* This class uses the Peak to peak calculation algorithm to calculate a person's BPM
*/

public class PeakToPeakAlgorithm implements Algorithm {
    // Unchanging variables
    private static final int SECOND = 60;
    private static final int FREQ = 50;
    private static final int BOUND = 1250;

    // Variable used for peak calculations
    // Last peak value in mV
    private int prevPeak;
    // The distance of last peak to end of data packet's data set
    private int prevPeakDistance;

    // initialise the previous peak and previous peak distance to 0
    public PeakToPeakAlgorithm() {
        prevPeak = 0;
        prevPeakDistance = 0;
    }

    // Calculate BPM functiion.
    @Override
    public double getBPM(DataPacket dataPacket) {
        // Set the lastPeakDistance value to be prevPeakDistance
        int lastPeakDistance = prevPeakDistance;
        int peakCount = 0;
        double averageBPM = 0;
        ArrayList<Integer> peakDistanceArray = new ArrayList<>();
        List<DataPoint> dataArray = dataPacket.getDataArray();


        // Loop through data to find the peaks of
        for (int i = 3; i <dataArray.size(); i++){
            int currentData = dataArray.get(i).getValue();
            // Find the difference between current value and value from 2 data points before
            int difference = currentData - dataArray.get(i - 3).getValue();
            // If the difference between 3 data points is greater than the bound
            // And the last peak is more than 2 data points away (Might need double check on this statement)
            // lastPeakDistance < i - 2
            if (difference > BOUND && lastPeakDistance < i - 2){
                Log.d("debug", "Peak values = " + currentData + " and " + dataArray.get(i - 3));
                Log.d("debug", "At point = " + i + " and " + (i - 3));
                peakDistanceArray.add(i - lastPeakDistance);
                peakCount ++;
                lastPeakDistance = i;
            }
        }
        Log.d("debug", "Peak count = " + peakCount);
        for (int i = 0; i < peakDistanceArray.size(); i++){
            double freqPeak = SECOND * ((double)FREQ/(double)peakDistanceArray.get(i));
            averageBPM = averageBPM + freqPeak;
            Log.d("debug", "Last peak: " + peakDistanceArray.get(i));
            Log.d("debug","Heart beat between peaks: " + freqPeak);
        }
        // Set prevPeakDistance as datapoint distance between the last peak in the data packet to the end of data packet
        prevPeakDistance = dataArray.size() - lastPeakDistance;
        Log.d("debug", "Distance of last peak to end of data packet: " + prevPeakDistance);
        Log.d("debug","Sum of Heart beat over 5 second: " + averageBPM);

        return averageBPM/peakCount;
    }
}
