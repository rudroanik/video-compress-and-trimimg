package com.example.viewgallery.activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.viewgallery.R;
import com.example.viewgallery.util.FilePath;
import com.example.viewgallery.util.VideoDuration;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import life.knowledge4.videotrimmer.utils.FileUtils;

public class VideoFromGalleryActivity extends AppCompatActivity {

    private VideoView videoView;
    private Uri uri;
    private Button trimButton;
    boolean isLessThenTenSecond = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_from_gallery);
        videoView = findViewById(R.id.GalleryViewVideoID);
        trimButton = findViewById(R.id.trimBtnID);

        try {
            Intent intent = new Intent();
            intent.setTypeAndNormalize("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent, "Select Video"), 2);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK) {
            if (data != null) {
                uri = data.getData();

                videoView.setVideoURI(data.getData());
                videoView.start();

                if (VideoDuration.getDuration(VideoFromGalleryActivity.this, data.getData()) > 10) {

                    isLessThenTenSecond = false;

                } else {

                    trimButton.setVisibility(View.VISIBLE);
                    isLessThenTenSecond = true;
                    trimButton.setText("Upload Video");


                }
            }
        }

    }

    public void trimCompressVideo(View view) {
        if (isLessThenTenSecond) {
            Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show();

        } else {
            startActivity(new Intent(VideoFromGalleryActivity.this, TrimActivity.class).putExtra("video", FileUtils.getPath(this, uri)));

        }

    }

}
