package com.FIT3170.HealthMonitor.services;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

import com.FIT3170.HealthMonitor.bluetooth.BluetoothService;
import com.FIT3170.HealthMonitor.bluetooth.BluetoothServiceModel;
import com.FIT3170.HealthMonitor.database.DataPacket;

public class UploadingService extends LifecycleService {

    private BluetoothServiceModel model;
    private BluetoothService mService;

    /**
     * First method called when the service is instantiated.
     * Binds to the bluetooth service, then begins to observe it.
     * Also creates the notifcation channel
     */
    @Override
    public void onCreate() {
        super.onCreate();
        model = new BluetoothServiceModel();
        bindToBluetoothService();
        setObservers();
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

                    mService.getDataPacketLongDuration().observe(UploadingService.this, dataPacketObserver);
                }
            }
        });
    }

    // Do some uploading here
    private void handlePacket(DataPacket dataPacket) {
        Log.d("d","uploading packet");
    }

    /**
     * This is the observer object that represents this class.
     * The onChanged method is called everytime new data is sent to the phone via bluetooth
     *
     */
    Observer<DataPacket> dataPacketObserver = new Observer<DataPacket>() {
        @Override
        public void onChanged(DataPacket dataPacket) {
            handlePacket(dataPacket);
        }
    };

//    Observer<Integer> connectionStatusObserver = new Observer<Integer>() {
//        @Override
//        public void onChanged(Integer integer) {
//            Log.d("debug", "Connection status: "+integer.toString());
//        }
//    };



}