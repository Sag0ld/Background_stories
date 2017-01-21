package com.sag0ld.background_stories;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagold on 2017-01-09.
 */

public class DirectoryArrayAdapter extends ArrayAdapter<File> {
    private Context context;
    private List<File> rentalProperties;
    public enum fileType {Dir, File}
    public enum imgExtension { JPG, PNG, JPEG}

    //constructor, call on creation
    public DirectoryArrayAdapter (Context context, int resource, ArrayList<File> objects) {
        super(context, resource, objects);

        this.context = context;
        this.rentalProperties = objects;
    }

    //called when rendering the list
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the property we are displaying
        File item = rentalProperties.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService
                                                    (Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_list, null);

        TextView path = (TextView) view.findViewById(R.id.txtPath);
        TextView name = (TextView) view.findViewById(R.id.txtItemName);
        TextView fontAwesomeIcon = (TextView) view.findViewById(R.id.fontAwesomeIconTextView);
        TextView type = (TextView) view.findViewById(R.id.txtType);
        ImageView imageAccepted = (ImageView) view.findViewById(R.id.imageThumbnail);

        Typeface fontAwesomeFont = Typeface.createFromAsset(context.getAssets(),
                                    "fonts/fontawesome-webfont.ttf");
        fontAwesomeIcon.setTypeface(fontAwesomeFont);
        path.setText(item.getPath());
        name.setText(item.getName());

        //Initilize type and imageView
        if(item.isDirectory()) {
            type.setText(fileType.Dir.name());
            fontAwesomeIcon.setText(R.string.font_awesome_folder);
            imageAccepted.setVisibility(ImageView.GONE);
        } else {
            String[] separeteditems = item.getName().split("\\.");
            String extension  = separeteditems[separeteditems.length - 1];
            // If it's an image accepted or another file
            if(extension.equalsIgnoreCase(imgExtension.JPEG.toString()) ||
               extension.equalsIgnoreCase(imgExtension.JPG.toString()) ||
               extension.equalsIgnoreCase(imgExtension.PNG.toString())) {
                Bitmap bitmap = ThumbnailUtils.extractThumbnail
                        (BitmapFactory.decodeFile(item.getPath()),50,50);
                imageAccepted.setImageBitmap(bitmap);
                fontAwesomeIcon.setVisibility(TextView.GONE);
            } else {
                fontAwesomeIcon.setText(R.string.font_awesome_file);
                imageAccepted.setVisibility(ImageView.GONE);
            }
            type.setText(fileType.File.name());
        }
        return view;
    }
}
