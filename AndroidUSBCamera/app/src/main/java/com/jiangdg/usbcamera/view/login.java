package com.jiangdg.usbcamera.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.jiangdg.usbcamera.R;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class login extends AppCompatActivity {
    @BindView(R.id.dynamic_part_spinner_login)
    public Spinner dynamicSpinnerlogin;
    @BindView(R.id.editTextTextName)
    public EditText name;
    @BindView(R.id.editTextBadgeID)
    public EditText badgeID;

    protected String numRegex   = "[0-9]*";
    protected String alphaRegex = "[A-Za-z]*";
    protected String alphaNumericRegex = "[A-Za-z0-9]*";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        String[] items = new String[] { "H","Short Screw", "Long Screw", "Bolt"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);

        dynamicSpinnerlogin.setAdapter(adapter);
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        badgeID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    public void CreateSession(View view) {
        Context context = getApplicationContext();
        String nameStr = name.getText().toString();
        String badgeIDStr = badgeID.getText().toString();
        if (!nameStr.matches(alphaRegex)){
            Toast.makeText(context, "Name has invalid characters! Enter first name only", Toast.LENGTH_SHORT).show();
        } else if (!badgeIDStr.matches(alphaNumericRegex)){
            Toast.makeText(context, "Badge ID has invalid characters! Alphanumeric characters only", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Session creation succesful! Loading...", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent loadCameraPage = new Intent(login.this, USBCameraActivity.class);
                    JSONObject sessionDetails = new JSONObject();
                    String partID =  dynamicSpinnerlogin.getSelectedItem().toString();
                    try {
                        //sessionDetails.put("name", nameStr);
                        sessionDetails.put("timestamp", String.valueOf(System.currentTimeMillis()));
                        sessionDetails.put("session", badgeIDStr);
                        sessionDetails.put("part",partID);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    loadCameraPage.putExtra("Session_details", sessionDetails.toString());
                    loadCameraPage.putExtra("badgeID", badgeIDStr);
                    loadCameraPage.putExtra("partID", partID);
                    startActivity(loadCameraPage);
                    login.this.finish();
                }
            }, 2000);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
}