package com.FIT3170.HealthMonitor.DashBoard;

import com.FIT3170.HealthMonitor.database.DataPacket;

public class DataResult {
    public double bpm;
    public DataPacket dataPacket;

    public DataResult(double _bpm, DataPacket _dpacket) {
        bpm = _bpm;
        dataPacket = _dpacket;

    }

    public DataResult(double _bpm) {
        bpm = _bpm;
        dataPacket = null;
    }


}
