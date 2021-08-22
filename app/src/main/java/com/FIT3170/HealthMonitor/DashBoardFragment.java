package com.FIT3170.HealthMonitor;

import android.graphics.Color;
import android.os.Bundle;
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

import java.util.ArrayList;

public class DashBoardFragment extends Fragment {

    private LineChart bPMLineChart;
    private TextView heartRateTextView;


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


}