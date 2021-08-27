package com.FIT3170.HealthMonitor.bluetooth;


import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.FIT3170.HealthMonitor.MainActivity;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;

import java.util.List;
import java.util.UUID;

public class BluetoothService extends Service {
    private final IBinder binder = new BluetoothBinder();
    private MutableLiveData<Boolean> mIsScanning = new MutableLiveData<>(false);
    private MutableLiveData<BleDevice> mBleDevice = new MutableLiveData<>();
    private MutableLiveData<BleDevice> mScanResult = new MutableLiveData<>();

    // Connection States
    private MutableLiveData<Integer> connectionStatus = new MutableLiveData<>();
    public static final int CONNECTION_INITIATE = 1;
    public static final int CONNECTION_SUCCESS = 2;
    public static final int CONNECTION_FAILURE = 3;
    public static final int CONNECTION_DISCONNECTED = 4;

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
        return mIsScanning;
    }


    public MutableLiveData<BleDevice> getmBleDevice() {return mBleDevice; }

    public MutableLiveData<BleDevice> getmScanResult() {return mScanResult; }

    public void setmBleDevice(BleDevice device) {
        mBleDevice.postValue(device);}

    @Override
    // Stops the service
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        BleManager.getInstance().disconnectAllDevice();
        stopSelf();
    }

    public void startScan() {
        if (!mIsScanning.getValue()) {
            BleManager.getInstance().scan(new BleScanCallback() {
                @Override
                public void onScanStarted(boolean success) {
                    mIsScanning.postValue(true);
//                img_loading.startAnimation(operatingAnim);
//                img_loading.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLeScan(BleDevice bleDevice) {
                    super.onLeScan(bleDevice);
                }

                @Override
                public void onScanning(BleDevice bleDevice) {
                    mScanResult.postValue(bleDevice);
                }

                @Override
                public void onScanFinished(List<BleDevice> scanResultList) {
                    mIsScanning.postValue(false);
                }
            });
        }
    }

    public void stopScan() {
        if (mIsScanning.getValue()) {
            BleManager.getInstance().cancelScan();
            mIsScanning.postValue(false);
        }
    }

    public void connectDevice(BleDevice device) {
        if (device != null) {
            BleManager.getInstance().connect(device, new BleGattCallback() {
                @Override
                public void onStartConnect() {
                    connectionStatus.postValue(CONNECTION_INITIATE);
                }

                @Override
                public void onConnectFail(BleDevice bleDevice, BleException exception) {
                    connectionStatus.postValue(CONNECTION_FAILURE);
                }

                @Override
                public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                    connectionStatus.postValue(CONNECTION_SUCCESS);
                    mBleDevice.postValue(bleDevice);
                }

                @Override
                public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                    connectionStatus.postValue(CONNECTION_DISCONNECTED);
                    mBleDevice.postValue(null);
                }
            });
        }

    }

    public void disconnectAllDevices() {
        BleManager.getInstance().disconnectAllDevice();
    }

}
