package com.FIT3170.HealthMonitor.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

import com.FIT3170.HealthMonitor.NotificationBuilder;
import com.FIT3170.HealthMonitor.bluetooth.BluetoothService;
import com.FIT3170.HealthMonitor.bluetooth.BluetoothServiceModel;
import com.FIT3170.HealthMonitor.database.DataPacket;
import com.FIT3170.HealthMonitor.database.ECGAlgorithm;
import com.FIT3170.HealthMonitor.database.PeakToPeakAlgorithm;
import com.FIT3170.HealthMonitor.database.UserProfile;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class NotificationService extends LifecycleService {

    private BluetoothServiceModel model;
    private BluetoothService mService;
    private int mConnectionStatus;
    private DataPacket mDataPacket;
    private ECGAlgorithm algorithm;
    private Timestamp lastNotification;

    public static final int ABNORMAL_HEART_RATE = 120;

    // FOR DEBUG PURPOSES ONLY
    // !!
    // !!
    // !!
    private Boolean hasNotificationBeenServed = false;
    // !!
    // !!
    // !!


    /**
     * First method called when the service is instantiated.
     * Binds to the bluetooth service, then begins to observe it.
     * Also creates the notifcation channel
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // Magic number, change after algorithm is working properly
        lastNotification = new Timestamp(System.currentTimeMillis()- (5 * 60000));
        model = new BluetoothServiceModel();
        algorithm = new ECGAlgorithm(new PeakToPeakAlgorithm());
        bindToBluetoothService();
        setObservers();
        createNotificationChannel();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        super.onBind(intent);
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    // Stops the service
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(model.getBinder() != null){
            removeObservers();
            this.unbindService(model.getServiceConnection());
        }
    }

    private void removeObservers() {
        if(mService != null){
            // Remove Observers
            mService.getDataPacket().removeObserver(dataPacketObserver);
        }
    }


    /**
     * Binds to the bluetooth service.
     * Must bind to a service before you can interact with it.
     */
    private void bindToBluetoothService() {
        Intent serviceIntent = new Intent(this, BluetoothService.class);
        this.bindService(serviceIntent, model.getServiceConnection(), Context.BIND_AUTO_CREATE);
    }

    /**
     * Adds the notification service as an observer to the bluetooth service.
     *
     */
    private void setObservers() {
        model.getBinder().observe(this, new Observer<BluetoothService.BluetoothBinder>() {
            @Override
            public void onChanged(BluetoothService.BluetoothBinder bluetoothBinder) {
                if(bluetoothBinder == null){
                    Log.d("debug", "onChanged: unbound to service.");
                    mService = null;
                }
                else {
                    Log.d("debug", "onChanged: bound to service.");
                    mService = bluetoothBinder.getService();

//                    mConnectionStatus = mService.getConnectionStatus().getValue();
//                    mService.getConnectionStatus().observe(currentLifecycle, connectionStatusObserver);

                    // mDataPacket = mService.getDataPacket().getValue();
                    mService.getDataPacket().observe(NotificationService.this, dataPacketObserver);
                }
            }
        });
    }

    /**
     * This is the observer object that represents this class.
     * The onChanged method is called everytime new data is sent to the phone via bluetooth
     *
     */
    Observer<DataPacket> dataPacketObserver = new Observer<DataPacket>() {
        @Override
        public void onChanged(DataPacket dataPacket) {
            Log.d("debug","-----------------------------");
            Log.d("debug", "Data Packet Size: "+ dataPacket.getData().size()+"");
            Log.d("debug","-----------------------------");
            // change implementation
            // store algorithm class as local
            double bpm = algorithm.calculate(dataPacket);
            Log.d("notification service", bpm + "");
            checkAbnormalHeartRate(bpm);
        }
    };

//    Observer<Integer> connectionStatusObserver = new Observer<Integer>() {
//        @Override
//        public void onChanged(Integer integer) {
//            Log.d("debug", "Connection status: "+integer.toString());
//        }
//    };

    /**
     * Checks to see if the heart rate is abnormal, triggers notification if so
     * @param bpm Most recent bpm value
     */
    private void checkAbnormalHeartRate(double bpm) {
        int minuteCool = 5;
        long lastMili = lastNotification.getTime();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        // heart rate trigger and notification off cooldown
        if(bpm > ABNORMAL_HEART_RATE && now.getTime() > lastMili + minuteCool * 60000) {

            // Please Remove This Line Of Code after we fix the abnormal heart rate algorithm
            lastNotification = new Timestamp(System.currentTimeMillis());
            sendNotification(bpm, lastNotification);
        }
    }

    /**
     * Need to investigate its purpose.
     * Allows for notifications to be sent
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Test";//getString(R.string.channel_name);
            String description = "Description"; //getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("com.FIT3170.HealthMonitor",
                    name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    /**
     * Sends the abnormal heart rate notification to the phone
     */
    private void sendNotification(double bpm, Timestamp time) {
        // ----- TESTING HARDCODING
//        time = new Timestamp(System.currentTimeMillis());
//        bpm = 160.2;
        // -------

        NotificationBuilder builder = new NotificationBuilder();
        String title = "Abnormal Heart Rate";
        String description = "An abnormal heart rate of " + String.valueOf(bpm) + " was detected. We recommend you get proper medical "
                + "assistance.";

        builder.createNotification(this, title, description);
        storeNotification(title, description, time);
    }

    /**
     * TODO: Implement functionality to store a sent notification in firebase
     * TODO: Clarify which Timestamp is best (com.google.firebase; java.sql)
     */
    private void storeNotification(String title, String description, Timestamp time) {
        Date timeDate = new Date(time.getTime());

        UserProfile.uploadNotification(title, description, timeDate);

    }

}