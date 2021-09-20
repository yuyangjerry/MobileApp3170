package com.FIT3170.HealthMonitor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

import com.FIT3170.HealthMonitor.bluetooth.BluetoothService;
import com.FIT3170.HealthMonitor.bluetooth.BluetoothServiceViewModel;
import com.FIT3170.HealthMonitor.database.DataPacket;
import com.FIT3170.HealthMonitor.database.ECGAlgorithm;
import com.FIT3170.HealthMonitor.database.ReadingUploader;

public class NotificationService extends LifecycleService {

    private BluetoothServiceViewModel model;
    private BluetoothService mService;
    private int mConnectionStatus;
    private DataPacket mDataPacket;
    private ECGAlgorithm algorithm;

    public static final int ABNORMAL_HEART_RATE = 120;

    public NotificationService() {
    }

    /**
     * First method called when the service is instantiated.
     * Binds to the bluetooth service, then begins to observe it.
     * Also creates the notifcation channel
     */
    @Override
    public void onCreate() {
        super.onCreate();
        model = new BluetoothServiceViewModel();
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

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
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
        LifecycleOwner currentLifecycle = this;
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
                    mService.getDataPacket().observe(currentLifecycle, dataPacketObserver);
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
            checkAbnormalHeartRate(bpm);
//
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
        if(bpm > ABNORMAL_HEART_RATE) {
            sendNotification();
            storeNotification();
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
     * Sends the notification to the phone
     */
    private void sendNotification() {
        NotificationBuilder builder = new NotificationBuilder();

        builder.createNotification(this, "Abnormal Heart Rate",
                "An abnormal heart rate was detected. We recommend you get proper medical "
                        + "assistance.");

    }

    /**
     * TODO: Implement functionality to store a sent notification in firebase
     */
    private void storeNotification() {

    }

}