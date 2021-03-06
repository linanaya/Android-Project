package com.example.myapplication.utils.BluetoothTool;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Administrator on 2017/4/4.
 */

public class ClientThread implements Runnable {

    final String TAG = "ClientThread";

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice device;

    Handler uiHandler;
    Handler writeHandler;

    BluetoothSocket socket;
    OutputStream out;
    InputStream in;
    BufferedReader reader;
    public ClientThread(BluetoothAdapter bluetoothAdapter, BluetoothDevice device) {
        this.bluetoothAdapter = bluetoothAdapter;
        this.device = device;
        BluetoothSocket tmp = null;
        try {
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(Params.UUID));
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket = tmp;
    }

    @Override
    public void run() {

        Log.e(TAG, "----------------- do client thread run()");
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        try {
            socket.connect();
            System.out.println("设备"+socket.getRemoteDevice().getName()+"连接成功");
            out = socket.getOutputStream();
            in = socket.getInputStream();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "-----------do client read run()");
                    byte[] buffer = new byte[1024];
                    int len;
                    String content;
                    try {
                        while ((len=in.read(buffer)) != -1) {
                            content=new String(buffer, 0, len);
                            Log.e(TAG, "------------- client read data in while ,send msg ui" + content);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            System.out.println("设备"+socket.getRemoteDevice().getName()+"连接失败");
            Log.e(TAG, "-------------- exception");
        }
    }
    public void write(String data){
        try {
            out.write(data.getBytes("utf-8"));
            Log.e(TAG, "---------- write data ok "+data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}