package com.FIT3170.HealthMonitor;

import android.app.Activity;
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
    private Thread thread;
    private Activity mActivity;
    private int displayCount = 10; //number of data  points to display
    private int dataCount = 0;
    private final int ENTRY_COUNT_MAX = 15;

    public DashBoardFragment(Activity mActivity) {
        this.mActivity = mActivity;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

        bPMLineChart.setExtraOffsets(10f, 7f, 0f, 16f);
        setLineChartDummyData();
        beginChartThread();

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

    private void addEntry() {
        if (lineData != null) {
            int random = new Random().nextInt(10) + 60;
            ILineDataSet dataSet = lineData.getDataSetByIndex(0);
            Entry newEntry = new Entry(dataCount++, random);
            lineData.addEntry(newEntry, 0);
            lineData.notifyDataChanged();

            bPMLineChart.notifyDataSetChanged();
            bPMLineChart.setVisibleXRangeMaximum(displayCount);

            bPMLineChart.moveViewToX(lineData.getEntryCount());


            if (dataSet.getEntryCount() >= ENTRY_COUNT_MAX) {
                dataSet.removeFirst();
                for (int i = 0; i < dataSet.getEntryCount(); i++) {
                    Entry entryToChange = dataSet.getEntryForIndex(i);
                    entryToChange.setX(entryToChange.getX() - 1);
                }
            }

            Log.i("Dashboard", "added entry");
        }
    }

    private void beginChartThread() {
        if (thread != null) {
            thread.interrupt();
        }
        thread = new Thread() {
            private boolean running = true;

            public void run() {
                while (running) {
                    try {
                        mActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                addEntry();
                            }
                        });
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        running = false;
                        return;
                    }
                }
            }

        };
        thread.start();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (thread != null) {
            thread.interrupt();
        }
    }

    @Override
    public void onDetach() {
        if (thread != null) {
            thread.interrupt();
        }
        super.onDetach();
    }


    @Override
    public void onResume() {
        if (thread == null) {
            beginChartThread();
        }
        super.onResume();
    }

}