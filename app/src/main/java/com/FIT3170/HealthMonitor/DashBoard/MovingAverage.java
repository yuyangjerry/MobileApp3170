package com.FIT3170.HealthMonitor.DashBoard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.Queue;

public class MovingAverage {

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
        window.clear();
    }

    public double getAverage() {
        if (window.isEmpty()) return 0.0; // technically the average is undefined
        int divisor = window.size();
        double avgSum = sum / divisor;
        return avgSum;
    }
}