package com.example.viewgallery.activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.viewgallery.R;
import com.example.viewgallery.compress.MediaController;

import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;


public class TrimActivity extends AppCompatActivity implements OnTrimVideoListener {

    K4LVideoTrimmer trimmer;
    Uri trimmedUri;
    ProgressDialog progressDialog;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim);
        trimmer = findViewById(R.id.trimID);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Compressing video");
        if(trimmer != null){
            trimmer.setMaxDuration(15);
            trimmer.setOnTrimVideoListener(this);
            trimmer.setVideoURI(Uri.parse(getIntent().getStringExtra("video")));

        }

    }


    @Override
    public void getResult(final Uri uri) {
        
        trimmedUri = uri;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
            }
        });
        new VideoCompressor().execute();

    }

    @Override
    public void cancelAction() {
        trimmer.destroy();
        finish();
    }

    private class VideoCompressor extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return MediaController.getInstance().convertVideo(trimmedUri.getPath());
        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            if (compressed) {
                progressDialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MediaController.cachedFile.getPath()));
                intent.setDataAndType(Uri.parse(MediaController.cachedFile.getPath()), "video/mp4");
                startActivity(intent);
                finish();
            }
        }
    }
}
