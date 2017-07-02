package com.sag0ld.background_stories;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlertReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Start the WallpaperFinderServices
        Intent serviceIntent = new Intent(context.getApplicationContext(), WallpaperFinderIntentService.class);
        context.getApplicationContext().startService(serviceIntent);
    }
}