package com.sag0ld.background_stories;

import android.app.WallpaperManager;
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

        final GridView gridPictureFound = (GridView) findViewById(R.id.gridPictureFound);
        Button btnSetWallpaper = (Button) findViewById(R.id.btnSetWallpaperSelected);

        SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCE_FILE_NAME, MODE_PRIVATE);
        final Set<String> pathsFound = settings.getStringSet("PathPictureFound", new HashSet<String>());

        final Vector<Bitmap> bitmaps = new Vector<Bitmap>(pathsFound.size());
        Iterator<String> it = pathsFound.iterator();
        while(it.hasNext()) {
            Bitmap bitmap = ThumbnailUtils.extractThumbnail
                    (BitmapFactory.decodeFile(it.next()),500,500);
            bitmaps.add(bitmap);
        }

        gridPictureFound.setAdapter(new ImageAdapter(this, bitmaps));
        gridPictureFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                for (int positionChild = 0; positionChild < gridPictureFound.getChildCount();
                        positionChild++) {
                    ImageView child = (ImageView) gridPictureFound.getChildAt(positionChild);
                    child.setBackgroundColor(
                                        getResources().getColor(R.color.backgroundColor));
                    child.setContentDescription("");
                }

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
                for (int positionChild = 0; it.hasNext(); positionChild++){
                    ImageView child = (ImageView) gridPictureFound.getChildAt(positionChild);
                    if(child.getContentDescription() == "Selected") {
                        pathSelected = it.next();
                    }
                }
                if(pathSelected != "") {
                    Intent intent = wallpaperManager.getCropAndSetWallpaperIntent(
                            WallpaperFinderIntentService.getImageContentUri(getApplicationContext(), pathSelected));
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "You need to select one of those picture.",
                                   Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
