package com.example.myapplication.utils.BluetoothTool;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
@RequiresApi(api = Build.VERSION_CODES.N)
public class BluetoothTool {
    private BluetoothAdapter bluetoothAdapter;//蓝牙适配器
    private MyBtReceiver btReceiver;//广播处理器
    private Activity activity;//activity
    private ServerThread serverThread;//socket服务线程
    private ClientThread clientThread;//socket客户端线程
    private Set<BluetoothDevice> deviceSet = new HashSet<>();//搜素到的目标设备列表
    private Set<BluetoothSocket> connectedDevice = new HashSet<>();
    private IntentFilter intentFilter;//设置监听事件
    private String target = Params.TAGET;//目标设备名称
    private Handler handler;
    private BluetoothCallback callback;
    private Consumer<Set<BluetoothDevice>> searchCompletedCallback;
    public BluetoothTool(Activity activity,BluetoothCallback callback){
        this.callback = callback;
        this.activity = activity;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.btReceiver = new MyBtReceiver();
        createHandler();
        checkAlreadyBounded();
    }
    public void openBluetooth(){
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent,Params.REQUEST_ENABLE_BT);
        }
    }
    public void createHandler(){
        handler = new Handler(x->{
            switch (x.what){
                case Params.CONNECT_SUCCESS:{
                    BluetoothSocket socket = (BluetoothSocket)x.obj;
                    connectedDevice.add(socket);
                    callback.connectSuccess(socket);
                    break;
                }
                case Params.CONNECT_FAIL:
                    BluetoothDevice device = (BluetoothDevice)x.obj;
                    callback.connectFail(device);
                    break;
                case Params.DISCONNECT:
                    BluetoothSocket socket = (BluetoothSocket)x.obj;
                    connectedDevice.remove(socket);
                    callback.disconnect(socket);
                    break;
                case Params.GET_LOCATION:
                    GPSObj localtion = (GPSObj) x.obj;
                    callback.getLocation(localtion);
                    break;

                case Params.GET_BATTERY:
                    int battery = (int) x.obj;
                    callback.getBattery(battery);
                    break;

            }
            return false;
        });
    }
    public void checkAlreadyBounded() {
        //获取已经配对的集合
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().contains(target)){
                    System.out.println("已添加"+device.getName());
                    deviceSet.add(device);
                }
            }
        }
    }
    public Set<BluetoothDevice> getBoundedSet(){
        return bluetoothAdapter.getBondedDevices();
    }
    public void connectDevice(BluetoothDevice device){
        ClientThread clientThread = new ClientThread(bluetoothAdapter, device,handler);
        new Thread(clientThread).start();
    }
    public void connectAllDevice(){
        if (serverThread!=null){
            serverThread.cancel();
        }
        for (BluetoothDevice device : deviceSet) {
            connectDevice(device);
        }
    }
    public void startService(){
        if (serverThread!=null){
            serverThread.cancel();
        }
        serverThread = new ServerThread(bluetoothAdapter);
        new Thread(new ServerThread(bluetoothAdapter)).start();
    }
    public void startSearch(){
        this.searchCompletedCallback = searchCompletedCallback;
        intentFilter = new IntentFilter();
        btReceiver = new MyBtReceiver();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        activity.registerReceiver(btReceiver,intentFilter);
        bluetoothAdapter.startDiscovery();
    }
    public void boundDevice(BluetoothDevice device){
        device.createBond();
    }
    private class MyBtReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                System.out.println("开始搜索");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                System.out.println("搜索结束");
                callback.searchCompleted(deviceSet);
                bluetoothAdapter.cancelDiscovery();
                activity.unregisterReceiver( btReceiver);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                System.out.println(device.getName()+" "+ device.getAddress()+" "+device.getUuids());
                if (device.getName()!=null && device.getName().contains(target)){
                    deviceSet.add(device);
                }
            }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.contains(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                    connectDevice(device);
                }
            }

        }
    }
    public void write(BluetoothSocket socket,String data){
        try {
            socket.getOutputStream().write(data.getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Set<BluetoothDevice> getDeviceSet() {
        return deviceSet;
    }

    public Set<BluetoothSocket> getConnectedDevice() {
        return connectedDevice;
    }
}
