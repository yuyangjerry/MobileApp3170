package com.FIT3170.HealthMonitor.DashBoard;

import android.content.Context;
import android.service.autofill.Dataset;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.FIT3170.HealthMonitor.R;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.LinkedList;
import java.util.Queue;

public class MAChartData implements mChartData {

    private MovingAverage movingAverage;
    private LineData maData;
    private final int MAX_ENTRIES = 15;

    public MAChartData(int period, LineData _lineData) {
        movingAverage = new MovingAverage(period);
        maData = _lineData;
    }

    @Override
    public void updateChart(DataResult result) {
        double bpm = result.bpm;
        //if result produces nan return
        if (Double.isNaN(bpm)) {
            return;
        }
        //the dataset is stores in first index of LineData
        ILineDataSet dataSet = maData.getDataSetByIndex(0);

        if (dataSet != null) {

            movingAverage.add(bpm);
            //If exceeds max entry count, remove first value, update all indexes
            if (dataSet.getEntryCount() == MAX_ENTRIES) {
                dataSet.removeFirst();
                for (int i = 0; i < dataSet.getEntryCount(); i++) {
                    dataSet.getEntryForIndex(i).setX(dataSet.getEntryForIndex(i).getX() - 1);
                }
            }


            float maAvg = (float) movingAverage.getAverage();

            if (maAvg > 0.0) {
                Entry newEntry = new Entry(dataSet.getEntryCount(), maAvg);
                Log.d("chart", String.valueOf(dataSet.getEntryCount()));
                dataSet.addEntry(newEntry);
            }
            //everytime dataset is changed, it must be notified
            maData.notifyDataChanged();
        }


    }

    @Override
    public void clearChart() {
        maData.getDataSetByIndex(0).clear();
        maData.notifyDataChanged();
        movingAverage.clear();
    }

    @Override
    public LineData getDataSet() {
        return maData;
    }
}

//Used by MAChartData to calculate moving average
class MovingAverage {

    private final Queue<Double> window = new LinkedList<Double>();
    private final int period;
    private double sum = 0.0;

    public MovingAverage(int period) {
        assert period > 0 : "Period must be a positive integer";
        this.period = period;
    }

    public void add(double num) {
        sum += num;
        window.add(num);
        if (window.size() > period) {
            sum -= window.remove();
        }
    }

    public void clear() {
        sum = 0.0;
        window.clear();
    }

    public double getAverage() {
        if (window.isEmpty()) return 0.0; // technically the average is undefined
        int divisor = window.size();
        double avgSum = sum / divisor;
        return avgSum;
    }
}
