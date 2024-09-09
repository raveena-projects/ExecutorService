package com.example.executorserviceapp;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private Button btn_download;
    private EditText urlEditText;
    private ImageView imageView;
    private ExecutorService executorSerivce;
    private Handler mainHandler;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_download = findViewById(R.id.btn_download);
        urlEditText = findViewById(R.id.et_image_url);
        imageView = findViewById(R.id.iv_image);
        progressBar = findViewById(R.id.pb_loader);

        executorSerivce = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        progressBar.setVisibility(View.GONE);
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlText = urlEditText.getText().toString();
                if(!urlText.isEmpty()){
                    progressBar.setVisibility(View.VISIBLE);
                    executorSerivce.submit(new ImageDownloadTask(urlText));
                } else{
                    Toast.makeText(getApplicationContext(),"Invalid url",Toast.LENGTH_SHORT);
                }
            }
        });
    }
    private class ImageDownloadTask implements Runnable{
        private final String imageUrl;
        ImageDownloadTask(String imageUrl){
            this.imageUrl = imageUrl;
        }
        @Override
        public void run() {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(imageUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap finalBitmap = bitmap;
                mainHandler.post(()->{
                    imageView.setImageBitmap(finalBitmap);
                    imageView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}