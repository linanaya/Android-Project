package com.example.myapplication.utils.BluetoothTool;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Created by Administrator on 2017/4/4.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class ClientThread implements Runnable {

    final String TAG = "ClientThread";

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice device;
    Handler handler;
    BluetoothSocket socket;
    OutputStream out;
    InputStream in;
    BufferedReader reader;
    public ClientThread(BluetoothAdapter bluetoothAdapter, BluetoothDevice device, Handler handler) {
        this.bluetoothAdapter = bluetoothAdapter;
        this.device = device;
        this.handler = handler;
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
            sendMessage(Params.CONNECT_SUCCESS,socket);
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
                            dataProcessing(content);
                        }
                    } catch (IOException e) {
                        sendMessage(Params.DISCONNECT,socket);
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            sendMessage(Params.CONNECT_FAIL,device);
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
    public void  sendMessage(int what,Object obj){
        Message message = new Message();
        message.what = what;
        message.obj = obj;
        handler.sendMessage(message);
    }
    public void dataProcessing(String data){
        try {
            String[] line = data.split("\r\n");
            for (String s : line) {
                if (s.contains("$GNGLL")){
                    String[] dataList = s.split(",");
                    System.out.println(s);
                    GPSObj gpsObj = new GPSObj(dataList);
                    //if (!gpsObj.getStatus().equals("A") && !gpsObj.getModel().startsWith("N")){
                        sendMessage(Params.GET_LOCATION,gpsObj);
                    //}0
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}