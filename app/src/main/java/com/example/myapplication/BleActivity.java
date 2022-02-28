package com.example.myapplication;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.clj.fastble.BleManager;
import com.example.myapplication.utils.BluetoothTool.BluetoothTool;

public class BleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        Application application = this.getApplication();
        BleManager.getInstance().init(getApplication());
        Button btnConnect = (Button) findViewById(R.id.startConnect);
        Button check = findViewById(R.id.check);
        BluetoothTool bluetoothTool = new BluetoothTool(this,findViewById(R.id.textView));
        btnConnect.setOnClickListener(view->{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};
                for (String str : permissions) {
                    if (checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(permissions, 111);
                        break;
                    }
                }
            }
            bluetoothTool.startSearch();
            bluetoothTool.openBluetooth();
        });
        check.setOnClickListener(x->{
            System.out.println(bluetoothTool.checkAlreadyConnect());
            System.out.println(bluetoothTool.getDeviceSet());
        });

    }
}
