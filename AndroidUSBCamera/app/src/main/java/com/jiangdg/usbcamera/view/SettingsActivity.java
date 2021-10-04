package com.jiangdg.usbcamera.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.jiangdg.usbcamera.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {
    @BindView(R.id.ipConfig)
    EditText ipConfigEditText;
    @BindView(R.id.port)
    EditText portEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        ButterKnife.bind(this);
        SharedPreferences sharedPref= getSharedPreferences("networkSettings", 0);
        ipConfigEditText.setText(sharedPref.getString("ipConfig", ""));
        portEditText.setText(sharedPref.getString("port", ""));

    }

    public void SaveSettings(View view) {
        String IPConfig = ipConfigEditText.getText().toString();
        String Port = portEditText.getText().toString();
        SharedPreferences sharedPref= getSharedPreferences("networkSettings", 0);
        SharedPreferences.Editor editor= sharedPref.edit();
        editor.putString("ipConfig", IPConfig);
        editor.putString("port", Port);
        editor.commit();
        Toast.makeText(SettingsActivity.this, "Saved Network Settings",Toast.LENGTH_SHORT).show();
        Handler mHandler = new Handler();
        Toast.makeText(SettingsActivity.this, "Returning to Camera View Page...",Toast.LENGTH_SHORT).show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SettingsActivity.this.finish();
            }
        }, 400);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}