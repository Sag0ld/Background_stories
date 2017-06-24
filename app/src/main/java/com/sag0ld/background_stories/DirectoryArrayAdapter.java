package com.sag0ld.background_stories;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagold on 2017-01-09.
 */

public class DirectoryArrayAdapter extends ArrayAdapter<File> {
    private Context m_context;
    private List<File> m_files;
    public enum imgExtension { JPG, PNG, JPEG}
    ImageLoader imageLoader;
    DisplayImageOptions options;

    //constructor, call on creation
    public DirectoryArrayAdapter (Context context, int resource, List<File> objects) {
        super(context, resource, objects);

        this.m_context = context;
        this.m_files = objects;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).cacheInMemory(true).considerExifParams(true).build();
    }


    //called when rendering the list
    public View getView(int position, View convertView, ViewGroup parent) {
        final int m_pos = position;

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) m_context.getSystemService
                (Activity.LAYOUT_INFLATER_SERVICE);

        //Init the view holder
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_list, null);
            holder.picture = (ImageView) convertView.findViewById(R.id.imageThumbnail);
            holder.name = (TextView) convertView.findViewById(R.id.txtItemName);
            holder.fontAwesomeIcon = (TextView) convertView.findViewById(R.id.fontAwesomeIconTextView);
            holder.progress = (ProgressBar) convertView.findViewById(R.id.progress_spinner);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        //get the property we are displaying
        final File item = m_files.get(position);

        holder.picture.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.picture.setPadding(8, 8, 8, 8);
        Typeface fontAwesomeFont = Typeface.createFromAsset(m_context.getAssets(),
                                    "fonts/fontawesome-webfont.ttf");
        holder.fontAwesomeIcon.setTypeface(fontAwesomeFont);
        //path.setText(item.getPath());
        holder.name.setText(item.getName());

        //Initilize type and imageView
        if(item.isDirectory()) {
            //type.setText(fileType.Dir.name());
            holder.fontAwesomeIcon.setText(R.string.font_awesome_folder);
            holder.picture.setVisibility(ImageView.GONE);
            holder.progress.setVisibility(ProgressBar.GONE);
        } else {
            String[] separeteditems = item.getName().split("\\.");
            String extension  = separeteditems[separeteditems.length - 1];
            // If it's an image accepted or another file
            if(extension.equalsIgnoreCase(imgExtension.JPEG.toString()) ||
               extension.equalsIgnoreCase(imgExtension.JPG.toString()) ||
               extension.equalsIgnoreCase(imgExtension.PNG.toString())) {

                ImageAware imageAware = new ImageViewAware(holder.picture, false);
                imageLoader.displayImage(item.getAbsolutePath(), imageAware,options);

                holder.fontAwesomeIcon.setVisibility(TextView.GONE);
            } else {
                holder.fontAwesomeIcon.setText(R.string.font_awesome_file);
                holder.picture.setVisibility(ImageView.GONE);
                holder.progress.setVisibility(ProgressBar.GONE);
            }
        }
        return convertView;
    }
}