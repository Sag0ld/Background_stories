package com.sag0ld.background_stories;

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

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(m_context).inflate(R.layout.item_list, parent, false);
            viewHolder = new ViewHolder();

            // Immediately access to all view component inside the tag of the layout
            viewHolder.picture = (ImageView) convertView.findViewById(R.id.imageThumbnail);
            viewHolder.name = (TextView) convertView.findViewById(R.id.txtItemName);
            viewHolder.fontAwesomeIcon = (TextView) convertView.findViewById(R.id.fontAwesomeIconTextView);
            viewHolder.progress = (ProgressBar) convertView.findViewById(R.id.progress_spinner);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //get the property we are displaying
        final File item = m_files.get(position);

        viewHolder.picture.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewHolder.picture.setPadding(8, 8, 8, 8);
        Typeface fontAwesomeFont = Typeface.createFromAsset(m_context.getAssets(),
                                    "fonts/fontawesome-webfont.ttf");
        viewHolder.fontAwesomeIcon.setTypeface(fontAwesomeFont);
        viewHolder.name.setText(item.getName());

        //Initilize imageView
        if(item.isDirectory()) {
            viewHolder.fontAwesomeIcon.setText(R.string.font_awesome_folder);
            viewHolder.picture.setVisibility(ImageView.GONE);
            viewHolder.progress.setVisibility(ProgressBar.GONE);
        } else {
            String[] separeteditems = item.getName().split("\\.");
            String extension  = separeteditems[separeteditems.length - 1];
            // If it's an image accepted or another file
            if(extension.equalsIgnoreCase(imgExtension.JPEG.toString()) ||
               extension.equalsIgnoreCase(imgExtension.JPG.toString()) ||
               extension.equalsIgnoreCase(imgExtension.PNG.toString())) {

                ImageAware imageAware = new ImageViewAware(viewHolder.picture, false);
                imageLoader.displayImage(item.getAbsolutePath(), imageAware,options);

                viewHolder.fontAwesomeIcon.setVisibility(TextView.GONE);
            } else {
                viewHolder.fontAwesomeIcon.setText(R.string.font_awesome_file);
                viewHolder.picture.setVisibility(ImageView.GONE);
                viewHolder.progress.setVisibility(ProgressBar.GONE);
            }
        }
        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView fontAwesomeIcon;
        ImageView picture;
        ProgressBar progress;
        int position;
    }
}