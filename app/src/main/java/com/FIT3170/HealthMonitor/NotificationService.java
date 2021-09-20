package com.FIT3170.HealthMonitor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleService;

public class NotificationService extends LifecycleService {
    public NotificationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        super.onBind(intent);
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }
}