package com.FIT3170.HealthMonitor.database;

/**
 * Stores a Algorithm class,the calculationAlgorithm can be changed to anything else if a better algorithm is found.
 */
public class ECGAlgorithm {

    private Algorithm calculationAlgorithm;

    // The calculationAlgorithm is passed in as input, must be a Algorithm class.
    public ECGAlgorithm(Algorithm calculationAlgorithm){
        this.calculationAlgorithm = calculationAlgorithm;
    }

    public double calculate(DataPacket dataPacket){
        return calculationAlgorithm.getBPM(dataPacket);

    }
}

