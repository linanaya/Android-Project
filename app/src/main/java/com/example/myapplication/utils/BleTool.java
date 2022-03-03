package com.example.myapplication.utils;


import android.app.Application;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleIndicateCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class BleTool {
    private UUID uuidService;
    private UUID uuidChara;
    private BluetoothGatt bluetoothGatt;
    private Application application;
    private String deviceName;

    public BleTool(Application application,String deviceName) {
        this.application = application;
        this.deviceName = deviceName;
    }


    public void scan() {
        //初始化

        System.out.println("是否支持低功耗蓝牙" + BleManager.getInstance().isSupportBle());
        System.out.println("是否打开蓝牙" + BleManager.getInstance().isBlueEnable());
        //检查是否打开蓝牙
        if (!BleManager.getInstance().isBlueEnable()) {
            BleManager.getInstance().enableBluetooth();
        }
        //设置扫描规则
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                //.setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
                //.setDeviceName(true, names)         // 只扫描指定广播名的设备，可选
                //.setDeviceMac(mac)                  // 只扫描指定mac的设备，可选
                //.setAutoConnect(isAutoConnect)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒；小于等于0表示不限制扫描时间
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);

        //开始扫描
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                Toast.makeText(application, "扫描结束", Toast.LENGTH_LONG).show();
                AtomicReference<Boolean> flag = new AtomicReference<>(false);
                scanResultList.forEach(x -> {
                    if (!Objects.isNull(x.getName()) && x.getName().contains(deviceName)) {
                        flag.set(true);
                        connect(x);
                    }
                });
                if (!flag.get()){
                    Toast.makeText(application, "未扫描到指定设备", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onScanStarted(boolean success) {
                Toast.makeText(application, "开始寻找蓝牙", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onScanning(BleDevice bleDevice) {

            }
        });
    }

    private void connect(BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                Toast.makeText(application, "开始连接", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                Toast.makeText(application, "连接失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                System.out.println("成功");
                Toast.makeText(application, "连接成功", Toast.LENGTH_LONG).show();
                List<BluetoothGattService> services = gatt.getServices();
                services.forEach(service -> {
                    uuidService = service.getUuid();
                    List<BluetoothGattCharacteristic> characteristicList = service.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : characteristicList) {
                        uuidChara = characteristic.getUuid();
                    }

                });
                System.out.println(uuidService.toString());
                BleManager.getInstance().indicate(
                        bleDevice,
                        uuidService.toString(),
                        uuidChara.toString(),
                        new BleIndicateCallback() {
                            @Override
                            public void onIndicateSuccess() {

                            }

                            @Override
                            public void onIndicateFailure(BleException exception) {

                            }

                            @Override
                            public void onCharacteristicChanged(byte[] data) {
                                ByteArrayInputStream stream = new ByteArrayInputStream(data);
                                System.out.println(stream.read());
                            }
                        }
                );
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                Toast.makeText(application, "连接中断", Toast.LENGTH_LONG).show();
            }
        });
    }
}