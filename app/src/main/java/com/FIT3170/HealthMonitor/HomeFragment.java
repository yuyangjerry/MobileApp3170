package com.FIT3170.HealthMonitor;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.FIT3170.HealthMonitor.bluetooth.BluetoothService;
import com.FIT3170.HealthMonitor.bluetooth.BluetoothServiceViewModel;
import com.FIT3170.HealthMonitor.database.UserProfile;
import com.clj.fastble.data.BleDevice;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private TextView email;
    private TextView uuid;

    // Bluetooth
    private BluetoothService service;
    private BleDevice device;
    private BluetoothServiceViewModel model;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //get all needed views by id
        email = view.findViewById(R.id.email);
        uuid = view.findViewById(R.id.uuid);

        //get the currently signed in user
        model = ViewModelProviders.of(this).get(BluetoothServiceViewModel.class);
        //set the text of the views with user data
        email.setText(UserProfile.getEmail());
        uuid.setText(UserProfile.getUid());

//        // Set Observers
        setObservers();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
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
                    service = bluetoothBinder.getService();

                    // Device Observer
                    // BleDevice
                    service.getBleDevice().observe(getActivity(), bleDeviceObserver);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        startService();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(model.getBinder() != null){
            getActivity().unbindService(model.getServiceConnection());
        }
    }

    private void startService(){
        Intent serviceIntent = new Intent(getActivity(), BluetoothService.class);
        getActivity().startService(serviceIntent);
        bindService();
    }

    private void bindService() {
        Intent serviceIntent = new Intent(getActivity(), BluetoothService.class);
        getActivity().bindService(serviceIntent, model.getServiceConnection(), Context.BIND_AUTO_CREATE);
    }

    Observer<BleDevice> bleDeviceObserver = new Observer<BleDevice>() {
        @Override
        public void onChanged(BleDevice bleDevice) {
            device = bleDevice;
            // Update UI about changes to device
            // ...
        }
    };



}