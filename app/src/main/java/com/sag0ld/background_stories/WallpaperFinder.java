package com.sag0ld.background_stories;

import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.io.File;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Sagold on 2017-01-15.
 */

public class WallpaperFinder extends AsyncTask<String, Integer, String> {

    private Context m_context;
    private SharedPreferences settings;
    public WallpaperFinder (Context p_context) {
        m_context = p_context;
        settings = m_context.getSharedPreferences(MainActivity.PREFERENCE_FILE_NAME, MODE_PRIVATE);
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
            c.setTimeInMillis(f.lastModified());
            int fileDay = c.get(Calendar.DAY_OF_MONTH);
            int fileMonth = c.get(Calendar.MONTH);
            if (f.isFile()) {
                String extension = f.getName().split("\\.")[1];
                if (day == fileDay && month == fileMonth &&
                        (extension.equalsIgnoreCase(DirectoryArrayAdapter.imgExtension.JPEG.toString())||
                                extension.equalsIgnoreCase(DirectoryArrayAdapter.imgExtension.JPG.toString()) ||
                                extension.equalsIgnoreCase(DirectoryArrayAdapter.imgExtension.PNG.toString()))) {
                    return f.getPath();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        WallpaperManager wallpaperManager
                = WallpaperManager.getInstance(m_context);
        if (result != null) {
            Uri contentURI = getImageContentUri(m_context, result);
            Intent intent = wallpaperManager.getCropAndSetWallpaperIntent(contentURI);
            m_context.startActivity(intent);
        } else {
            // Set a wallpaper by default
            Uri contentURI = getImageContentUri(m_context, settings.getString("PathDefaultPicture", ""));
            Intent intent = wallpaperManager.getCropAndSetWallpaperIntent(contentURI);
            m_context.startActivity(intent);

        }
    }

    // http://stackoverflow.com/questions/23207604/get-a-content-uri-from-a-file-uri
    public static Uri getImageContentUri(Context context, String absPath) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , new String[]{MediaStore.Images.Media._ID}
                , MediaStore.Images.Media.DATA + "=? "
                , new String[]{absPath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(id));

        } else if (!absPath.isEmpty()) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, absPath);
            return context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            return null;
        }
    }
}
