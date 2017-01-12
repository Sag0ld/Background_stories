package com.sag0ld.background_stories;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;

public class MainActivity extends Activity {

    // Variable
    public enum BrowseType {Picture, Folder};
    private Button buttonFindFolder;
    private EditText editPath;
    private Button btnBrowsePicture;
    private EditText editPathPicture;
    private Button btnDone;
    private String pathFolder = "";
    private String pathPicture = "";
    private ProgressBar spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize interface
        buttonFindFolder = (Button) findViewById(R.id.buttonFindFolder);
        editPath = (EditText) findViewById(R.id.pathFolder);
        btnBrowsePicture = (Button) findViewById(R.id.btnBrowsePicture);
        editPathPicture = (EditText) findViewById(R.id.pathDefaultWallpaper);
        btnDone = (Button) findViewById(R.id.btnDone);
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        // Listener
        View.OnClickListener findFolderListener = new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SearchView.class);
                i.putExtra("BrowseType", BrowseType.Folder.toString());
                startActivityForResult(i, 0);
            }
        };

        View.OnClickListener browsePictureListener = new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SearchView.class);
                i.putExtra("BrowseType", BrowseType.Picture.toString());
                startActivityForResult(i, 1);
            }
        };

        final Button.OnClickListener btnDoneOnClick = new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                new FindWallpaper().execute(pathFolder);
                spinner.setVisibility(View.GONE);
                btnDone.setEnabled(true);
            }
        };

        // Initialize action
        buttonFindFolder.setOnClickListener(findFolderListener);
        btnBrowsePicture.setOnClickListener(browsePictureListener);
        btnDone.setOnClickListener(btnDoneOnClick);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pathFolder != "")
            editPath.setText(pathFolder);
        if (pathPicture != "")
            editPathPicture.setText(pathPicture);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (0) : {
                // Update the current path of the folder choosen
                if (resultCode == Activity.RESULT_OK) {
                    pathFolder = data.getStringExtra("pathFolder");
                }
                break;
            }
            case (1) : {
                // Update the current path of the picture choosen
                if (resultCode == Activity.RESULT_OK) {
                    pathPicture = data.getStringExtra("pathPicture");
                }
                break;
            }
        }
    }


    private class FindWallpaper extends AsyncTask<String, Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            // Get the current date
            Calendar c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);

            // Get the first file corresponding with the current date
            File directoryChoosen = new File(params[0].toString());
            for (File f : directoryChoosen.listFiles()) {
                c.setTimeInMillis(f.lastModified());
                int fileDay = c.get(Calendar.DAY_OF_MONTH);
                int fileMonth = c.get(Calendar.MONTH);
                if (f.isFile()) {
                    String extension = f.getName().split("\\.")[1];
                    if (day == fileDay && month == fileMonth &&
                       (extension.equalsIgnoreCase(DirectoryArrayAdapter.imgExtension.JPEG.toString())||
                        extension.equalsIgnoreCase(DirectoryArrayAdapter.imgExtension.JPG.toString()) ||
                        extension.equalsIgnoreCase(DirectoryArrayAdapter.imgExtension.PNG.toString()))) {
                        try {
                             return MediaStore.Images.Media.getBitmap(getContentResolver(),
                                    android.net.Uri.parse(f.toURI().toString()));
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            spinner.setProgress(progress[0]);
        }
        protected void onPreExecute() {
            spinner.setVisibility(View.VISIBLE);
            btnDone.setEnabled(false);
        }

        protected void onPostExecute(Bitmap result) {
            WallpaperManager myWallpaperManager
                    = WallpaperManager.getInstance(getApplicationContext());
            if (result != null) {
                try {
                    myWallpaperManager.setBitmap(result);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                // Set a wallpaper by default
                try {
                    File defaultPicture = new File(pathPicture);

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                            android.net.Uri.parse(defaultPicture.toURI().toString()));
                    myWallpaperManager.setBitmap(bitmap);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}


