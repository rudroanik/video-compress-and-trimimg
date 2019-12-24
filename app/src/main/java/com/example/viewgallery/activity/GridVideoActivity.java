package com.example.viewgallery.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.viewgallery.R;
import com.example.viewgallery.adapter.GridVideoAdapter;

import java.util.ArrayList;
import java.util.HashSet;

public class GridVideoActivity extends AppCompatActivity {

    private GridView galleryVideo;
    private ArrayList<String> videos;
    private ArrayList<Bitmap> imges;
    GridVideoAdapter adapter;
    Bitmap thumb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_video);
        galleryVideo = findViewById(R.id.gridViewID);
        imges = new ArrayList<>();
        videos = new ArrayList<>();
        videos = getAllMedia();
        for (String s: videos){
            thumb = ThumbnailUtils.createVideoThumbnail(s, MediaStore.Video.Thumbnails.MICRO_KIND);

        }
        imges.add(thumb);
        adapter = new GridVideoAdapter(this, videos,imges);
        galleryVideo.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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
}
