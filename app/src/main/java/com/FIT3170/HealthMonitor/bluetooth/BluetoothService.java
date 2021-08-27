package com.FIT3170.HealthMonitor.bluetooth;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.scan.BleScanRuleConfig;

import java.util.UUID;

public class BluetoothService extends Service {
    private final IBinder binder = new BluetoothBinder();
    private MutableLiveData<Boolean> isScanning = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isPermitted = new MutableLiveData<>(false);
    private MutableLiveData<BleDevice> bleDevice = new MutableLiveData<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        // Initiate BleManager
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);

        setScanRule();

        // Verify whether Ble is supported
        if (!BleManager.getInstance().isSupportBle()) {
            Log.d("debug","BLE not supported");
            stopSelf();
        };

        // Check if bluetooth is on. Request to turn it on otherwise
        if (!BleManager.getInstance().isBlueEnable()) {
            BleManager.getInstance().enableBluetooth();
        }
    }

    private void setScanRule() {
        UUID[] serviceUuids = new UUID[]{Utils.getSensorServiceUUID()};
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setServiceUuids(serviceUuids)
                .setAutoConnect(false)
                .setScanTimeOut(10000)
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    public class BluetoothBinder extends Binder {

        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    public MutableLiveData<Boolean> isScanning() {
        return isScanning;
    }

    public MutableLiveData<Boolean> isPermitted() {
        return isPermitted;
    }

    public MutableLiveData<BleDevice> getBleDevice() {return bleDevice; }

    public void setBleDevice(BleDevice device) {bleDevice.postValue(device);}

    @Override
    // Stops the service
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }
}
