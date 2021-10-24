package com.FIT3170.HealthMonitor.bluetooth;


import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.FIT3170.HealthMonitor.database.DataPacket;
import com.FIT3170.HealthMonitor.database.DataPoint;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Background Service that handles connecting, subscribing to heart rate sensor values and pushing these
 * values to any Bound Fragments or even other Services. Acts a layer of abstraction for any interaction between
 * the front-end and the heart rate sensor
 * @author Lasith Koswatta Gamage
 */
public class BluetoothService extends Service {
    // Binder
    private final IBinder binder = new BluetoothBinder();

    // Observers
    private MutableLiveData<Boolean> mIsScanning = new MutableLiveData<>(false);
    private MutableLiveData<BleDevice> mBleDevice = new MutableLiveData<>();
    private MutableLiveData<BleDevice> mScanResult = new MutableLiveData<>();
    private MutableLiveData<DataPacket> mDataPointShortSink = new MutableLiveData<>();
    private MutableLiveData<DataPacket> mDataPointLongSink = new MutableLiveData<>();
    private MutableLiveData<Integer> connectionStatus = new MutableLiveData<>(CONNECTION_DISCONNECTED);

    // Connection States
    public static final int CONNECTION_DISCONNECTED = 1;
    public static final int CONNECTION_INITIATE = 2;
    public static final int CONNECTION_FAILURE = 3;
    public static final int CONNECTION_CONNECTED = 4;
    public static final int CONNECTION_SUBSCRIBED = 5;

    // Services + Gatt
    private BluetoothGattCharacteristic mNotifyCharacteristic;


    // Handler + Short & Long Polling Queue
    Handler mHandler = new Handler();
    private List<DataPoint> shortBuffer = new ArrayList<DataPoint>();
    private List<DataPoint> longBuffer = new ArrayList<DataPoint>();
    private static final int SINK_SHORT_DURATION = 5000; // 5 sec
    private static final int SINK_LONG_DURATION = 10000; // 10 second

    /**
     * Return binder to allow LifeCycleServices, Fragments and Activities to subscribe to BluetoothService
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * Steps
     * 1) Initialize BleManager
     * 2) Initialize BLE Scan Rules
     * 3) Check if device supports BLE
     */
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

