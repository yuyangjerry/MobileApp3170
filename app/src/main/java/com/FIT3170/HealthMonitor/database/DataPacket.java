package com.FIT3170.HealthMonitor.database;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to communicate between the sensor service and the uploader.
 * This is the structure that is expected to add data to the uploader.  It should contain 5 seconds of data.
 */
public class DataPacket {

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

}
