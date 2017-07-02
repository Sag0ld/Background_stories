package com.sag0ld.background_stories;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmSetter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

            // Set the broadcast to run the service
            Intent intentAlarm = new Intent(context, AlertReceiver.class);
            intent.setAction("com.sag0ld.background_stories.START_WALLPAPERSERVICE");
            PendingIntent pendingIntent = PendingIntent.getBroadcast
                    (context, 0, intentAlarm, PendingIntent.FLAG_ONE_SHOT);

            // Set the alarm to start at midnight
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }
