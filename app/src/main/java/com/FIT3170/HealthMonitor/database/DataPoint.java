package com.FIT3170.HealthMonitor.database;

/**
 * This is an individual data point
 *
 * This class has been implemented to work around Firebase not being able to store nested arrays
 */
public class DataPoint {

    public int value;
    public long timeInMillis;

    DataPoint(int value, long timeInMillis){
        this.value = value;
        this.timeInMillis = timeInMillis;
    }
}
