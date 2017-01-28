package com.sag0ld.background_stories;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Sagold on 2017-01-15.
 */

public class NotificationBroadcast extends BroadcastReceiver {

    private Context m_context;
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"Work",Toast.LENGTH_LONG).show();
        m_context = context;
        SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFERENCE_FILE_NAME,
        Context.MODE_PRIVATE);
        new WallpaperFinder(context).execute(settings.getString("PathFolder", ""));

        Set<String> pathPicture = settings.getStringSet("PathPictureFound" , new HashSet<String>());

        // if no picture found, don't push a notification
        if (pathPicture.size() > 0)
            showNotification(pathPicture);
    }

    private void showNotification (Set<String> p_pathPictureFound) {
        // Notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(m_context)
                        .setSmallIcon(R.drawable.folder)
                        .setContentTitle("New Wallpaper found!")
                        .setContentText("We find a new match!")
                        .setAutoCancel(true);
        Intent intent;
        if (p_pathPictureFound.size() == 1) {
            // Set the new image to Crop and set to background
            WallpaperManager wallpaperManager
                    = WallpaperManager.getInstance(m_context);
            File wallpaperFile = new File(p_pathPictureFound.iterator().next());

            // Get the content Uri to set into the intent
            Uri contentURI = WallpaperFinder.getImageContentUri(m_context, wallpaperFile.getAbsolutePath());
            intent = wallpaperManager.getCropAndSetWallpaperIntent(contentURI);
        }
        // If founded more then one picture
        else {
            intent = new Intent(m_context, WallpaperChooser.class);
        }

        // Set the intent to the notification Click
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(m_context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) m_context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Show the notification
        notificationManager.notify(Notification.PRIORITY_DEFAULT, notificationBuilder.build());
    }
}