    /**
     * Stop BluetoothService when application is manually removed from process stack
     * @param rootIntent
     */
    @Override
    // Stops the service
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    /**
     * Deprovision the Long and Short Polling Queues and disconnect from all
     * connected devices
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(transferDataPointShortSink);
        mHandler.removeCallbacks(transferDataPointLongSink);
        BleManager.getInstance().disconnectAllDevice();
    }

    /**
     * Initialises the search parameters for Bluetooth Scanning. Search by UUID.
     * Predefined BLE Sensor UUID is received from BluetoothUtils
     */
    private void setScanRule() {
        UUID[] serviceUuids = new UUID[]{BluetoothUtils.getSensorServiceUUID()};
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setServiceUuids(serviceUuids)
                .setAutoConnect(false)
                .setScanTimeOut(10000)
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    /**
     * Binder for LifeCycleServices, Fragments and Activities to bind to
     */
    public class BluetoothBinder extends Binder {

        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    /**
     * Return observer for isScanning. Notification pushed when scanning
     * is activated, manually deactivated or experiences a timeout
     * @return isScanning observer
     */
    public MutableLiveData<Boolean> isScanning() {
        return mIsScanning;
    }

    /**
     * Return observer for connected BLE device. Notification pushed when
     * a new BLE device is connected/disconnected
     * @return bleDevice observer
     */
    public MutableLiveData<BleDevice> getmBleDevice() {return mBleDevice; }

    /**
     * Return observer for scan results. Notification pushed when a new scan result
     * is received
     * @return scanResult observer
     */
    public MutableLiveData<BleDevice> getmScanResult() {return mScanResult; }

    /**
     * Return observer for DataPacket that is from the short polling queue. Notification pushed
     * when the short polling queue is refreshed
     * @return dataPacket (short polling queue) observer
     */
    public MutableLiveData<DataPacket> getDataPacketShortDuration() {return mDataPointShortSink; }

    /**
     * Return observer for DataPacket that is from the long polling queue. Notification pushed
     * when the short polling queue is refreshed
     * @return dataPacket (long polling queue) observer
     */
    public MutableLiveData<DataPacket> getDataPacketLongDuration() {return mDataPointLongSink; }

    /**
     * Return observer for connection status of BLE Device. Notification pushed when the connection status
     * of the sensor changes (disconnection, connected, subscribed to heart rate channel)
     * @return connectionStatus observer
     */
    public MutableLiveData<Integer> getConnectionStatus() {return  connectionStatus;}

    /**
     * Push new BLE device to mBleDevice observer. Called when user successfully connects or
     * disconnects from a BLE device
     * @param device New BLE device
     */
    public void setmBleDevice(BleDevice device) {
        mBleDevice.postValue(device);
    }

    /**
     * Callback functions for when....
     * onScanStarted:
     *      Called once when a new scan is started
     * onLeScan:
     *      Called everytime a new BLE device is detected
     * onScanning:
     *      Called once when a scan timesout
     */
    public void startScan() {
        if (!mIsScanning.getValue()) {
            BleManager.getInstance().scan(new BleScanCallback() {
                @Override
                public void onScanStarted(boolean success) {
                    mIsScanning.postValue(true);
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

    /**
     *  If still scanning, then cancel the scan and push a false value to mIsScanning observer
     */
    public void stopScan() {
        if (mIsScanning.getValue()) {
            BleManager.getInstance().cancelScan();
            mIsScanning.postValue(false);
        }
    }

    /**
     * Callback functions for when device connects to a BLE device
     * onStartConnect:
     *      Called once when initiating the connection to BLE device
     * onConnectFail:
     *      Called once when failed to connect to a BLE device
     * onConnectSuccess:
     *      Called once when successfully connected to a BLE device
     * onDisConnected:
     *      Called once when disconnecting from an already connected BLE device
     * @param device BLE device to connect to
     */
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

    /**
     * Disconnect from all connected devices. If already subscribed to the heart rate sensor, then stop subscribing to the
     * Heart Rate Notification channel
     */
    public void disconnectAllDevices() {
        if (connectionStatus.getValue() == CONNECTION_SUBSCRIBED) {
            BleManager.getInstance().stopNotify(
                mBleDevice.getValue(),
                mNotifyCharacteristic.getService().getUuid().toString(),
                mNotifyCharacteristic.getUuid().toString());
            mHandler.removeCallbacks(transferDataPointShortSink);
        }
        BleManager.getInstance().disconnectAllDevice();
    }

    // BLE Notification Service Extraction Pipeline
    // The UUID of the Notification Characteristic that stores that heart rate values is hidden behind many layers of abstractions
    // The initSubscriptionDetails() function presents the pipeline for extracting this afforementioned UUID.
    private BluetoothGattService pipeGattService(BleDevice bleDevice) {
        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
        BluetoothGattService retrievedService = null;
        for (BluetoothGattService service : gatt.getServices()) {
            if (service.getUuid().equals(BluetoothUtils.getSensorServiceUUID())) {
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


    /**
     * Callback functions related to subscribing to the heart rate sensor value. Before subscribing to the channel, clear the
     * short polling and long polling buffer.
     * onNotifySuccess:
     *      Called once when initially subscribed to sensor
     * onNotifyFailure:
     *      Called once when failing to subscribe to sensor
     * onCharacteristicChanged:
     *      Called when the value presented by the heart rate sensor channel is changed. Roughly 30 times a second (30hz)
     * @param bleDevice BLE device to subscribe to
     */
    private void subscribeToSensor(BleDevice bleDevice) {
        if (!BleManager.getInstance().isConnected(bleDevice)) {
            return;
        }
        // Clear The Buffer
        shortBuffer.clear();
        longBuffer.clear();
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
                        mHandler.postDelayed(transferDataPointShortSink, SINK_SHORT_DURATION);
                        mHandler.postDelayed(transferDataPointLongSink, SINK_LONG_DURATION);
                    }

                    @Override
                    public void onNotifyFailure(final BleException exception) {
                        Log.d("debug","Failed to Subscribe to Sensor (Raw)");
                        connectionStatus.postValue(CONNECTION_FAILURE);
                        mHandler.removeCallbacks(transferDataPointShortSink);
                        mHandler.removeCallbacks(transferDataPointLongSink);
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
//                        Integer heartBeat = data[0] | data[1] << 8;
                        Integer heartBeatInt = Integer.parseInt(decodeResult(data));
//                        Log.d("debug",heartBeat.toString());
                        shortBuffer.add(new DataPoint(heartBeatInt,System.currentTimeMillis()));
                        longBuffer.add(new DataPoint(heartBeatInt,System.currentTimeMillis()));
                    }
                });
    }

    /**
     * The runnable executed every SINK_SHORT_DURATION milliseconds. It pushes the contents of shortBuffer
     * to mDataPointShortSink observer, and then clears the buffer to allow incoming DataPoints to accumulate
     */
    Runnable transferDataPointShortSink = new Runnable() {
        @Override
        public void run() {
            List<DataPoint> tempRef = shortBuffer;
//            Log.d("debug", "Sink: "+tempRef.size()+" packets");
            mDataPointShortSink.postValue(new DataPacket(tempRef));
            // Don't use buffer.clear(). This will destroy the data before it is sent to fragments
            shortBuffer = new ArrayList<DataPoint>();
            mHandler.postDelayed(this, SINK_SHORT_DURATION);
        }
    };

    /**
     * The runnable executed every SINK_LONG_DURATION milliseconds. It pushes the contents of longBuffer
     * to mDataPointLongSink observer, and then clears the buffer to allow incoming DataPoints to accumulate
     */
    Runnable transferDataPointLongSink = new Runnable() {
        @Override
        public void run() {
            List<DataPoint> tempRef = longBuffer;
//            Log.d("debug", "Sink: "+tempRef.size()+" packets");
            mDataPointLongSink.postValue(new DataPacket(tempRef));
            // Don't use buffer.clear(). This will destroy the data before it is sent to fragments
            longBuffer = new ArrayList<DataPoint>();
            mHandler.postDelayed(this, SINK_LONG_DURATION);
        }
    };

    /**
     * Decodes the heart-rate sensor value provided by the heart rate sensor in the form of a byte array
     * @param data encoded heart rate value (byte[])
     * @return decoded heart rate value
     */
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
