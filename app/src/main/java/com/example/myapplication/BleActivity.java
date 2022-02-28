package com.example.myapplication;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.clj.fastble.BleManager;
import com.example.myapplication.utils.BluetoothTool.BluetoothTool;

public class BleActivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        Application application = this.getApplication();
        BleManager.getInstance().init(getApplication());
        Button btnConnect = (Button) findViewById(R.id.startSearch);
        Button check = findViewById(R.id.check);
        Button connect = findViewById(R.id.connect);
        TextView text = findViewById(R.id.textView);
        BluetoothTool bluetoothTool = new BluetoothTool(this,text);
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
            bluetoothTool.startService();
        });
        check.setOnClickListener(x->{
            bluetoothTool.getDeviceSet().forEach(y->{
                text.setText(text.getText()+"\n"+y.getName());
            });
        });
        connect.setOnClickListener(x->{
            bluetoothTool.connectDevice();
        });

    }
}
