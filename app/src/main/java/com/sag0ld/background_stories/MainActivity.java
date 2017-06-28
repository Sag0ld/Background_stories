package com.sag0ld.background_stories;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends Activity {

    // Variable
    public enum BrowseType { Picture, Folder }
    private EditText editPathFolder;
    private EditText editPathPicture;
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
        Button btnSetWallpaper = (Button) findViewById(R.id.btnSetWallPaper);
        Button btnSave = (Button) findViewById(R.id.btnSaveSetting);

        // If preferrenceSetting does exist, restore them else its going to be created
        settings = getSharedPreferences(getString(R.string.preference_file_name), MODE_PRIVATE);

        // Initialize if we already use this app before
        if(!settings.contains(getString(R.string.saved_wallpaper_default_set))) {
            SharedPreferences.Editor settingsEditor = settings.edit();
            settingsEditor.putString(getString(R.string.saved_wallpaper_default_set),
                    getString(R.string.saved_wallpaper_default_set_option));
            settingsEditor.commit();
        }

        editPathFolder.setText(settings.getString(getString(R.string.saved_path_directory),
                                                  getString(R.string.saved_path_directory_default)));
        editPathPicture.setText(settings.getString(getString(R.string.saved_path_wallpaper),
                                                   getString(R.string.saved_path_default_wallpaper)));
        // Listener
        View.OnClickListener browseFolderListener = new View.OnClickListener() {
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

        final Button.OnClickListener btnSetWallpaperOnClick = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Validation
                StringBuilder errorMsgBuilder = new StringBuilder();
                if (editPathFolder.getText().toString().isEmpty())
                    errorMsgBuilder.append("You need to choose a folder!\n");
                if (editPathPicture.getText().toString().isEmpty())
                    errorMsgBuilder.append("You need to choose a default wallpaper picture!\n");
                if (!errorMsgBuilder.toString().isEmpty()) {
                    Toast message = Toast.makeText(getApplicationContext(), errorMsgBuilder, Toast.LENGTH_LONG);
                    message.show();
                }

                Intent serviceIntent = new Intent(MainActivity.this, WallpaperFinderIntentService.class);
                startService(serviceIntent);
                finishAndRemoveTask();
            }
        };
        Button.OnClickListener btnSaveSettingOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAndRemoveTask();
            }
        };

        // Initialize action
        buttonFindFolder.setOnClickListener(browseFolderListener);
        btnBrowsePicture.setOnClickListener(browsePictureListener);
        btnSetWallpaper.setOnClickListener(btnSetWallpaperOnClick);
        btnSave.setOnClickListener(btnSaveSettingOnClick);

        //Initialize the Alarm for the recurency to find a new wallpaper
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, WallpaperFinderIntentService.class);
        PendingIntent alarmIntent = PendingIntent.getService
                (MainActivity.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // Set the alarm to start at midnight
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 16);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences
                                        (getString(R.string.preference_file_name), MODE_PRIVATE);
        editPathFolder.setText(preferences.getString(getString(R.string.saved_path_directory),
                                           getString(R.string.saved_path_directory_default)));
        editPathPicture.setText(preferences.getString(getString(R.string.saved_path_wallpaper),
                                            getString(R.string.saved_path_default_wallpaper)));
    }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 0: {
                // Update the current path of the folder chosen
                if (resultCode == Activity.RESULT_OK) {
                    SharedPreferences.Editor settingsEditor  = settings.edit();
                    settingsEditor.putString(getString(R.string.saved_path_directory),
                            data.getStringExtra(getString(R.string.intent_extra_directory)));
                    settingsEditor.commit();
                }
                break;
            }
            case 1: {
                // Update the current path of the picture chosen
                if (resultCode == Activity.RESULT_OK) {
                    SharedPreferences.Editor settingsEditor  = settings.edit();
                    settingsEditor.putString(getString(R.string.saved_path_wallpaper),
                            data.getStringExtra(getString(R.string.intent_extra_wallpaper)));
                    settingsEditor.commit();
                }
                break;
            }
        }
   }
}