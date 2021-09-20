package com.FIT3170.HealthMonitor;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.FIT3170.HealthMonitor.adapters.ListAdapter_BleDevices;
import com.FIT3170.HealthMonitor.bluetooth.BluetoothService;
import com.FIT3170.HealthMonitor.bluetooth.BluetoothServiceViewModel;
import com.FIT3170.HealthMonitor.database.DataPacket;
import com.FIT3170.HealthMonitor.database.UserProfile;
import com.clj.fastble.data.BleDevice;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private TextView email;
    private TextView uuid;

    // Bluetooth
    private BluetoothService mService;
    private BleDevice mDevice;
    private Boolean mIsScanning;
    private BluetoothServiceViewModel model;
    private DataPacket buffer;
    public static final int REQUEST_FINE_LOCATION = 2;

    // UI
    private ListAdapter_BleDevices adapter;
    private ListView scannedDevicesListView;
    private ProgressDialog progressDialog;
    private Button scanDevicesBtn;
        // Button States
        private final int BTN_SCANNING = 1;
        private final int BTN_DISABLED = 2;
        private final int BTN_ENABLED = 3;



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        // Set Observers
        setObservers();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private void initView(View view) {
        //get all needed views by id
        email = view.findViewById(R.id.email);
        uuid = view.findViewById(R.id.uuid);

        //get the currently signed in user
        model = ViewModelProviders.of(this).get(BluetoothServiceViewModel.class);
        //set the text of the views with user data
        email.setText(UserProfile.getEmail());
        uuid.setText(UserProfile.getUid());

        // Adapter
        adapter = new ListAdapter_BleDevices(getActivity());
        adapter.setOnDeviceClickListener(new ListAdapter_BleDevices.OnDeviceClickListener() {
            @Override
            public void onAction(BleDevice bleDevice) {
                if (adapter.getButtonState() == ListAdapter_BleDevices.BTN_CONNECTED) {
                    disconnectAllDevices();
                } else if (adapter.getButtonState() == ListAdapter_BleDevices.BTN_DISCONNECTED) {
                    connectDevice(bleDevice);
                }
            }
        });

        // ListView
        scannedDevicesListView = (ListView) view.findViewById(R.id.homeFrgLstViewScanDevices);
        scannedDevicesListView.setAdapter(adapter);

        // Scan Button
        scanDevicesBtn = (Button) view.findViewById(R.id.homeFrgBtnScanDevices);
        scanDevicesBtn.setOnClickListener(this);
        setScanButtonState(BTN_DISABLED);

        // Progress
        progressDialog = new ProgressDialog(getActivity());
    }

    private void setObservers() {
        model.getBinder().observe(getActivity(), new Observer<BluetoothService.BluetoothBinder>() {
            @Override
            public void onChanged(BluetoothService.BluetoothBinder bluetoothBinder) {
                if(bluetoothBinder == null){
                    Log.d("debug", "onChanged: unbound to service.");
                }
                else{
                    Log.d("debug", "onChanged: bound to service.");
                    mService = bluetoothBinder.getService();

                    // Device Observer
                    // BleDevice
                    mDevice = mService.getmBleDevice().getValue();
                    mService.getmBleDevice().observe(getActivity(), bleDeviceObserver);
                    // isScanning
                    mIsScanning = mService.isScanning().getValue();
                    mService.isScanning().observe(getActivity(),isScanningObserver);
                    if (!mIsScanning) {
                        setScanButtonState(BTN_ENABLED);
                    }
                    // Device Scanner Observer
                    mService.getmScanResult().observe(getActivity(),scanResultObserver);
                }
            }
        });
    }

    // Important!
    // Only remove observers if you do not want to persistently perform some action with the sensor packet
    // data once it is received
    public void removeObservers() {
        Log.d("debug","Observers Removed");
        if(mService != null){
            // Remove Observers
            mService.getmBleDevice().removeObserver(bleDeviceObserver);
            mService.isScanning().removeObserver(isScanningObserver);
            mService.getmScanResult().removeObserver(scanResultObserver);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("debug", "HomeFragment: onResume");
        bindService();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("debug", "HomeFragment: onStop");
        if(model.getBinder() != null){
            removeObservers();
            getActivity().unbindService(model.getServiceConnection());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

//    private void startService() {
//        Intent serviceIntent = new Intent(getActivity(), BluetoothService.class);
//        getActivity().startService(serviceIntent);
//        bindService();
//    }

    private void bindService() {
        Intent serviceIntent = new Intent(getActivity(), BluetoothService.class);
        getActivity().bindService(serviceIntent, model.getServiceConnection(), Context.BIND_AUTO_CREATE);
    }

    private void setScanButtonState(int buttonState) {
        switch (buttonState) {
            case BTN_ENABLED: {
                scanDevicesBtn.setText("Scan Devices");
                scanDevicesBtn.setEnabled(true);
                break;
            }
            case BTN_DISABLED: {
                scanDevicesBtn.setEnabled(false);
                break;
            }
            case BTN_SCANNING: {
                scanDevicesBtn.setText("Scanning...");
                scanDevicesBtn.setEnabled(false);
                break;
            }
        }
    }

    Observer<BleDevice> bleDeviceObserver = new Observer<BleDevice>() {
        @Override
        public void onChanged(BleDevice bleDevice) {
            // When device connects
            if (bleDevice != null) {
                adapter.addDevice(bleDevice);
                adapter.notifyDataSetChanged();
                Log.d("debug","successfully received BleDevice on HomeFragment");
            } else {
                // When device disconnects
                if (mDevice != null) {
                    // Replace the connected device, with the disconnected device!
                    adapter.replaceDevice(mDevice);
                    adapter.notifyDataSetChanged();
                }
                Log.d("debug","disconnected BleDevice on HomeFragment");
            }
            // Update UI about changes to device
            // ...
            mDevice = bleDevice;
        }
    };

    Observer<Boolean> isScanningObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean isScanning) {
            mIsScanning =  isScanning;
            // Update UI about changes to device
            // ...
            Log.d("debug", "isScanning: "+isScanning.toString());
            if (mIsScanning) {
                setScanButtonState(BTN_SCANNING);
            } else {
                setScanButtonState(BTN_ENABLED);
            }
        }
    };

    Observer<BleDevice> scanResultObserver = new Observer<BleDevice>() {
        @Override
        public void onChanged(BleDevice device) {
            Log.d("debug","scan");
            if (device != null) {
                adapter.addDevice(device);
                adapter.notifyDataSetChanged();
            }
            // Update UI about changes to device
            // ...
        }
    };

    @Override
    public void onClick(View view) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED) {
            // Should an explanation to users, why we require their fine location services
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.location_permissions_title)
                    .setMessage(R.string.location_permissions_text)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_FINE_LOCATION);
                            // Calls the onRequestPermissionsResult() function once user enters an input
                            // This function will call the startScan() function
                        }
                    })
                    .create()
                    .show();
        } else {
            startScan();
        }
    }

    private void startScan() {
        if (!mIsScanning) {
            if (mService != null) {
                adapter.clearScanDevice();
                adapter.notifyDataSetChanged();
                mService.startScan();
            }
        }
    }

    private void connectDevice(BleDevice device) {
        stopScan();
        if (device != null) {
            if (mService != null) {
                Log.d("debug", "connected to device");
                mService.connectDevice(device);
            }
        }
    }

    private void disconnectAllDevices() {
        if (mService != null) {
            mService.disconnectAllDevices();
        }
    }

    private void stopScan() {
        if (mService != null) {
            mService.stopScan();
        }
    }
}