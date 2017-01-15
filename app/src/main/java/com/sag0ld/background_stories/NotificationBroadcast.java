package com.sag0ld.background_stories;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;

import java.io.File;

import static com.sag0ld.background_stories.MainActivity.*;

/**
 * Created by Sagold on 2017-01-15.
 */

public class NotificationBroadcast extends BroadcastReceiver {

    // Constant
    final private String PREFERENCE_FILE_NAME = "PreferenceSetting";
    private Context m_context;
    @Override
    public void onReceive(Context context, Intent intent) {
        m_context = context;
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_FILE_NAME,
                Context.MODE_PRIVATE);
        MainActivity.FindWallpaper.execute(settings.getString("PathFolder", ""));
        showNotification();

    }

    private void showNotification (String p_pathPictureFound) {
        // Notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(m_context)
                        .setSmallIcon(R.drawable.folder)
                        .setContentTitle("New Wallpaper found!")
                        .setContentText("We find a new match!")
                        .setAutoCancel(true);

        // Set the new image to Crop and set to background
        WallpaperManager wallpaperManager
                = WallpaperManager.getInstance(m_context);
        File wallpaperFile = new File(p_pathPictureFound);

        // Get the content Uri to set into the intent
        Uri contentURI = getImageContentUri(m_context, wallpaperFile.getAbsolutePath());
        Intent intent = wallpaperManager.getCropAndSetWallpaperIntent(contentURI);

        // Set the intent to the notification Click
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(m_context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) m_context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Show the notification
        notificationManager.notify(Notification.PRIORITY_DEFAULT, notificationBuilder.build());
    }

    // http://stackoverflow.com/questions/23207604/get-a-content-uri-from-a-file-uri
    public Uri getImageContentUri(Context context, String absPath) {
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
