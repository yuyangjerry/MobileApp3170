package com.FIT3170.HealthMonitor.DashBoard;

import static java.lang.Thread.sleep;

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

import java.util.concurrent.ThreadLocalRandom;

public class ChartManager {

    public enum ChartType {
        MovingAverage,
        DefaultECG,
    }

    private ChartType currentType = ChartType.DefaultECG;

    private LineChart lineChart;
    private Context context;
    private final int ENTRY_COUNT_MAX = 15;

    mChartData _ECGChart;
    mChartData _MAChart;

    public ChartManager(Context context, LineChart chart) {
        this.context = context;
        lineChart = chart;
        SetUpLineChart();

//        Runnable runnable = () -> {
//            while (true) {
//
//                try {
//                    int randomNum = ThreadLocalRandom.current().nextInt(65, 75);
//                    UpdateCharts(new DataResult(randomNum));
//                    sleep(500);
//                } catch (InterruptedException e) {
//
//                }
//            }
//
//        };
//        Thread thread = new Thread(runnable);
//        thread.start();

    }

    // set up line chart data sets
    private void setLineChartData() {
        LineDataSet _ECGDataSet = createDataSet("ecg", ContextCompat.getColor(context, R.color.primaryRed));
        _ECGChart = new ECGChartData(new LineData(_ECGDataSet));

        LineDataSet _MADataSet = createDataSet("ma", ContextCompat.getColor(context, R.color.black));
        _MAChart = new MAChartData(5, new LineData(_MADataSet));
    }

    //update chart data
    public void UpdateCharts(DataResult result) {
        if (lineChart.getData() == null) return;

        switch (currentType) {
            case DefaultECG: {
                _ECGChart.updateChart(result);
                notifyChanged(_ECGChart.getDataSet().getEntryCount(), _ECGChart.getDataSet().getEntryCount());
                break;
            }
            case MovingAverage: {
                _MAChart.updateChart(result);
                notifyChanged(ENTRY_COUNT_MAX, _MAChart.getDataSet().getEntryCount());
                break;
            }
        }
    }

    // Call to update the visual line chart
    private void notifyChanged(int xRangeMax, int moveToX) {
        if (xRangeMax < 1 || moveToX < 1) {
            return;
        }

        lineChart.notifyDataSetChanged();
        lineChart.setVisibleXRangeMaximum(xRangeMax);
        lineChart.moveViewToX(moveToX);
    }


    // switches graph type
    public void switchGraph(ChartType type) {
        switch (type) {
            case MovingAverage:
                lineChart.setData(_MAChart.getDataSet());
                break;
            case DefaultECG:
                lineChart.setData(_ECGChart.getDataSet());

                break;
        }
        currentType = type;
        ClearChart();
    }

    //clears all the charts
    private void ClearChart() {
        _MAChart.clearChart();
        _ECGChart.clearChart();
        lineChart.postInvalidate();
    }

    //create a new Line data set.
    public LineDataSet createDataSet(String label, int color) {
        LineDataSet newSet = new LineDataSet(null, label);
        newSet.setLineWidth(2f);
        newSet.setDrawCircles(false);
        newSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER); //curved shape
        newSet.setColor(color);
        newSet.setDrawValues(false);
        return newSet;
    }


    //Sets up the line chart
    private void SetUpLineChart() {
        setLineChartData();
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
        lineChart.setDragDecelerationEnabled(false);


    }

}
