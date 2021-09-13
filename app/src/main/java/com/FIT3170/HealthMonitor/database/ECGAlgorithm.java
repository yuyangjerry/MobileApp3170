package com.FIT3170.HealthMonitor.database;

public class ECGAlgorithm {

    private Algorithm calculationAlgorithm;

    public ECGAlgorithm(Algorithm calculationAlgorithm){
        this.calculationAlgorithm = calculationAlgorithm;
    }

    public double calculate(DataPacket dataPacket){
        return calculationAlgorithm.getBPM(dataPacket);

    }
}

