package com.FIT3170.HealthMonitor.DashBoard;

import static java.lang.Thread.sleep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.FIT3170.HealthMonitor.R;
import com.FIT3170.HealthMonitor.bluetooth.BluetoothService;
import com.FIT3170.HealthMonitor.bluetooth.BluetoothServiceModel;
import com.FIT3170.HealthMonitor.database.DataPacket;
import com.FIT3170.HealthMonitor.database.ECGAlgorithm;
import com.FIT3170.HealthMonitor.database.PeakToPeakAlgorithm;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class DashBoardFragment extends Fragment {

    private LineChart bPMLineChart;
    private TextView heartRateTextView;

    private Activity mActivity;
    private int displayCount = 10; //number of data  points to display
    private int dataCount = 0;
    private final int ENTRY_COUNT_MAX = 15;

    private BluetoothService mService;
    private int mConnectionStatus;
    private BluetoothServiceModel model;
    private DataPacket mDataPacket;
    private ECGAlgorithm algorithm;

    private Button maChartBtn, ecgChartBtn;
    private TextView mainTextView;


    public DashBoardFragment(Activity mActivity) {
        this.mActivity = mActivity;
    }

    // Required empty public constructor
    public DashBoardFragment() {
    }

    private ChartManager chartManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set View Model
        model = new BluetoothServiceModel();
        setObservers();

        //get all needed views by id

        bPMLineChart = view.findViewById(R.id.line_chart);
        heartRateTextView = view.findViewById(R.id.heart_rate_text);
        mainTextView = view.findViewById(R.id.dashboard_main_text);

        // Create algorithm class
        algorithm = new ECGAlgorithm(new PeakToPeakAlgorithm());
        chartManager = new ChartManager(getContext(), bPMLineChart);


        maChartBtn = view.findViewById(R.id.ma_chart_btn);
        maChartBtn.setOnClickListener(v -> chartManager.switchGraph(ChartManager.ChartType.MovingAverage));


        ecgChartBtn = view.findViewById(R.id.ecg_chart_btn);
        ecgChartBtn.setOnClickListener(v -> chartManager.switchGraph(ChartManager.ChartType.DefaultECG));


        chartManager.switchGraph(ChartManager.ChartType.DefaultECG);


    }


    private void setObservers() {
        model.getBinder().observe(getActivity(), new Observer<BluetoothService.BluetoothBinder>() {
            @Override
            public void onChanged(BluetoothService.BluetoothBinder bluetoothBinder) {
                if (bluetoothBinder == null) {
                    Log.d("debug", "onChanged: unbound to service.");
                    mService = null;
                }
                else{
                    Log.d("debug", "onChanged: bound to service.");
                    mService = bluetoothBinder.getService();
                    mService.getConnectionStatus().observe(getActivity(), connectionStatusObserver);
                    mService.getDataPacketShortDuration().observe(getActivity(), dataPacketObserver);
                }
            }
        });
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

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        bindService();
    }

    // Important!
    // Only remove observers if you do not want to persistently perform some action with the sensor packet
    // data once it is received
    public void removeObservers() {
        Log.d("debug","Observers Removed");
        if(mService != null){
            // Remove Observers
            mService.getDataPacketShortDuration().removeObserver(dataPacketObserver);
            mService.getConnectionStatus().removeObserver(connectionStatusObserver);
        }
    }

//    private void startService(){
//        Intent serviceIntent = new Intent(getActivity(), BluetoothService.class);
//        getActivity().startService(serviceIntent);
//        bindService();
//    }

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
            // change implementation
            // store algorithm class as local
            // algorithm.getPeakCount(dataPacket)
            // inside algorithm store last peak value + distance to end of data packet
            //float bpm = dataPacket.getPeakCount();
            double bpm = algorithm.calculate(dataPacket);
//            String outString = Float.toString(bpm);
            heartRateTextView.setText(String.format("%.1f", bpm));

            // Dummy Code
            // Sensor Is Spitting Millivolt Values that are
            chartManager.UpdateCharts(new DataResult(bpm, dataPacket));

        }
    };

    Observer<Integer> connectionStatusObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer integer) {
            if (mainTextView != null) {
                if (integer != mService.CONNECTION_CONNECTED) {
                    mainTextView.setText("ECG device not connected");
                } else {
                    mainTextView.setText("Battery: 50%");
                    Toast.makeText(getContext(), "Device unconnected", Toast.LENGTH_LONG);
                }
            }
            Log.d("debug", "Connection status: " + integer.toString());
        }
    };


}