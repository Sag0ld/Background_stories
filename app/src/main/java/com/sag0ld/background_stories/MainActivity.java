package com.sag0ld.background_stories;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {

    // Constant
    public static final String PREFERENCE_FILE_NAME = "PreferenceSetting";

    // Variable
    public enum BrowseType {Picture, Folder}
    private EditText editPathFolder;
    private EditText editPathPicture;
    private Button btnDone;
    private ProgressBar spinner;
    private SharedPreferences settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize interface
        Button buttonFindFolder = (Button) findViewById(R.id.buttonFindFolder);
        Button btnBrowsePicture = (Button) findViewById(R.id.btnBrowsePicture);
        editPathFolder = (EditText) findViewById(R.id.pathFolder);
        editPathPicture = (EditText) findViewById(R.id.pathDefaultWallpaper);
        btnDone = (Button) findViewById(R.id.btnDone);
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        // If preferrenceSetting exist, restore them else its going to be created
        settings = getSharedPreferences(PREFERENCE_FILE_NAME, MODE_PRIVATE);
        editPathFolder.setText(settings.getString("PathFolder", ""));
        editPathPicture.setText(settings.getString("PathDefaultPicture", ""));


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

        final Button.OnClickListener btnDoneOnClick = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WallpaperFinder(getApplicationContext()).execute(editPathFolder.getText().toString());
                spinner.setVisibility(View.GONE);
                btnDone.setEnabled(true);
                Toast message = Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_LONG);
                message.show();
            }
        };

        // Initialize action
        buttonFindFolder.setOnClickListener(findFolderListener);
        btnBrowsePicture.setOnClickListener(browsePictureListener);
        btnDone.setOnClickListener(btnDoneOnClick);

        // TODO
        // Finf a way to put a listener on the clock and run this methode only at this time
        // showNotification();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences  = getSharedPreferences(PREFERENCE_FILE_NAME, MODE_PRIVATE);
            editPathFolder.setText(preferences.getString("PathFolder", ""));
            editPathPicture.setText(preferences.getString("PathDefaultPicture", ""));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (0) : {
                // Update the current path of the folder choosen
                if (resultCode == Activity.RESULT_OK) {
                    SharedPreferences.Editor settingsEditor  = settings.edit();
                    settingsEditor.putString("PathFolder", data.getStringExtra("pathFolder"));
                    settingsEditor.apply();
                }
                break;
            }
            case (1) : {
                // Update the current path of the picture choosen
                if (resultCode == Activity.RESULT_OK) {
                    SharedPreferences.Editor settingsEditor  = settings.edit();
                    settingsEditor.putString("PathDefaultPicture", data.getStringExtra("pathPicture"));
                    settingsEditor.apply();
                }
                break;
            }
        }
    }
}


