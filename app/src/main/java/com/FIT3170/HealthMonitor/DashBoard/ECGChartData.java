package com.FIT3170.HealthMonitor.DashBoard;

import android.util.Log;

import com.FIT3170.HealthMonitor.database.DataPoint;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class ECGChartData implements mChartData {

    LineData eCGData;

    public ECGChartData(LineData _lineData) {

        eCGData = _lineData;
    }


    @Override
    public void updateChart(DataResult result) {
        if (result.dataPacket != null) {
            clearChart();
            int dataPointCount = 0;
            for (DataPoint dataPoint : result.dataPacket.getData()) {
                Entry newEntry = new Entry(dataPointCount, dataPoint.getValue());
                eCGData.addEntry(newEntry, 0);
                dataPointCount += 1;
            }
//            notifyChanged(dataPointCount, dataPointCount);
            eCGData.notifyDataChanged();
        }
    }

    @Override
    public void clearChart() {
        eCGData.getDataSetByIndex(0).clear();
        eCGData.notifyDataChanged();
    }

    @Override
    public LineData getDataSet() {
        return eCGData;
    }
}
