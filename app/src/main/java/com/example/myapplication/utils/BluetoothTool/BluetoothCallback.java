package com.example.myapplication.utils.BluetoothTool;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.util.Set;

public interface BluetoothCallback {
    void getLocation(GPSObj location);
    void getBattery(int battery);
    void connectSuccess(BluetoothSocket socket);
    void disconnect(BluetoothSocket socket);
    void connectFail(BluetoothDevice device);
    void searchCompleted(Set<BluetoothDevice> deviceSet);
}
