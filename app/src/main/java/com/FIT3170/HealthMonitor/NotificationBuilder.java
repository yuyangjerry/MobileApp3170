package com.FIT3170.HealthMonitor;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.FIT3170.HealthMonitor.MainActivity.CHANNEL_ID;

public class NotificationBuilder {

    private int id = 0;


    public void createNotification(Context context, String title, String content){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "com.FIT3170.HealthMonitor")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(id, builder.build());
        id++;

    }




}
