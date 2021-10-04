package com.jiangdg.usbcamera.view;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jiangdg.usbcamera.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConfirmActivity extends AppCompatActivity {
    public String imgURL;
    public String JSONData;
    public Bitmap myBitmap;
    private ProgressBar spinner;
    public String responseJSON = "";
    public String responseJSONreturned = "";
    public SharedPreferences sharedPref;
    public String ipv4Address;
    public String portNumber;
    public Boolean resultReturned;
    public int previousCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //This strict mode needs to be changed by handling http calls on async methods
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_confirm);
        Intent intent = getIntent();
        imgURL = intent.getStringExtra("image_URL");
        File imgFile = new File(imgURL);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        sharedPref= getSharedPreferences("networkSettings", 0);
        ipv4Address = sharedPref.getString("ipConfig", "");
        portNumber = sharedPref.getString("port", "");

        sharedPref = getSharedPreferences("sessionDetails", 0);
        JSONData = sharedPref.getString("sessionDetailJSON", "");

        sharedPref = getSharedPreferences("countSession", 0);
        previousCount = sharedPref.getInt("totalCount", -10001);


        resultReturned = false;
        if(imgFile.exists()){

            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ImageView myImage = (ImageView) findViewById(R.id.cameraPhoto);

            myImage.setImageBitmap(myBitmap);

            connectServer();

        }

    }
    void connectServer(){
        spinner.setVisibility(View.VISIBLE);

        String postUrl= "http://"+ipv4Address+":"+portNumber+"/uploadimage/";

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // Read BitMap by file path

        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String img_name = "IMG_"+ timeStamp +".jpg";

        RequestBody postBodyImage = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", img_name, RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
                .addFormDataPart("meta", JSONData)
                .build();



        TextView responseText = findViewById(R.id.responseText);
        responseText.setText(postUrl);

        postRequestImage(postUrl, postBodyImage);
    }
    void postRequestImage(String postUrl, RequestBody postBody) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.responseText);
                        responseText.setText(e.toString());
                        spinner.setVisibility(View.GONE);
                        e.printStackTrace();

                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.responseText);
                        try {

                            responseJSON = response.body().string();
                            responseText.setText("Connection Established!");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        spinner.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    void postRequestMeta(String postUrl, RequestBody postBody, boolean getResult) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.responseText);
                        responseText.setText(e.toString());
                        spinner.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.responseText);
                        try {
                            String responseLocal = response.body().string();
                            JSONObject returnMeta = new JSONObject(responseLocal);
                            if(!getResult)
                                if (returnMeta.getBoolean("created")) {

                                        responseText.setText("Process request created");

                                } else {
                                    responseText.setText("Process creation did not work");
                                }
                            else
                                if (returnMeta.getString("status").equals("processing"))
                                    responseText.setText(
                                            "Still processing. Current line in queue is " + String.valueOf(returnMeta.getJSONObject("data").getInt("position"))
                                                    + " out of a total of " + String.valueOf(returnMeta.getJSONObject("data").getInt("total")));
                                else {
                                    if (!resultReturned) {
                                        resultReturned = true;
                                        int returnedCount = returnMeta.getJSONObject("data").getJSONArray("annotations").length();

                                        SharedPreferences.Editor editor= sharedPref.edit();
                                        editor.putInt("totalCount", previousCount + returnedCount);
                                        editor.commit();

                                        String message = "Image processing complete! The total number of parts detected the current image is :"
                                                + String.valueOf(returnedCount)
                                                + ". Do you want to view total count (Or go back to the camera page)?";
                                        new AlertDialog.Builder(ConfirmActivity.this)
                                                .setTitle("Parts Counting Result")
                                                .setMessage(message)

                                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                                // The dialog is automatically dismissed when a dialog button is clicked.
                                                .setPositiveButton("Yes, Total count", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent loadCameraPage = new Intent(ConfirmActivity.this, CountTotal.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        Handler mHandler = new Handler();
                                                        Toast.makeText(ConfirmActivity.this, "Loading results page...",Toast.LENGTH_SHORT).show();
                                                        mHandler.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                startActivity(loadCameraPage);
                                                            }
                                                        }, 400);
                                                        ConfirmActivity.this.finish();
                                                    }
                                                })

                                                // A null listener allows the button to dismiss the dialog and take no further action.
                                                .setNegativeButton("No, Back to Camera", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        ConfirmActivity.this.finish();
                                                    }
                                                })
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                        resultReturned = true;
                                    }
                                    spinner.setVisibility(View.GONE);
                                }
                        } catch (IOException | JSONException e ) {
                            responseText.setText( e.toString());
                        }

                    }
                });
            }
        });
    }

    public void confirmYes(View view) throws InterruptedException {
        uploadMeta();
        spinner.setVisibility(View.VISIBLE);
        Handler mHandler = new Handler();
        if (!resultReturned)
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    getMeta();
                    mHandler.postDelayed(this,2000);
                }
            }, 2000);
    }

    public void confirmNo(View view) {
        this.finish();
    }

    public void uploadMeta() {
        spinner.setVisibility(View.VISIBLE);
        String postUrl= "http://"+ipv4Address+":"+portNumber+"/create/request";
        RequestBody postBodyImage = RequestBody.create(
                MediaType.parse("application/json"), responseJSON);


        postRequestMeta(postUrl, postBodyImage, false);
    }

    public void getMeta() {
        String postUrl= "http://"+ipv4Address+":"+portNumber+"/status/request";
        RequestBody postBodyImage = RequestBody.create(
                MediaType.parse("application/json"), responseJSON);


        postRequestMeta(postUrl, postBodyImage, true);
    }

}