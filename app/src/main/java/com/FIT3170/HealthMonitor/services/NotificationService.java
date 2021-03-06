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
import com.google.firebase.firestore.auth.User;

import java.sql.Timestamp;
import java.util.Date;


/**
 * This services handles the observing and firing of bpm heart rate alerts
 */
public class NotificationService extends LifecycleService {

    private BluetoothServiceModel model;
    private BluetoothService mService;
    private int mConnectionStatus;
    private DataPacket mDataPacket;
    private ECGAlgorithm algorithm;
    private Timestamp lastNotification;
    private final int minuteCool = 5;

    public static final int ABNORMAL_HIGH_HEART_RATE = 190 - UserProfile.getAge();
    public static final int ABNORMAL_LOW_HEART_RATE = 40;


    /**
     * First method called when the service is instantiated.
     * Binds to the bluetooth service, then begins to observe it.
     * Also creates the notification channel
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // Magic number, change after algorithm is working properly
        lastNotification = new Timestamp(System.currentTimeMillis()- (minuteCool * 60000));
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

    /**
     * Stop observing any other services
     */
    private void removeObservers() {
        if(mService != null){
            // Remove Observers
            mService.getDataPacketShortDuration().removeObserver(dataPacketObserver);
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
                    mService.getDataPacketShortDuration().observe(NotificationService.this, dataPacketObserver);
                }
            }
        });
    }

    /**
     * This is the observer object that represents this class.
     * The onChanged method is called everytime new data is sent to the phone via bluetooth
     */
    Observer<DataPacket> dataPacketObserver = new Observer<DataPacket>() {
        @Override
        public void onChanged(DataPacket dataPacket) {
            Log.d("debug","-----------------------------");
            Log.d("debug", "Data Packet Size: "+ dataPacket.getData().size()+"");
            Log.d("debug","-----------------------------");
            // change implementation
            // TODO: store algorithm class as local
            double bpm = algorithm.calculate(dataPacket);

            Log.d("notification service", bpm + "");
            checkAbnormalHeartRate(bpm);
        }
    };

    /**
     * Checks to see if the heart rate is abnormal, triggers notification if so
     * @param bpm Most recent bpm value
     */
    private void checkAbnormalHeartRate(double bpm) {
        long lastMili = lastNotification.getTime();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        // heart rate too high and notification off cool-down
        if(bpm > ABNORMAL_HIGH_HEART_RATE && now.getTime() > lastMili + minuteCool * 60000) {
            lastNotification = new Timestamp(System.currentTimeMillis());
            String title = "Abnormally High Heart Rate";
            String description = "An abnormal high heart rate of " + java.lang.Math.round(bpm) + " was detected. We recommend you get proper medical "
                    + "assistance.";
            sendNotification(lastNotification, title, description);
        }
        // or too low
        else if (bpm < ABNORMAL_LOW_HEART_RATE && now.getTime() > lastMili + minuteCool * 60000){
            String title = "Abnormally Low Heart Rate";
            String description = "An abnormal low heart rate of " + java.lang.Math.round(bpm) + " was detected. We recommend you get proper medical "
                    + "assistance.";

            lastNotification = new Timestamp(System.currentTimeMillis());
            sendNotification(lastNotification, title, description);
        }
    }

    /**
     * Creates the settings for heart rate notifications to be sent through: id, name, importance
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Test"; // TODO: Set a meaningful name and description
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
    private void sendNotification(Timestamp time, String title, String description) {

        NotificationBuilder builder = new NotificationBuilder();

        builder.createNotification(this, title, description);
        storeNotification(title, description, time);
    }

    /**
     * Store the notification by making a database call
     */
    private void storeNotification(String title, String description, Timestamp time) {
        Date timeDate = new Date(time.getTime());

        UserProfile.uploadNotification(title, description, timeDate);

    }

}