package com.sag0ld.background_stories;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
        // Get the db
        SharedPreferences settings = getSharedPreferences
                (getString(R.string.preference_file_name), MODE_PRIVATE);

        // Get the current date
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);

        //Get data from the bd
        String pathDirectory = settings.getString(getString(R.string.saved_path_directory),
                                                  getString(R.string.saved_path_directory_default));
        String pathDefaultWallpaper = settings.getString(
                getString(R.string.saved_path_wallpaper),
                getString(R.string.saved_path_default_wallpaper));
        String useDefaultWallpaper = settings.getString(
                getString(R.string.saved_wallpaper_default_set),
                getString(R.string.saved_wallpaper_default_set_option));

        SharedPreferences.Editor settingsEditor = settings.edit();
        Set<String> pathPictureFounds = new HashSet<String>();
        if (pathDirectory != "") {
            File directoryChoosen = new File(pathDirectory);
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

            Log.d("Service number picture", "found " + Integer.toString(pathPictureFounds.size()));
        }
        if ((useDefaultWallpaper.compareTo("No") == 0 && pathPictureFounds.size() >= 0) ||
                pathDefaultWallpaper != "") {

            Uri contentURI = getImageContentUri(this, pathDefaultWallpaper);
            Intent setWallPaperIntent = new Intent(Intent.ACTION_ATTACH_DATA, contentURI);

            String contentTextCustom = "We found nothing :( But we gonna set your default choose!";

            switch (pathPictureFounds.size()) {
                case 0:
                    break;
                case 1:
                    contentTextCustom = "We found a new match!";

                    File wallpaperFile = new File(pathPictureFounds.iterator().next());
                    contentURI = getImageContentUri(this, wallpaperFile.getAbsolutePath());
                    setWallPaperIntent = new Intent(Intent.ACTION_ATTACH_DATA, contentURI);

                    // Remove All path who's set in the bd
                    settingsEditor.remove(getString(R.string.saved_path_found));
                    settingsEditor.commit();

                    break;
                // More then one picture founds
                default:
                    contentTextCustom = "We found some new match!";

                    // Set all the path found in the sharedPref
                    settingsEditor.putStringSet(getString(R.string.saved_path_found),
                            pathPictureFounds);
                    settingsEditor.commit();

                    setWallPaperIntent = new Intent(this, WallpaperChooser.class);
            }
            // Set the first time used
            settingsEditor.putString(getString(R.string.saved_wallpaper_default_set),
                    "Yes");
            settingsEditor.commit();

            // Set the notification
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.story_teller_white)
                    .setContentTitle("Wallpaper founds")
                    .setContentText(contentTextCustom)
                    .setAutoCancel(true);

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);

            // Adds the Intent that starts the Activity to the top of the stack
            if(pathPictureFounds.size() > 1) {
                stackBuilder.addNextIntent(setWallPaperIntent);
            }
            else stackBuilder.addNextIntent(Intent.createChooser(setWallPaperIntent,
                    "Set as:"));

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