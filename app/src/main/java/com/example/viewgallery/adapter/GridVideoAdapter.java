package com.example.viewgallery.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.viewgallery.R;
import com.example.viewgallery.util.VideoDuration;

import java.util.ArrayList;
import java.util.List;

public class GridVideoAdapter extends BaseAdapter {

    private Activity context;
    private ArrayList<String> images;
    private ArrayList<Bitmap> image;

    public GridVideoAdapter(Activity localContext, ArrayList<String> images, ArrayList<Bitmap> image) {
        context = localContext;
        this.images = images;
        this.image = image;

    }

    public int getCount() {
        return images.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView,
                        ViewGroup parent) {

        String image = images.get(position);
        ImageView imageView;


        View view = LayoutInflater.from(context).inflate(R.layout.item_grid_video, parent, false);

        imageView = view.findViewById(R.id.videoThumbnailIVID);


        Glide.with(context).load(image)
                .placeholder(R.drawable.no_video).centerCrop()
                .into(imageView);


        return view;
    }

}
