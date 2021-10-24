package com.FIT3170.HealthMonitor.bluetooth;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import java.util.UUID;

/**
 * Utility class that houses dependencies that may be suspect to change. This includes the manually
 * assigned UUID of the service and GATT characteristic that stores the Notification Channel for Heart Rate sensor
 * values
 */
public class BluetoothUtils {

    // Returns of the preassigned service that has the heart-rate values
    public static UUID getSensorServiceUUID() {
        return UUID.fromString("713D0000-503E-4C75-BA94-3148F18D941E");
    }

    // Returns of the preassigned characteristic that has the heart-rate values
    public static UUID getSensorCharacteristicUUID() {
        return UUID.fromString("713D0002-503E-4C75-BA94-3148F18D941E");
    }



}
