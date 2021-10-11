package com.FIT3170.HealthMonitor;

import com.google.firebase.Timestamp;

/**
 * Class to represent the data of a single notification sent to the user
 */
public class Notification {
    private Timestamp timestamp; // the time the notification was originally sent
    private String title;
    private String description;

    public Notification(String title, String description, Timestamp timestamp){
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public String formatTime(){
        return timestamp.toDate().toString();
    }

}
