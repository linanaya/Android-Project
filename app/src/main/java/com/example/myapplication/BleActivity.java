package com.example.myapplication;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.clj.fastble.BleManager;
import com.example.myapplication.utils.BleTool;

public class BleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        Application application = this.getApplication();
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setOperateTimeout(5000);
        Button btnConnect = (Button) findViewById(R.id.startConnect);
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
            BleTool bleTool = new BleTool(application,"Water");
            bleTool.scan();
        });

    }
}
