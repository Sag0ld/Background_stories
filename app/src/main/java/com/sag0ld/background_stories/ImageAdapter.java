package com.sag0ld.background_stories;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Created by Sagold on 2017-01-27.
 */

public class ImageAdapter extends BaseAdapter {

    private Context m_context;
    private List<String> m_pictureFounds = new ArrayList<>() ;
    public ImageAdapter(Context p_context, Set<String> p_pictureFounds) {
            m_context = p_context;
        Iterator itr = p_pictureFounds.iterator();
        while(itr.hasNext()) {
            m_pictureFounds.add(itr.next().toString());
        }
    }

    @Override
    public int getCount() {
        return m_pictureFounds.size();
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
        Glide
                .with(imageView)
                .load(m_pictureFounds.get(position))
                .into(imageView);
        return imageView;
    }
}
