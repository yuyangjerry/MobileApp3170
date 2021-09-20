package com.FIT3170.HealthMonitor.bluetooth;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BluetoothServiceViewModel  {
    MutableLiveData<BluetoothService.BluetoothBinder> binder = new MutableLiveData<>();

    // Keeping this in here because it doesn't require a context
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            Log.d("debug", "ServiceConnection: connected to service.");
            // We've bound to MyService, cast the IBinder and get MyBinder instance
            BluetoothService.BluetoothBinder bluetoothBinder = (BluetoothService.BluetoothBinder) iBinder;
            binder.postValue(bluetoothBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d("debug", "ServiceConnection: disconnected from service.");
            binder.postValue(null);
        }
    };

    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    public MutableLiveData<BluetoothService.BluetoothBinder> getBinder() {
        return binder;
    }
}
