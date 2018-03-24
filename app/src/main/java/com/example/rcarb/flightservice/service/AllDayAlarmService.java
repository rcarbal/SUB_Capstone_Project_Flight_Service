package com.example.rcarb.flightservice.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.rcarb.flightservice.receivers.AllDayAlarmReciever;
import com.example.rcarb.flightservice.utilities.DataCheckingUtils;
import com.example.rcarb.flightservice.utilities.ExtractFlightUtilities;
import com.example.rcarb.flightservice.utilities.IntentActions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by rcarb on 3/8/2018.
 */

public class AllDayAlarmService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public AllDayAlarmService() {
        super("AllDayAlarmService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int numberOfAlarms = intent.getIntExtra(IntentActions.ACTION_SEND_ALL_DAY_ALARMS_INT, -2);
        int initialHour = intent.getIntExtra(IntentActions.ACTION_SEND_INITIAL_HOUR_INT, -2);

        if (initialHour %2 != 0) {
            initialHour = initialHour - 1;
        }

//        else if (initialHour %2 == 0){
//            initialHour = initialHour -1;
//        }
        int date = Integer.valueOf(ExtractFlightUtilities.getDate());
        int requestCode = 1;
        int systemTime = (int)System.currentTimeMillis();
        requestCode = requestCode + initialHour;
        requestCode = requestCode + date;
        requestCode = systemTime + requestCode;





        if (numberOfAlarms != -2 && initialHour != -2) {

            Intent dayAlarm = new Intent(this, AllDayAlarmReciever.class);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                    requestCode,
                    dayAlarm,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            //Get calendar instance
            Calendar calendar = Calendar.getInstance();

            int hour =  initialHour+2;

            //get hour
            if (initialHour > 0) {;
                calendar.set(Calendar.HOUR_OF_DAY, hour);
            }
                int minute = 5;
                calendar.set(Calendar.MINUTE, minute);


                calendar.set(Calendar.SECOND, 0);
               // Calendar newCalendar = DataCheckingUtils.adjustAlarmTime(calendar);

                assert alarmManager != null;

                alarmManager.setExact(AlarmManager.RTC,
                        calendar.getTimeInMillis(),
                        pendingIntent);

                int hourSecond = calendar.get(Calendar.HOUR_OF_DAY);
                int minuteSecond= calendar.get(Calendar.MINUTE);

                Intent processRemainingAlarms = new Intent(IntentActions.ACTION_PROCESS_REMAINING_DAILY_ALARMS);
                processRemainingAlarms.putExtra(IntentActions.ACTION_SEND_ALL_DAY_ALARMS_INT, numberOfAlarms);
                processRemainingAlarms.putExtra(IntentActions.ACTION_SEND_INITIAL_HOUR_INT, hour);
                sendBroadcast(processRemainingAlarms);
        }
    }
}
