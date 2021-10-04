package com.jiangdg.usbcamera.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jiangdg.usbcamera.R;


public class CountTotal extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.total_count);
        SharedPreferences sharedPref= getSharedPreferences("countSession", 0);
        TextView responseText = findViewById(R.id.totalCountValue);
        responseText.setText(String.valueOf(sharedPref.getInt("totalCount", -20001)));
    }

    public void usbPageLoad(View v){
        Intent loadCameraPage = new Intent(CountTotal.this, USBCameraActivity.class);
        Handler mHandler = new Handler();
        Toast.makeText(CountTotal.this, "Returning to camera view...",Toast.LENGTH_SHORT).show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(loadCameraPage);
            }
        }, 1000);
        CountTotal.this.finish();
    }

    public void sessionStartLoad(View v){
        Intent loadCameraPage = new Intent(CountTotal.this, login.class);
        Handler mHandler = new Handler();
        Toast.makeText(CountTotal.this, "Saving image...",Toast.LENGTH_SHORT).show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(loadCameraPage);
            }
        }, 1000);
        CountTotal.this.finish();
    }

}
