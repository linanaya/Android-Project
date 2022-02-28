package com.example.myapplication.utils.BluetoothTool;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Administrator on 2017/4/4.
 */

public class ServerThread implements Runnable {
    BluetoothAdapter bluetoothAdapter;
    BluetoothServerSocket serverSocket =null;
    BluetoothSocket socket = null;
    OutputStream out;
    InputStream in;
    BufferedReader reader;

    boolean acceptFlag = true;
    Activity activity;
    public ServerThread(BluetoothAdapter bluetoothAdapter,Activity activity) {
        this.activity = activity;
        this.bluetoothAdapter = bluetoothAdapter;
        BluetoothServerSocket tmp = null;
        try {
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(Params.NAME, UUID.fromString(Params.UUID));
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverSocket = tmp;
    }

    @Override
    public void run() {
        System.out.println("服务开启成功");
        try {
            while (acceptFlag) {
                socket = serverSocket.accept();
                // 阻塞，直到有客户端连接
                if (socket != null) {
                    Toast.makeText(activity.getApplication(), "服务器被连接成功", Toast.LENGTH_LONG).show();
                    out = socket.getOutputStream();
                    in = socket.getInputStream();
                    BluetoothDevice remoteDevice = socket.getRemoteDevice();
                    // 读取服务器 socket 数据
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            byte[] buffer = new byte[1024];
                            int len;
                            String content;
                            try {
                                while ((len = in.read(buffer)) != -1) {
                                    content = new String(buffer, 0, len);
                                    System.out.println(remoteDevice.getName()+":"+content);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity.getApplication(), "开始发送数据", Toast.LENGTH_LONG).show();
                            for (int i = 0; i < 100; i++) {
                                try {
                                    Thread.sleep(10000);
                                    write("成功发送数据"+i);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    break;
                }
            }// end while(true)
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void write(String data){
        try {
            out.write(data.getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        try {
            acceptFlag = false;
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
