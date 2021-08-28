package com.FIT3170.HealthMonitor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.FIT3170.HealthMonitor.bluetooth.BluetoothService;
import com.FIT3170.HealthMonitor.bluetooth.BluetoothServiceViewModel;
import com.FIT3170.HealthMonitor.database.DataPacket;
import com.FIT3170.HealthMonitor.database.DataPoint;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class DashBoardFragment extends Fragment {

    private LineChart bPMLineChart;
    private TextView heartRateTextView;

    private BluetoothService mService;
    private int mConnectionStatus;
    private BluetoothServiceViewModel model;

    private DataPacket mDataPacket;


    public DashBoardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set View Model
        model = ViewModelProviders.of(this).get(BluetoothServiceViewModel.class);
        setObservers();

        //get all needed views by id
        bPMLineChart = view.findViewById(R.id.line_chart);
        heartRateTextView = view.findViewById(R.id.heart_rate_text);

        SetUpLineChart();

    }


    private void SetUpLineChart() {
        //style
        bPMLineChart.setBackgroundColor(Color.WHITE);

        //X Axis
        bPMLineChart.getXAxis().setDrawGridLines(false);
        bPMLineChart.getXAxis().setDrawLabels(true);
        bPMLineChart.getXAxis().setDrawAxisLine(true);
        bPMLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        //Right Axis
        bPMLineChart.getAxisRight().setDrawLabels(false);
        bPMLineChart.getAxisRight().setDrawAxisLine(false);
        bPMLineChart.getAxisRight().setDrawGridLines(false);

        //Left Axis
        bPMLineChart.getAxisLeft().setDrawGridLines(false);
        bPMLineChart.getAxisLeft().setDrawAxisLine(false);

        bPMLineChart.getXAxis().setLabelCount(5, true);
        bPMLineChart.getLegend().setEnabled(false);
        bPMLineChart.getDescription().setEnabled(false);
        ;
        bPMLineChart.setExtraOffsets(10f, 7f, 0f, 16f);
        SetLineChartDummyData();


    }


    private void SetLineChartDummyData() {
        int count = 12;
        float[] bpmDummyData = {61f, 65f, 64f, 72f, 74f, 79f, 65f, 63f, 65f, 67f, 64f, 66f, 67f, 66f};
        ArrayList<Entry> values = new ArrayList<Entry>();
        for (int i = 0; i < count; i++) {
            values.add(new Entry(i, bpmDummyData[i]));
        }

        // documentation for LineDataSet class
        //https://javadoc.jitpack.io/com/github/PhilJay/MPAndroidChart/v3.1.0/javadoc/
        LineDataSet dummySet = new LineDataSet(values, "BPM");
        dummySet.setLineWidth(1.7f);
        dummySet.setDrawCircles(false);
        dummySet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER); //curved shape
        dummySet.setColor(ContextCompat.getColor(getContext(), R.color.primaryRed));
        dummySet.setDrawValues(false);


        LineData data = new LineData(dummySet);
        bPMLineChart.setData(data);
    }

    private void setObservers() {
        model.getBinder().observe(getActivity(), new Observer<BluetoothService.BluetoothBinder>() {
            @Override
            public void onChanged(BluetoothService.BluetoothBinder bluetoothBinder) {
                if(bluetoothBinder == null){
                    Log.d("debug", "onChanged: unbound to service.");
                    mService = null;
                }
                else{
                    Log.d("debug", "onChanged: bound to service.");
                    mService = bluetoothBinder.getService();

                    mConnectionStatus = mService.getConnectionStatus().getValue();
                    mService.getConnectionStatus().observe(getActivity(), connectionStatusObserver);

                    mDataPacket = mService.getDataPacket().getValue();
                    mService.getDataPacket().observe(getActivity(), dataPacketObserver);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("debug", "DashboardFragment: onResume");
        startService();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("debug", "DashboardFragment: onStop");
//        if (mDevice != null) {
//            disconnectAllDevices();
//        }
        if(model.getBinder() != null){
            removeObservers();
            getActivity().unbindService(model.getServiceConnection());
        }
    }

    // Important!
    // Only remove observers if you do not want to persistently perform some action with the sensor packet
    // data once it is received
    public void removeObservers() {
        Log.d("debug","Observers Removed");
        if(mService != null){
            // Remove Observers
            mService.getDataPacket().removeObserver(dataPacketObserver);
            mService.getConnectionStatus().removeObserver(connectionStatusObserver);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    // Work on this function here
    Observer<DataPacket> dataPacketObserver = new Observer<DataPacket>() {
        @Override
        public void onChanged(DataPacket dataPacket) {
            Log.d("debug","-----------------------------");
            Log.d("debug", "Data Packet Size: "+ dataPacket.getData().size()+"");
            Log.d("debug","-----------------------------");
            Integer sum = 0;
            for (DataPoint dp : dataPacket.getData()) {
                sum += dp.getValue();
            }
            Integer avg = sum / dataPacket.getData().size();
            heartRateTextView.setText(avg.toString()+" mV");
        }
    };

    Observer<Integer> connectionStatusObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer integer) {
            Log.d("debug", "Connection status: "+integer.toString());
        }
    };




}