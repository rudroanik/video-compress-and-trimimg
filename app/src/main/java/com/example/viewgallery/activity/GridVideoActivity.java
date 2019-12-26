package com.example.viewgallery.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.viewgallery.R;
import com.example.viewgallery.adapter.GridVideoAdapter;
import com.example.viewgallery.compress.MediaController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GridVideoActivity extends AppCompatActivity {

    private GridView galleryVideo;
    private ArrayList<String> videos;
    private ArrayList<Bitmap> imges;
    GridVideoAdapter adapter;
    Bitmap thumb;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_video);
        galleryVideo = findViewById(R.id.gridViewID);
        progressBar = findViewById(R.id.progressBarId);
        imges = new ArrayList<>();
        videos = new ArrayList<>();
        videos = getAllMedia();
        new VideoCompressor().execute();
        galleryVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(GridVideoActivity.this, videos.get(position), Toast.LENGTH_SHORT).show();

            }
        });

    }

    public ArrayList<String> getAllMedia() {
        HashSet<String> videoItemHashSet = new HashSet<>();
        String[] projection = { MediaStore.Video.VideoColumns.DATA ,MediaStore.Video.Media.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        try {
            cursor.moveToFirst();
            do{
                videoItemHashSet.add((cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))));
            }while(cursor.moveToNext());

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<String> downloadedList = new ArrayList<>(videoItemHashSet);
        return downloadedList;
    }

    private class VideoCompressor extends AsyncTask<Void, Void, List<Bitmap>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Bitmap> doInBackground(Void... voids) {
            for (String s: videos){

                thumb = ThumbnailUtils.createVideoThumbnail(s, MediaStore.Video.Thumbnails.MICRO_KIND);

            }
            imges.add(thumb);
            return imges;
        }

        @Override
        protected void onPostExecute(List<Bitmap> compressed) {
            super.onPostExecute(compressed);
            progressBar.setVisibility(View.GONE);
            galleryVideo.setVisibility(View.VISIBLE);
            adapter = new GridVideoAdapter(GridVideoActivity.this, videos,imges);
            galleryVideo.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}
