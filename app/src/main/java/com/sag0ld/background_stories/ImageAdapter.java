package com.sag0ld.background_stories;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.Vector;

/**
 * Created by Sagold on 2017-01-27.
 */

public class ImageAdapter extends BaseAdapter {

    private Context m_context;
    private Vector<Bitmap> m_wallpapers;
    public ImageAdapter(Context p_context, Vector<Bitmap> p_wallpapers) {
            m_context = p_context;
            m_wallpapers = p_wallpapers;
    }

    @Override
    public int getCount() {
        return m_wallpapers.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(m_context);
            imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(m_wallpapers.elementAt(position));
        return imageView;
    }
}
