package com.FIT3170.HealthMonitor.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.FIT3170.HealthMonitor.R;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kelvin on 5/7/16.
 */
public class ListAdapter_BleDevices extends BaseAdapter {
    private final Context context;
    private final List<BleDevice> devices;
    private OnDeviceClickListener listener;

    // Ui
    private ViewHolder holder;

    // Button States
    private int currentState;
    public static final int BTN_CONNECTED = 1;
    public static final int BTN_DISCONNECTED = 2;


    public ListAdapter_BleDevices(Context context) {
        this.context = context;
        devices = new ArrayList<BleDevice>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(context, R.layout.btle_device_list_item, null);
            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.list_device_name = (TextView) convertView.findViewById(R.id.listItemDeviceName);
            holder.btn_action = (Button) convertView.findViewById(R.id.btnListItemDeviceAction);
        }

        final BleDevice device = devices.get(position);
        if (device != null) {
            String name = device.getName();
            boolean isConnected = BleManager.getInstance().isConnected(device);

            holder.list_device_name.setText(name);

            // isConnected Visual Logic
            if (isConnected) {
                currentState = BTN_CONNECTED;
            } else {
                currentState = BTN_DISCONNECTED;
            }
            updateButtonUI(currentState);

            // Event Listeners
            holder.btn_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onAction(device);
                    }
                }
            });

        }
        return convertView;
    }
    public void addDevice(BleDevice bleDevice) {
        removeDevice(bleDevice);
        devices.add(bleDevice);
    }

    public void replaceDevice(BleDevice device) {
        addDevice(device);
    }

    public void removeDevice(BleDevice bleDevice) {
        for (int i = 0; i < devices.size(); i++) {
            BleDevice device = devices.get(i);
            if (bleDevice.getKey().equals(device.getKey())) {
                devices.remove(i);
            }
        }
    }

    public void clearConnectedDevice() {
        for (int i = 0; i < devices.size(); i++) {
            BleDevice device = devices.get(i);
            if (BleManager.getInstance().isConnected(device)) {
                devices.remove(i);
            }
        }
    }

    public void clearScanDevice() {
        for (int i = 0; i < devices.size(); i++) {
            BleDevice device = devices.get(i);
            if (!BleManager.getInstance().isConnected(device)) {
                devices.remove(i);
            }
        }
    }

    public void clear() {
        clearConnectedDevice();
        clearScanDevice();
    }

    private void updateButtonUI(int btnState) {
        switch (btnState) {
            case BTN_CONNECTED:
                holder.btn_action.setText("DISCONNECT");
                holder.btn_action.setBackgroundColor(context.getResources().getColor(R.color.primaryRed));
                break;
            case BTN_DISCONNECTED:
                holder.btn_action.setText("CONNECT");
                holder.btn_action.setBackgroundColor(context.getResources().getColor(R.color.purple_500));
                break;
        }
    }

    public int getButtonState() {
        return currentState;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public BleDevice getItem(int position) {
        if (position > devices.size())
            return null;
        return devices.get(position);
    }

    class ViewHolder {
        TextView list_device_name;
        Button btn_action;
    }


    public interface OnDeviceClickListener {
        void onAction(BleDevice bleDevice);

    }

    public void setOnDeviceClickListener(OnDeviceClickListener listener) {
        this.listener = listener;
    }

}
