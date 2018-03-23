package com.example.rcarb.flightservice.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.rcarb.flightservice.receivers.RestartDayReceiver;

import java.util.Calendar;

/**
 * Created by rcarb on 3/15/2018.
 */

public class RestartDayService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.

     */
    public RestartDayService() {
        super("RestartDayService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int requestCode = 2;
        Intent receiver = new Intent(this, RestartDayReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                requestCode,
                receiver,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //Get calendar instance
        Calendar calendar = Calendar.getInstance();
        int hour = 0;
        calendar.set(Calendar.HOUR_OF_DAY, hour);

        int minute = 0;
        calendar.set(Calendar.MINUTE, minute);
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        assert alarmManager != null;

        alarmManager.setExact(AlarmManager.RTC,
                calendar.getTimeInMillis(),
                pendingIntent);

    }
}
