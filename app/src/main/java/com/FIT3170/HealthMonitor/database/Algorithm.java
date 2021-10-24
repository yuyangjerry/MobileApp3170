package com.FIT3170.HealthMonitor.database;

/**
 * An interface for all algorithm to follow.
 */
public interface Algorithm {
    double getBPM(DataPacket dataPacket);
}
