package com.jiangdg.usbcamera.view;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jiangdg.usbcamera.R;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

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
    String ipv4Address = "192.168.137.253";
    String portNumber = "5002";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        Intent intent = getIntent();
        imgURL = intent.getStringExtra("image_URL");
        JSONData = intent.getStringExtra("Session_details");
        File imgFile = new  File(imgURL);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        if(imgFile.exists()){

            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ImageView myImage = (ImageView) findViewById(R.id.cameraPhoto);

            myImage.setImageBitmap(myBitmap);

            connectServer();

        }

    }
    void connectServer(){
        spinner.setVisibility(View.VISIBLE);

        String postUrl= "http://"+ipv4Address+":"+portNumber+"/uploadimage";

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
        responseText.setText("Please wait ...");

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
                        responseText.setText("Failed to Connect to Server");
                        spinner.setVisibility(View.GONE);
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

    void postRequestMeta(String postUrl, RequestBody postBody) {

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
                        responseText.setText("Failed to Connect to Server");
                        spinner.setVisibility(View.GONE);
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
                            responseText.setText(responseLocal);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        spinner.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    public void confirmYes(View view) throws InterruptedException {
        uploadMeta();
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getMeta();
            }
        }, 3000);
    }

    public void confirmNo(View view) {
        setContentView(R.layout.activity_usbcamera);
        this.finish();
    }

    public void uploadMeta() {
        spinner.setVisibility(View.VISIBLE);
        String postUrl= "http://"+ipv4Address+":"+portNumber+"/create/request";
        RequestBody postBodyImage = RequestBody.create(
                MediaType.parse("application/json"), responseJSON);


        postRequestMeta(postUrl, postBodyImage);
    }

    public void getMeta() {
        spinner.setVisibility(View.VISIBLE);


        String postUrl= "http://"+ipv4Address+":"+portNumber+"/status/request";
        RequestBody postBodyImage = RequestBody.create(
                MediaType.parse("application/json"), responseJSON);


        postRequestMeta(postUrl, postBodyImage);
    }

}