package com.FIT3170.HealthMonitor.database;
import android.os.Build;
import androidx.annotation.RequiresApi;
import java.time.Instant;


/**
 * This is an individual data point
 *
 * This class has been implemented to work around Firebase not being able to store nested arrays
 */
public class DataPoint {

    private int value;
    private Instant time;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DataPoint(int value) {
        this.value = value;
        this.time = Instant.now();
    }

    public int getValue() {
        return value;
    }

    public Instant getTime() {
        return time;
    }

    public String toString() {
        return "{value:" + value + ",time:" + time.toString() + "}";
    }
}
