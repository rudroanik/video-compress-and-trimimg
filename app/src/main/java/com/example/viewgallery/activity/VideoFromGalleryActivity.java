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

public class VideoFromGalleryActivity extends AppCompatActivity {

    private VideoView videoView;
    private TextView endTimeTV,startTimeTV;
    private RangeSeekBar<Integer> rangeSeekBar;
    private int selectedMinValue,selectedMaxValue;
    private Uri uri;
    private FFmpeg ffmpeg;
    private String filePath;
    private ProgressDialog progressDialog;
    private Button trimButton;
    boolean isLessThenTenSecond= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_from_gallery);
        videoView = findViewById(R.id.GalleryViewVideoID);
        startTimeTV = findViewById(R.id.startTimeTVID);
        endTimeTV = findViewById(R.id.endTimeTVID);
        rangeSeekBar = findViewById(R.id.rangBarID);
        trimButton = findViewById(R.id.trimBtnID);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        loadFFMpegBinary();

        try {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Video"), 2);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==2 && resultCode == RESULT_OK){
            if (data != null){
                uri = data.getData();

                videoView.setVideoURI(data.getData());
                videoView.start();

                if (VideoDuration.getDuration(VideoFromGalleryActivity.this,data.getData())> 10){

                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            endTimeTV.setText(getTime(mp.getDuration() / 1000));
                            rangeSeekBar.setRangeValues(0, mp.getDuration() / 1000);
                            rangeSeekBar.setSelectedMinValue(0);
                            rangeSeekBar.setSelectedMaxValue(mp.getDuration() / 1000);

                            rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
                                @Override
                                public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Integer minValue, Integer maxValue) {
                                    videoView.seekTo((int) minValue * 1000);

                                    startTimeTV.setText(getTime((int) bar.getSelectedMinValue()));

                                    endTimeTV.setText(getTime((int) bar.getSelectedMaxValue()));
                                    selectedMinValue = rangeSeekBar.getSelectedMinValue() * 1000;
                                    selectedMaxValue = rangeSeekBar.getSelectedMaxValue() * 1000;
                                }
                            });


                        }
                    });


                }else {

                    rangeSeekBar.setVisibility(View.GONE);
                    startTimeTV.setVisibility(View.GONE);
                    endTimeTV.setVisibility(View.GONE);
                    trimButton.setVisibility(View.VISIBLE);
                    isLessThenTenSecond = true;
                    trimButton.setText("Upload Video");


                }
            }
        }

    }

    public void trimCompressVideo(View view) {
        if (isLessThenTenSecond){
            Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show();

        }else {
            extractImagesVideo(rangeSeekBar.getSelectedMinValue() * 1000, rangeSeekBar.getSelectedMaxValue() * 1000);

        }



    }

    private void extractImagesVideo(int selectedMinValue, int selectedMaxValue) {File moviesDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM
    );

        String filePrefix = "cut_video";
        String fileExtn = ".mp4";
        String yourRealPath = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            yourRealPath = FilePath.getPath(VideoFromGalleryActivity.this, uri);
        }
        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }

        filePath = dest.getAbsolutePath();
        String[] complexCommand = {"-ss", "" + selectedMinValue / 1000, "-y", "-i", yourRealPath, "-t", "" + (selectedMaxValue - selectedMinValue) / 1000,"-s", "640x480", "-r", "60","-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePath};

        execFFmpegBinary(complexCommand);
    }

    private void execFFmpegBinary(String[] complexCommand) {
        try {
            ffmpeg.execute(complexCommand, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {

                }

                @Override
                public void onSuccess(String s) {

                }

                @Override
                public void onProgress(String s) {

                    progressDialog.setMessage("Please wait... We are trimming & compressing your video");
                }

                @Override
                public void onStart() {

                    progressDialog.show();
                }

                @Override
                public void onFinish() {


                    progressDialog.dismiss();


                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
        }
    }

    private String getTime(int seconds) {
        int hr = seconds / 3600;
        int rem = seconds % 3600;
        int mn = rem / 60;
        int sec = rem % 60;
        return String.format("%02d", hr) + ":" + String.format("%02d", mn) + ":" + String.format("%02d", sec);
    }
    private void loadFFMpegBinary() {
        try {
            if (ffmpeg == null) {

                ffmpeg = FFmpeg.getInstance(this);
            }
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {

                }

                @Override
                public void onSuccess() {

                }
            });
        } catch (FFmpegNotSupportedException e) {

        } catch (Exception e) {

        }
    }

}
