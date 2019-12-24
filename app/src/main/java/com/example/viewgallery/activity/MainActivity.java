package com.example.viewgallery.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.viewgallery.R;
import com.example.viewgallery.adapter.GridVideoAdapter;
import com.example.viewgallery.util.ReadExternalStoragePermission;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ReadExternalStoragePermission.isReadStoragePermissionGranted(this);
    }

    public void openGallery(View view) {
        startActivity(new Intent(MainActivity.this, VideoFromGalleryActivity.class));

    }

    public void captureVideo1(View view) {
        if (ReadExternalStoragePermission.isReadStoragePermissionGranted(this)){
            startActivity(new Intent(MainActivity.this, VideoRecoderActivity.class));
        }

    }
}
