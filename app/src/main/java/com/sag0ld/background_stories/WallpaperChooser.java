package com.sag0ld.background_stories;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PaintDrawable;
import android.media.ThumbnailUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class WallpaperChooser extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_chooser);
        final Context m_context = this;

        final GridView gridPictureFound = (GridView) findViewById(R.id.gridPictureFound);
        Button btnSetWallpaper = (Button) findViewById(R.id.btnSetWallpaperSelected);

        SharedPreferences settings = getSharedPreferences
                                    (getString(R.string.preference_file_name), MODE_PRIVATE);
        final Set<String> pathsFound = settings.getStringSet(getString(R.string.saved_path_found),
                                        new HashSet<String>());

        gridPictureFound.setAdapter(new ImageAdapter(this, pathsFound));
        gridPictureFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // Foreach click, reset the background color for all the picture
                for (int positionChild = 0; positionChild < gridPictureFound.getChildCount();
                        positionChild++) {
                    ImageView child = (ImageView) gridPictureFound.getChildAt(positionChild);
                    child.setBackgroundColor(
                                        getResources().getColor(R.color.backgroundColor));
                    child.setContentDescription("");
                }
                // Change the background color of the one clicked
                ImageView imageSelected = (ImageView) gridPictureFound.getChildAt(position);
                imageSelected.setContentDescription("Selected");
                imageSelected.setBackgroundColor(getResources().getColor(R.color.colorButton));
            }
        });

        btnSetWallpaper.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Get the path of the new wallpaper
                WallpaperManager wallpaperManager =
                        WallpaperManager.getInstance(getApplicationContext());

                Iterator<String> it = pathsFound.iterator();
                String pathSelected = "";
                int positionChild = 0;
                while(it.hasNext()) {
                    ImageView child = (ImageView) gridPictureFound.getChildAt(positionChild);
                    if(child.getContentDescription() == "Selected")
                        pathSelected = it.next();
                    else
                        it.next();
                    ++positionChild;
                }

                if(pathSelected !=  "") {
                    Intent intent = new Intent(Intent.ACTION_ATTACH_DATA,
                            WallpaperFinderIntentService.getImageContentUri(m_context,
                                    pathSelected));
                    startActivityForResult(intent, 0);

                } else {
                    Toast.makeText(getApplicationContext(), "You need to select one of those picture.",
                                   Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 0) {
            // Make sure the request was successful
            if (resultCode == 0) {
                finishAndRemoveTask();
            }
        }
    }
}
