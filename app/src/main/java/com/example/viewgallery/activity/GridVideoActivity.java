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

    GridVideoAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_video);
        galleryVideo = findViewById(R.id.gridViewID);
        videos = new ArrayList<>();
        videos = getAllMedia();
        adapter = new GridVideoAdapter(GridVideoActivity.this, videos);
        galleryVideo.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        galleryVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videos.get(position)));
                intent.setDataAndType(Uri.parse(videos.get(position)), "video/mp4");
                startActivity(intent);

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

}
