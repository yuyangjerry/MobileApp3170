package com.FIT3170.HealthMonitor.database;

/**
 * This is an individual data point
 *
 * This class has been implemented to work around Firebase not being able to store nested arrays
 */
public class DataPoint {

    private int value;
    private long timeInMillis;

    public DataPoint(int value, long timeInMillis){
        this.value = value;
        this.timeInMillis = timeInMillis;
    }

    public int getValue() {
        return value;
    }

    public long getTime() {
        return timeInMillis;
    }
}
