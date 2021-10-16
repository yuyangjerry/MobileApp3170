package com.FIT3170.HealthMonitor.DashBoard;

import static java.lang.Thread.sleep;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.FIT3170.HealthMonitor.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public class ChartManager {

    public enum ChartType {
        MovingAverage,
        DefaultECG,
    }

    private ChartType currentType = ChartType.DefaultECG;

    private LineChart lineChart;
    private Context context;
    private final int ENTRY_COUNT_MAX = 15;

    mChartData ecgChartData;
    mChartData maChartData;

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

    // switches graph type
    // Simply sets the lineChart to use another Data set
    public void switchChart(ChartType type) {
        switch (type) {
            case MovingAverage:
                lineChart.setData(maChartData.getData());
                break;
            case DefaultECG:
                lineChart.setData(ecgChartData.getData());

                break;
        }
        currentType = type;
        ClearChart();
    }


    //update chart data
    public void UpdateCharts(DataResult result) {
        if (lineChart.getData() == null) return;

        switch (currentType) {
            case DefaultECG: {
                ecgChartData.updateChart(result);
                notifyChanged(ecgChartData.getData().getEntryCount(), ecgChartData.getData().getEntryCount());
                break;
            }
            case MovingAverage: {
                maChartData.updateChart(result);
                notifyChanged(ENTRY_COUNT_MAX, maChartData.getData().getEntryCount());
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


    //clears all the charts. Should delete all previous data when switching charts.
    private void ClearChart() {
        maChartData.clearChart();
        ecgChartData.clearChart();
        lineChart.postInvalidate();
    }

    // set up line chart data sets
    private void setLineChartData() {
        LineDataSet _ECGDataSet = createDataSet("ecg", ContextCompat.getColor(context, R.color.primaryRed));
        ecgChartData = new ECGChartData(new LineData(_ECGDataSet));

        LineDataSet _MADataSet = createDataSet("ma", ContextCompat.getColor(context, R.color.black));
        maChartData = new MAChartData(5, new LineData(_MADataSet));
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
        //Data
        setLineChartData();

        //style
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setScaleEnabled(true);
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setExtraOffsets(0f, 7f, 0f, 16f);
        lineChart.setDragDecelerationEnabled(false);

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

    }

}
