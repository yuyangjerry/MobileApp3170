package com.FIT3170.HealthMonitor.bluetooth;


import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import com.FIT3170.HealthMonitor.NotificationService;
import com.FIT3170.HealthMonitor.R;
import com.FIT3170.HealthMonitor.database.DataPacket;
import com.FIT3170.HealthMonitor.database.DataPoint;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.clj.fastble.utils.HexUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class BluetoothService extends Service {
    private final IBinder binder = new BluetoothBinder();
    private MutableLiveData<Boolean> mIsScanning = new MutableLiveData<>(false);
    private MutableLiveData<BleDevice> mBleDevice = new MutableLiveData<>(); //Connect to the device
    private MutableLiveData<BleDevice> mScanResult = new MutableLiveData<>();
    private MutableLiveData<DataPacket> mDataPointSink = new MutableLiveData<>();
    // Connection States
    private MutableLiveData<Integer> connectionStatus = new MutableLiveData<>(CONNECTION_DISCONNECTED);
    public static final int CONNECTION_DISCONNECTED = 1;
    public static final int CONNECTION_INITIATE = 2;
    public static final int CONNECTION_FAILURE = 3;
    public static final int CONNECTION_CONNECTED = 4;
    public static final int CONNECTION_SUBSCRIBED = 5;

    // Services + Gatt
//    private BluetoothGattService bluetoothGattService;
//    private List<BluetoothGattCharacteristic> bluetoothGattCharacteristics;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private List<DataPoint> buffer = new ArrayList<DataPoint>();

    // Handler
    Handler mHandler = new Handler();
    private static final int SINK_DURATION = 1000;

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

    public MutableLiveData<DataPacket> getDataPacket() {return mDataPointSink; }

    public MutableLiveData<Integer> getConnectionStatus() {return  connectionStatus;}

    public void setmBleDevice(BleDevice device) {
        mBleDevice.postValue(device);}

    @Override
    // Stops the service
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(transferBufferToSink);
        BleManager.getInstance().disconnectAllDevice();
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
                    connectionStatus.postValue(CONNECTION_CONNECTED);
                    Boolean wasNotifyCharactersticFound = initSubcriptionDetails(bleDevice);
                    Log.d("debug","notChara: "+wasNotifyCharactersticFound.toString());
                    if (wasNotifyCharactersticFound) {
                        subscribeToSensor(bleDevice);
                    } else {
                        stopSelf();
                        return;
                    }

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
        if (connectionStatus.getValue() == CONNECTION_SUBSCRIBED) {
            BleManager.getInstance().stopNotify(
                mBleDevice.getValue(),
                mNotifyCharacteristic.getService().getUuid().toString(),
                mNotifyCharacteristic.getUuid().toString());
            mHandler.removeCallbacks(transferBufferToSink);
        }
        BleManager.getInstance().disconnectAllDevice();
    }

    private BluetoothGattService pipeGattService(BleDevice bleDevice) {
        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
        BluetoothGattService retrievedService = null;
        for (BluetoothGattService service : gatt.getServices()) {
            if (service.getUuid().equals(Utils.getSensorServiceUUID())) {
                retrievedService = service;
            }
        }
        return retrievedService;
    }

    private List<BluetoothGattCharacteristic> pipeGattCharacteristics(BluetoothGattService bluetoothGattService) {
        return bluetoothGattService.getCharacteristics();
    }

    public BluetoothGattCharacteristic pipeNotifyCharacteristic(List<BluetoothGattCharacteristic> bluetoothGattCharacteristics) {
        BluetoothGattCharacteristic notifyCharacteristic = null;
        for (BluetoothGattCharacteristic chara : bluetoothGattCharacteristics)
            if ((chara.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                notifyCharacteristic = chara;
            }

        return notifyCharacteristic;
    }

    private Boolean initSubcriptionDetails(BleDevice bleDevice) {
        if (BleManager.getInstance().isConnected(bleDevice)) {
            // Set Device
            mBleDevice.postValue(bleDevice);
        } else {
            Log.d("debug","Couldn't set device as it is not connected");
            return false;
        }
        BluetoothGattService service = pipeGattService(bleDevice);
        if (service == null) {
            Log.d("debug","Couldn't extract a valid GattService");
            return false;
        }
        List<BluetoothGattCharacteristic> serviceCharacteristics = pipeGattCharacteristics(service);
        if (serviceCharacteristics.size() == 0) {
            Log.d("debug", "Couldn't extract a valid Characteristic");
            return false;
        }
        BluetoothGattCharacteristic notifyCharacteristic = pipeNotifyCharacteristic(serviceCharacteristics);
        if (notifyCharacteristic != null) {
            // Set Notify Characteristic
            mNotifyCharacteristic = notifyCharacteristic;
        } else {
            Log.d("debug","Couldn't extract a valid Notify Characteristic");
            return false;
        }
        return true;
    }


    private void subscribeToSensor(BleDevice bleDevice) {
        if (!BleManager.getInstance().isConnected(bleDevice)) {
            return;
        }
        // Clear The Buffer
        buffer.clear();
        BleManager.getInstance().notify(
                bleDevice,
                mNotifyCharacteristic.getService().getUuid().toString(),
                mNotifyCharacteristic.getUuid().toString(),
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        Log.d("debug","Successfully Subscribed to Sensor (Raw)");
//                        handler = new Handler();
//                        handler.postDelayed(graphing, handlerInterval);
                        connectionStatus.postValue(CONNECTION_SUBSCRIBED);
                        mHandler.postDelayed(transferBufferToSink,SINK_DURATION);
                    }

                    @Override
                    public void onNotifyFailure(final BleException exception) {
                        Log.d("debug","Failed to Subscribe to Sensor (Raw)");
                        connectionStatus.postValue(CONNECTION_FAILURE);
                        mHandler.removeCallbacks(transferBufferToSink);
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
//                        Integer heartBeat = data[0] | data[1] << 8;
                        Integer heartBeatInt = Integer.parseInt(decodeResult(data));
//                        Log.d("debug",heartBeat.toString());
                        buffer.add(new DataPoint(heartBeatInt,System.currentTimeMillis()));
                    }
                });
    }

    Runnable transferBufferToSink = new Runnable() {
        @Override
        public void run() {
            List<DataPoint> tempRef = buffer;
//            Log.d("debug", "Sink: "+tempRef.size()+" packets");
            mDataPointSink.postValue(new DataPacket(tempRef));
            // Don't use buffer.clear(). This will destroy the data before it is sent to fragments
            buffer = new ArrayList<DataPoint>();
            mHandler.postDelayed(this,SINK_DURATION);
        }
    };

    // Ripepd from our clients code :). Thank you for your contribuition sir
    private String decodeResult(byte[] data) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0 ; i<data.length; i +=3){
            if(data[i] == 0x0B){
                int Value;
                Value = ((data[i+1]<<8) & 0x0000ff00)|(data[i+2]&0x000000ff);
                stringBuilder.append(Value);
            }
        }
        return stringBuilder.toString();
    }



}
