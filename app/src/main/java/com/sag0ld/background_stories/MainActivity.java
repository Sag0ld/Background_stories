package com.sag0ld.background_stories;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Variable
        Button buttonFindFolder = (Button) findViewById(R.id.buttonFindFolder);
        final EditText editPath = (EditText) findViewById(R.id.pathFolder);
        Button btnDone = (Button) findViewById(R.id.btnDone);

        // Update the current path choosen
        if(getIntent().hasExtra("path")){
            editPath.setText(getIntent().getStringExtra("path"));
        }

        // Listener
        View.OnClickListener findFolderListener = new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchView.class));
            }
        };

        Button.OnClickListener btnDoneOnClick = new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(editPath.length() != 0) {
                    // Get the current date
                    Calendar c = Calendar.getInstance();
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    int month = c.get(Calendar.MONTH);

                    // Get the first file corresponding with the current date
                    File directoryChoosen = new File(editPath.getText().toString());
                    for (File f : directoryChoosen.listFiles()) {
                        c.setTimeInMillis(f.lastModified());
                        int fileDay =  c.get(Calendar.DAY_OF_MONTH);
                        int fileMonth = c.get(Calendar.MONTH);
                        String extension = f.getName().split("\\.")[1];

                        if (f.isFile() && day == fileDay && month == fileMonth && (
                           extension.equalsIgnoreCase(DirectoryArrayAdapter.imgExtension.JPEG.toString()) ||
                           extension.equalsIgnoreCase(DirectoryArrayAdapter.imgExtension.JPG.toString()) ||
                           extension.equalsIgnoreCase(DirectoryArrayAdapter.imgExtension.PNG.toString()))) {

                            WallpaperManager myWallpaperManager
                                    = WallpaperManager.getInstance(getApplicationContext());
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                        android.net.Uri.parse(f.toURI().toString()));
                                myWallpaperManager.setBitmap(bitmap);
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        } else { // Set a wallpaper by default
                            WallpaperManager myWallpaperManager
                                    = WallpaperManager.getInstance(getApplicationContext());
                            try {
                               /* Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                        android.net.Uri.parse(defaultPicture.toURI().toString()));
                                myWallpaperManager.setBitmap(bitmap); */
                                InputStream is = getResources().openRawResource(+ R.drawable.unknownfile);
                                myWallpaperManager.setStream(is);
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        };




        // Initialize action
        buttonFindFolder.setOnClickListener(findFolderListener);
        btnDone.setOnClickListener(btnDoneOnClick);
    }
}
