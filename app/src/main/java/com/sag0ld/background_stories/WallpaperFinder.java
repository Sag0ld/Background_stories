package com.sag0ld.background_stories;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.io.File;
import java.util.Calendar;

/**
 * Created by Sagold on 2017-01-15.
 */

public class WallpaperFinder extends AsyncTask<String, Integer, String> {

    private SharedPreferences settings;
    public WallpaperFinder (Context p_context) {
        settings = p_context.getSharedPreferences(MainActivity.PREFERENCE_FILE_NAME,
                Context.MODE_PRIVATE);
    }

    @Override
    protected String doInBackground(String... params) {
        // Get the current date
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);

        // Get the first file corresponding with the current date
        File directoryChoosen = new File(params[0]);
        for (File f : directoryChoosen.listFiles()) {
            // Get the date of the file
            c.setTimeInMillis(f.lastModified());
            int fileDay = c.get(Calendar.DAY_OF_MONTH);
            int fileMonth = c.get(Calendar.MONTH);
            if (f.isFile()) {
                // if the file is an image with the extension autorized
                String extension = f.getName().split("\\.")[1];
                if (day == fileDay && month == fileMonth &&
                   (extension.equalsIgnoreCase(DirectoryArrayAdapter.imgExtension.JPEG.toString())||
                    extension.equalsIgnoreCase(DirectoryArrayAdapter.imgExtension.JPG.toString()) ||
                    extension.equalsIgnoreCase(DirectoryArrayAdapter.imgExtension.PNG.toString()))){
                    return f.getPath();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String p_result) {
        SharedPreferences.Editor settingsEditor = settings.edit();
        if (p_result != null) {
            settingsEditor.putString("PathPictureFound", p_result);
        } else {
            // Set a wallpaper by default
            settingsEditor.putString("PathPictureFound", "");
        }
        settingsEditor.apply();
    }

    // http://stackoverflow.com/questions/23207604/get-a-content-uri-from-a-file-uri
    static Uri getImageContentUri(Context p_context, String p_path) {
        Cursor cursor = p_context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , new String[]{MediaStore.Images.Media._ID}
                , MediaStore.Images.Media.DATA + "=? "
                , new String[]{p_path}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        Integer.toString(id));

        } else if (!p_path.isEmpty()) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, p_path);
            return p_context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            return null;
        }
    }
}