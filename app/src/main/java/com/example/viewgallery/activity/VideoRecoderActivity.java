package com.example.viewgallery.activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.viewgallery.R;
import com.example.viewgallery.compress.MediaController;
import com.example.viewgallery.util.FilePath;
import com.example.viewgallery.util.VideoDuration;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import java.io.File;
import java.net.URISyntaxException;


public class VideoRecoderActivity extends AppCompatActivity {


     ImageView imageView;
     TextView textView;
     RelativeLayout relativeLayout;
     Uri uri;
    public String filePath;
    private FFmpeg ffmpeg;
    ProgressDialog progressDialog;
    String yourRealPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_recoder);
        imageView = findViewById(R.id.videoThumbnailIVID);
        textView = findViewById(R.id.durationTVID);
        relativeLayout = findViewById(R.id.imageThumLayoutID);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(null);
        progressDialog.setCancelable(false);
        //loadFFMpegBinary();






    }

    public void captureVideo(View view) {
        Intent captureVideoIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
        captureVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
        captureVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(captureVideoIntent, 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK){
            if (data != null){
                File moviesDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                );

                String filePrefix = "compress_video";
                String fileExtn = ".mp4";
                 yourRealPath = FilePath.getPath(this, data.getData());


                File dest = new File(moviesDir, filePrefix + fileExtn);
                int fileNo = 0;
                while (dest.exists()) {
                    fileNo++;
                    dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
                }

                filePath = dest.getAbsolutePath();
                relativeLayout.setVisibility(View.VISIBLE);
                uri = data.getData();
                Glide.with(this).load(getImageThumbnail(this,uri)).placeholder(R.drawable.test).into(imageView);
                textView.setText(VideoDuration.convertMillieToHMmSs(VideoDuration.getDuration(this,uri)));
                progressDialog.setMessage("Please wait");
                progressDialog.show();
                new VideoCompressor().execute();
               // filePath = CompressVideo.executeCompressCommand(this,uri,ffmpeg,progressDialog);
            }

        }
    }

    public String getImageThumbnail(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void playVideo(View view) {

        startActivity(new Intent(VideoRecoderActivity.this,ViewVideoActivity.class).putExtra("videoUri",filePath));
    }

    private class VideoCompressor extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return MediaController.getInstance().convertVideo(yourRealPath);
        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            if (compressed) {
                filePath = MediaController.cachedFile.getPath();
                progressDialog.dismiss();
                Log.e("Compression", "Compression successfully!");
                Log.e("Compressed File Path", "" + MediaController.cachedFile.getPath());

            }
        }
    }







}
