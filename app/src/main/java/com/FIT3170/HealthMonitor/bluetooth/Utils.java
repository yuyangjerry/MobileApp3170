package com.FIT3170.HealthMonitor.bluetooth;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import java.util.UUID;

/**
 * Created by Kelvin on 5/8/16.
 */
public class Utils {
    public static final int PROPERTY_NOTIFY = 1;
    public static final int PROPERTY_READ = 2;
    public static final int PROPERTY_WRITE = 3;
    public static final int PROPERTY_WRITE_NO_RESPONSE = 4;
    public static final int PROPERTY_INDICATE = 5;


//    public static void toast(Context context, String string) {
//
//        Toast toast = Toast.makeText(context, string, Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
//        toast.show();
//    }

    // Returns of the preassigned service that has the heart-rate values
    public static UUID getSensorServiceUUID() {
        return UUID.fromString("713D0000-503E-4C75-BA94-3148F18D941E");
    }

    // Returns of the preassigned characteristic that has the heart-rate values
    public static UUID getSensorCharacteristicUUID() {
        return UUID.fromString("713D0002-503E-4C75-BA94-3148F18D941E");
    }



}
