package com.FIT3170.HealthMonitor.DashBoard;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public interface mChartData {

    //update chart data given result
    void updateChart(DataResult result);

    //clears the chart data
    void clearChart();

    //returns the data
    LineData getData();

}
