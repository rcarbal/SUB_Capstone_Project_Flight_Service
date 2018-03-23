package com.example.rcarb.flightservice.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.rcarb.flightservice.receivers.TenPmAlarmReceiver;

import java.util.Calendar;

/**
 * Created by rcarb on 3/15/2018.
 */

public class SetupTenPmAlarmService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public SetupTenPmAlarmService() {
        super("SetupTenPmAlarmService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int requestCode = 3;
        Intent receiver = new Intent(this, TenPmAlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                requestCode,
                receiver,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //Get calendar instance
        Calendar calendar = Calendar.getInstance();

        int hour = 22;
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        int minute = 1;
        calendar.set(Calendar.MINUTE, minute);

        assert alarmManager != null;

        alarmManager.setExact(AlarmManager.RTC,
                calendar.getTimeInMillis(),
                pendingIntent);

    }
}
