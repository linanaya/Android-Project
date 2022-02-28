package com.example.myapplication.utils.BluetoothTool;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BluetoothTool {
    private BluetoothAdapter bluetoothAdapter;
    private MyBtReceiver btReceiver;
    private Activity activity;
    ServerThread serverThread;
    ClientThread clientThread;
    Set<BluetoothDevice> deviceSet = new HashSet<>();
    IntentFilter intentFilter;
    TextView textView;
    public BluetoothTool(Activity activity,TextView textView){
        this.activity = activity;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btReceiver = new MyBtReceiver();
    }
    public void openBluetooth(){
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent,Params.REQUEST_ENABLE_BT);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<BluetoothDevice> checkAlreadyConnect() {
        List<BluetoothDevice> list = new ArrayList<BluetoothDevice>();
        boolean flag = false;
        //获取已经配对的集合
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                list.add(device);
                deviceSet.add(device);
            }
        }
        return list;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void connectDevice(){
        if (serverThread!=null){
            serverThread.cancel();
        }
        deviceSet.forEach(x->{
            for (ParcelUuid uuid : x.getUuids()) {
                System.out.println("要连接的设备uuid为"+uuid.toString());
            }
            ClientThread clientThread = new ClientThread(bluetoothAdapter, x);
            new Thread(clientThread).start();
        });
    }
    public void startService(){
        if (serverThread!=null){
            serverThread.cancel();
        }
       serverThread = new ServerThread(bluetoothAdapter,activity);
        new Thread(serverThread).start();
    }
    public void startSearch(){
        intentFilter = new IntentFilter();
        btReceiver = new MyBtReceiver();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(btReceiver,intentFilter);
        bluetoothAdapter.startDiscovery();
    }
    private class MyBtReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                System.out.println("开始搜索");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                System.out.println("搜索结束");
                bluetoothAdapter.cancelDiscovery();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                System.out.println(device.getName()+" "+ device.getAddress()+" "+device.getUuids());
                if (device.getName()!=null){
                    deviceSet.add(device);
                }
            }
        }
    }
    public Set<BluetoothDevice> getDeviceSet() {
        return deviceSet;
    }
}
