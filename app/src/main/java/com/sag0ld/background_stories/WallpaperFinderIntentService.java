package com.sag0ld.background_stories;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.io.File;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Sagold on 2017-01-15.
 */

public class WallpaperFinderIntentService extends IntentService {

    public WallpaperFinderIntentService() {
        super("WallpaperFinderIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("Service strat","Service begin");
        // Get the current date
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);

        Log.d("a","Jour : "+Integer.toString(day) + " Mois : " + Integer.toString(month));
        //Get data from intent
        File directoryChoosen = new File (intent.getStringExtra("PathDirectory"));

        Set<String> pathPictureFounds = new HashSet<String>();
        for (File f : directoryChoosen.listFiles()) {
            // Get the date of the file
            c.setTimeInMillis(f.lastModified());
            int fileDay = c.get(Calendar.DAY_OF_MONTH);
            int fileMonth = c.get(Calendar.MONTH);
            if (f.isFile()) {
                // if the file is an image with the extension autorized
                String extension = f.getName().split("\\.")[1];
                if (day == fileDay && month == fileMonth
                    && (extension.equalsIgnoreCase(DirectoryArrayAdapter.imgExtension.JPEG.toString())
                        || extension.equalsIgnoreCase(DirectoryArrayAdapter.imgExtension.JPG.toString())
                        || extension.equalsIgnoreCase(DirectoryArrayAdapter.imgExtension.PNG.toString())))
                    pathPictureFounds.add(f.getPath());
            }
        }
        Log.d("Service number picture","found " + Integer.toString(pathPictureFounds.size()));
        //Close the app and if it's found some picture then call notification
        String contentTextCustom = "We found a new match!";
        Intent resultIntent;
        if(pathPictureFounds.size() != 0) {
            switch (pathPictureFounds.size()) {
                case 1 :    WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
                            File wallpaperFile = new File(pathPictureFounds.iterator().next());
                            Uri contentURI = getImageContentUri(this, wallpaperFile.getAbsolutePath());
                            resultIntent = wallpaperManager.getCropAndSetWallpaperIntent(contentURI);
                    break;
                default :   contentTextCustom = "We found some new match!";
                            resultIntent = new Intent(this, WallpaperChooser.class);
            }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.story_teller_white)
                    .setContentTitle("Wallpaper founds")
                    .setContentText(contentTextCustom);

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(0, mBuilder.build());
        }
        Log.d("Service finish","Le service a fini");
        stopSelf();
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