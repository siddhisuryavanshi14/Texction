package com.example.texction;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.jakewharton.threetenabp.AndroidThreeTen;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {

    private Button btnImage;
    public static ImageView imageView;
    Uri file;
    SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        sharedPreferences=getSharedPreferences(getString(R.string.preference_file),MODE_PRIVATE);
        setContentView(R.layout.activity_camera);

        btnImage = (Button) findViewById(R.id.btnImage);
        imageView = (ImageView) findViewById(R.id.imageView);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            btnImage.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                btnImage.setEnabled(true);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void takePicture(View view) {
        StrictMode.VmPolicy.Builder builder1 = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder1.build());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        //file=Uri.fromFile(new File(file.toString()));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        //sharedPreferences.edit().putString("img",file.toString()).apply();

        startActivityForResult(intent, 100);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "Texction");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Toast.makeText(this, "failed to create directory",Toast.LENGTH_LONG).show();
                return null;
            }
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy '@'HH:mm:ss a");
        LocalDateTime now = LocalDateTime.now();
        String timeStamp = dtf.format(now);
        //sharedPreferences.edit().putString("img","IMG_"+ timeStamp + ".jpg").apply();
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 100) {
                if (resultCode == RESULT_OK) {
                    imageView.setImageURI(file);
                    sharedPreferences.edit().putString("img",file.toString()).apply();
                    Toast.makeText(this, "Image captured", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CameraActivity.this,ExtractActivity.class));
                }
            } else {
                Toast.makeText(this, "You haven't clicked any Image", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}