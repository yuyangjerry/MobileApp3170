package com.FIT3170.HealthMonitor.DashBoard;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.FIT3170.HealthMonitor.R;
import com.FIT3170.HealthMonitor.database.DataPacket;
import com.FIT3170.HealthMonitor.database.DataPoint;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class ChartManager {

    public enum ChartType {
        MovingAverage,
        DefaultECG,
    }

    private ChartType currentType = ChartType.DefaultECG;

    private MovingAverage movingAverage;
    private LineChart lineChart;
    private LineData eCGData;
    private LineData maData;
    private Context context;
    private final int ENTRY_COUNT_MAX = 15;
    private int count = 0;

    public ChartManager(Context context, LineChart chart) {
        this.context = context;
        lineChart = chart;
        movingAverage = new MovingAverage(10);
        SetUpLineChart();
    }

    private void setLineChartData() {
        // documentation for LineDataSet class
        //https://javadoc.jitpack.io/com/github/PhilJay/MPAndroidChart/v3.1.0/javadoc/

        LineDataSet eCGDataSet = createDataSet("ecg", ContextCompat.getColor(context, R.color.primaryRed));
        eCGData = new LineData(eCGDataSet);

        LineDataSet maDataSet = createDataSet("ma", ContextCompat.getColor(context, R.color.black));
        maData = new LineData(maDataSet);

    }

    public void UpdateCharts(DataPacket dataPacket, double bpm) {
        switch (currentType) {
            case DefaultECG: {
                updateECGChart(dataPacket);
                break;
            }
            case MovingAverage: {
                Log.d("tag", String.valueOf(bpm));
                UpdateMAChart(bpm);
                break;
            }
        }
    }


    public void switchGraph(ChartType type) {
        switch (type) {
            case MovingAverage:
                Log.d("sw", "ma");

                lineChart.setData(maData);
                break;
            case DefaultECG:

                lineChart.setData(eCGData);

                break;
        }
        currentType = type;
        ClearChart();
    }

    private void ClearChart() {
        count = 0;
        maData.getDataSetByIndex(0).clear();
        eCGData.getDataSetByIndex(0).clear();
        movingAverage.clear();
        lineChart.invalidate();
    }


    private LineDataSet createDataSet(String label, int color) {
        LineDataSet newSet = new LineDataSet(null, label);
        newSet.setLineWidth(2f);
        newSet.setDrawCircles(false);
        newSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER); //curved shape
        newSet.setColor(color);
        newSet.setDrawValues(false);
        return newSet;
    }


    public void UpdateMAChart(double bpm) {

        ILineDataSet dataSet = maData.getDataSetByIndex(0);

        if (dataSet != null) {

            movingAverage.add(bpm);
            if (dataSet.getEntryCount() > ENTRY_COUNT_MAX) {
                dataSet.removeFirst();
            }


            float maAvg = (float) movingAverage.getAverage();

            if (maAvg > 0.0) {
                Entry newEntry = new Entry(count, maAvg);
                Log.d("chart", String.valueOf(maAvg));
                dataSet.addEntry(newEntry);
                count += 1;
            }

            notifyChanged(ENTRY_COUNT_MAX, count);
        }


    }

    private void notifyChanged(int xRangeMax, int moveToX) {
        eCGData.notifyDataChanged();
        maData.notifyDataChanged();

        lineChart.notifyDataSetChanged();
        lineChart.setVisibleXRangeMaximum(xRangeMax);
        lineChart.moveViewToX(moveToX);
    }


    public void updateECGChart(DataPacket dataPacket) {
        if (eCGData != null && dataPacket != null) {
            ILineDataSet dataSet = eCGData.getDataSetByIndex(0);
            dataSet.clear();
            Integer dataPointCount = 0;
            for (DataPoint dataPoint : dataPacket.getData()) {
                Entry newEntry = new Entry(dataPointCount, dataPoint.getValue());
                eCGData.addEntry(newEntry, 0);
                dataPointCount += 1;
            }
            notifyChanged(dataPointCount, dataPointCount);
            Log.i("Dashboard", "");

        }

    }

    //Sets up the main line chart
    private void SetUpLineChart() {
        //style
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setScaleEnabled(true);

        //X Axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setLabelCount(6, true);

        //Right Axis
        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.getAxisRight().setDrawAxisLine(false);
        lineChart.getAxisRight().setDrawGridLines(false);

        //Left Axis
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.getAxisLeft().setLabelCount(6, true);

        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);

        lineChart.setExtraOffsets(0f, 7f, 0f, 16f);
        setLineChartData();

    }

}
