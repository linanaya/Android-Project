package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btn_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_map = findViewById(R.id.btn_map);
        Button btnBle = findViewById(R.id.btn_ble);
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,mapActivity.class);
                startActivity(intent);
            }
        });
        btnBle.setOnClickListener(view->{
            Intent intent =new Intent(MainActivity.this,BleActivity.class);
            startActivity(intent);
        });
    }
}