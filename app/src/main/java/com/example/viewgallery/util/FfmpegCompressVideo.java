package com.example.viewgallery.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.viewgallery.activity.VideoRecoderActivity;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;

public class FfmpegCompressVideo {

    private static String filePath;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String executeCompressCommand(Activity activity, Uri path, FFmpeg fFmpeg, ProgressDialog progressDialog) {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM
        );

        String filePrefix = "compress_video";
        String fileExtn = ".mp4";
        String yourRealPath = FilePath.getPath(activity, path);


        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }

        filePath = dest.getAbsolutePath();
        String[] complexCommand = {"-y", "-i", yourRealPath, "-s", "640x480", "-r", "60", "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePath};
        //execFFmpegBinary(complexCommand,fFmpeg,progressDialog);

        return filePath;
    }

    private static void execFFmpegBinary(final String[] command, FFmpeg fFmpeg, final ProgressDialog progressDialog) {
        try {
            fFmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {

                }

                @Override
                public void onSuccess(String s) {

                }

                @Override
                public void onProgress(String s) {

                    progressDialog.setMessage("Please wait... We are compressing your video");
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
            // do nothing for now
        }
    }

}
