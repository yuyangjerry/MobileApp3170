package com.FIT3170.HealthMonitor;

import android.app.Activity;
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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.Random;

public class DashBoardFragment extends Fragment {

    private LineChart bPMLineChart;
    private TextView heartRateTextView;
    private LineData lineData;
    // Thread for line chart
//    private Thread thread;
    private Activity mActivity;
    private int displayCount = 10; //number of data  points to display
    private int dataCount = 0;
    private final int ENTRY_COUNT_MAX = 15;

    private BluetoothService mService;
    private int mConnectionStatus;
    private BluetoothServiceViewModel model;
    private DataPacket mDataPacket;

    public DashBoardFragment(Activity mActivity) {
        this.mActivity = mActivity;
    }
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
        bPMLineChart.setScaleEnabled(true);

        //X Axis
        XAxis xAxis = bPMLineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setLabelCount(6, true);

        //Right Axis
        bPMLineChart.getAxisRight().setDrawLabels(false);
        bPMLineChart.getAxisRight().setDrawAxisLine(false);
        bPMLineChart.getAxisRight().setDrawGridLines(false);

        //Left Axis
        bPMLineChart.getAxisLeft().setDrawGridLines(true);
        bPMLineChart.getAxisLeft().setDrawAxisLine(false);
        bPMLineChart.getAxisLeft().setLabelCount(6, true);

        bPMLineChart.getLegend().setEnabled(false);
        bPMLineChart.getDescription().setEnabled(false);

        bPMLineChart.setExtraOffsets(0f, 7f, 0f, 16f);
        setLineChartDummyData();
//        beginChartThread();

    }


    private void setLineChartDummyData() {
        // documentation for LineDataSet class
        //https://javadoc.jitpack.io/com/github/PhilJay/MPAndroidChart/v3.1.0/javadoc/

        LineDataSet dummySet = createDataSet();
        lineData = new LineData(dummySet);


        bPMLineChart.setData(lineData);
    }

    private LineDataSet createDataSet() {
        LineDataSet newSet = new LineDataSet(null, "BPM");
        newSet.setLineWidth(2f);
        newSet.setDrawCircles(false);
        newSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER); //curved shape
        newSet.setColor(ContextCompat.getColor(getContext(), R.color.primaryRed));
        newSet.setDrawValues(false);
        return newSet;
    }

//    private void addEntry() {
//        if (lineData != null) {
//            int random = new Random().nextInt(10) + 60;
//            ILineDataSet dataSet = lineData.getDataSetByIndex(0);
//            Entry newEntry = new Entry(dataCount++, random);
//            lineData.addEntry(newEntry, 0);
//            lineData.notifyDataChanged();
//
//            bPMLineChart.notifyDataSetChanged();
//            bPMLineChart.setVisibleXRangeMaximum(displayCount);
//
//            bPMLineChart.moveViewToX(lineData.getEntryCount());
//
//
//            if (dataSet.getEntryCount() >= ENTRY_COUNT_MAX) {
//                dataSet.removeFirst();
//                for (int i = 0; i < dataSet.getEntryCount(); i++) {
//                    Entry entryToChange = dataSet.getEntryForIndex(i);
//                    entryToChange.setX(entryToChange.getX() - 1);
//                }
//            }
//
//            Log.i("Dashboard", "added entry");
//        }
//    }

    private void graphPacket(DataPacket dataPacket) {
        if (lineData != null) {
            ILineDataSet dataSet = lineData.getDataSetByIndex(0);
            dataSet.clear();
            Integer dataPointCount = 0;
            for (DataPoint dataPoint: dataPacket.getData()) {
                Entry newEntry = new Entry(dataPointCount, dataPoint.getValue());
                lineData.addEntry(newEntry, 0);
                dataPointCount += 1;
            }
            lineData.notifyDataChanged();
            bPMLineChart.notifyDataSetChanged();
            bPMLineChart.setVisibleXRangeMaximum(dataPointCount);

            bPMLineChart.moveViewToX(dataPointCount);



//            if (dataSet.getEntryCount() >= ENTRY_COUNT_MAX) {
//                dataSet.removeFirst();
//                for (int i = 0; i < dataSet.getEntryCount(); i++) {
//                    Entry entryToChange = dataSet.getEntryForIndex(i);
//                    entryToChange.setX(entryToChange.getX() - 1);
//                }
//            }

            Log.i("Dashboard", "");
        }
    }

//    private void beginChartThread() {
//        if (thread != null) {
//            thread.interrupt();
//        }
//        thread = new Thread() {
//            private boolean running = true;
//
//            public void run() {
//                while (running) {
//                    try {
//                        mActivity.runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                addEntry();
//                            }
//                        });
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        running = false;
//                        return;
//                    }
//                }
//            }
//
//        };
//        thread.start();
//
//    }

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
//        if (thread != null) {
//            thread.interrupt();
//        }
    }

    @Override
    public void onDetach() {
//        if (thread != null) {
//            thread.interrupt();
//        }
        super.onDetach();
    }

    @Override
    public void onResume() {
//        if (thread == null) {
//            beginChartThread();
//        }
        super.onResume();
        startService();
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
            float bpm = dataPacket.getPeakCount();
//            String outString = Float.toString(bpm);
            heartRateTextView.setText(String.format("%.1f", bpm));

            // Dummy Code
            // Sensor Is Spitting Millivolt Values that are
            graphPacket(dataPacket);
        }
    };

    Observer<Integer> connectionStatusObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer integer) {
            Log.d("debug", "Connection status: "+integer.toString());
        }
    };


}