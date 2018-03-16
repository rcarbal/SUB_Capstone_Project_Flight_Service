package com.example.rcarb.flightservice.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.rcarb.flightservice.receivers.AlarmTimeFrameReceiver;
import com.example.rcarb.flightservice.utilities.DataCheckingUtils;

import java.util.Calendar;

/**
 * Created by rcarb on 3/14/2018.
 */

public class SetupTimeFrameService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SetupTimeFrameService() {
        super("SetupTimeFrameService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int requestCode = 0;
        Intent receiver = new Intent(this, AlarmTimeFrameReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                requestCode,
                receiver,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //Get calendar instance
        Calendar calendar = Calendar.getInstance();

        int minute = 1;
        calendar.set(Calendar.MINUTE, minute);

        assert alarmManager != null;

        alarmManager.setRepeating(AlarmManager.RTC,
                calendar.getTimeInMillis(), 1000 * 60,
                pendingIntent);


    }
}
