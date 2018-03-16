package com.example.rcarb.flightservice.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.example.rcarb.flightservice.objects.FlightObject;
import com.example.rcarb.flightservice.objects.FlightTimeObject;
import com.example.rcarb.flightservice.receivers.ProcessSecondAlarmReceiver;
import com.example.rcarb.flightservice.utilities.DataCheckingUtils;
import com.example.rcarb.flightservice.utilities.ExtractFlightUtilities;
import com.example.rcarb.flightservice.utilities.IntentActions;

import java.util.Calendar;

/**
 * Created by rcarb on 3/6/2018.
 */

public class SecondAlarmService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SecondAlarmService() {
        super("SecondAlarmServic");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        FlightObject flightThatNeedsAlarm = intent.getParcelableExtra(IntentActions.INTENT_SEND_PARCEL_TO_SECOND_ALARM_SERVICE);

        String name = flightThatNeedsAlarm.getFlightName();
        String extractNumberFromName = name.substring(name.length() - 3, name.length());
        int number = -1;
        try {
            number = Integer.valueOf(extractNumberFromName);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        String status = flightThatNeedsAlarm.getFlightStatus();
        long column = flightThatNeedsAlarm.getColumnId();

        int date = Integer.valueOf(ExtractFlightUtilities.getDate());

        int schedualedTime = flightThatNeedsAlarm.getFlightScheduledTime();
        int actualTime = flightThatNeedsAlarm.getActualArrivalTime();
        int parseTime = -2;
        int requestCode = -2;
        int systemTime = (int) System.currentTimeMillis();

        if (actualTime != -2) {
            parseTime = actualTime;
        } else {
            parseTime = schedualedTime;
        }

        requestCode = requestCode + date;
        requestCode = requestCode + parseTime;
        requestCode = requestCode + systemTime;
        requestCode = requestCode + number;

        FlightTimeObject timeObject = DataCheckingUtils.getConvertedTime(parseTime);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //Get calendar instance
        Calendar calendar = Calendar.getInstance();

        Intent sendIntent = new Intent(this, ProcessSecondAlarmReceiver.class);
        sendIntent.putExtra(IntentActions.INTENT_SEND_STRING_FLIGHT, name);
        sendIntent.putExtra(IntentActions.INTENT_SEND_FLIGHT_COLUMN_ID, column);
        sendIntent.putExtra(IntentActions.INTENT_SEND_FLIGHT_STATUS, status);
        sendIntent.putExtra(IntentActions.INTENT_REQUEST_CODE, requestCode);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //get hour
        if (timeObject.getHour() != -1) {
            int hour = timeObject.getHour();

            calendar.set(Calendar.HOUR_OF_DAY, hour);
//            calendar.set(Calendar.HOUR_OF_DAY, 11);
        }

        //get minute
        if (timeObject.getMinute() != -1) {
            int minute = timeObject.getMinute();
            calendar.set(Calendar.MINUTE, minute + 5);


//            calendar.set(Calendar.MINUTE, 45);

            calendar.set(Calendar.SECOND, 0);
            Calendar newCalendar = DataCheckingUtils.adjustAlarmTime(calendar);

            assert alarmManager != null;

            alarmManager.setRepeating(AlarmManager.RTC,
                    calendar.getTimeInMillis(), 1000 * 60 * 5,
                    pendingIntent);

            Intent secondAlarmSuccessfulBroadcast = new Intent(IntentActions.ACTION_SECOND_ALARM_SUCCESSUFULLY_SETUP);
//            secondAlarmSuccessfulBroadcast.putExtra(IntentActions.INTENT_REQUEST_CODE, requestCode);
            String nameAgain = flightThatNeedsAlarm.getFlightName();
            sendBroadcast(secondAlarmSuccessfulBroadcast);
        }
    }
}
