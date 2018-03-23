package com.example.rcarb.flightservice.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.rcarb.flightservice.MainActivity;
import com.example.rcarb.flightservice.objects.FlightObject;
import com.example.rcarb.flightservice.objects.FlightTimeObject;
import com.example.rcarb.flightservice.receivers.ProcessAlarmReceiver;
import com.example.rcarb.flightservice.utilities.DataCheckingUtils;
import com.example.rcarb.flightservice.utilities.ExtractFlightUtilities;
import com.example.rcarb.flightservice.utilities.IntentActions;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by rcarb on 3/5/2018.
 */

public class AlarmService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public AlarmService() {
        super("AlarmService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        int index = intent.getIntExtra(IntentActions.INTENT_SEND_INT, -2);
        ArrayList<FlightObject> flightArrayAlarms =
                intent.getParcelableArrayListExtra(IntentActions.PUT_EXTRA_PARCEL_ARRAY);

//        if (index != flightArrayAlarms.size()) {
        if (index != -2) {

            //Get all the variables make up the flight info.
            FlightObject flight = flightArrayAlarms.get(index);
            String name = flight.getFlightName();
            long flightColumnId = flight.getColumnId();
            int flightActualTime = flight.getActualArrivalTime();
            int flightSchedulke = flight.getFlightScheduledTime();
            String status = flight.getFlightStatus();

            int parseTime = -2;
            int date = Integer.valueOf(ExtractFlightUtilities.getDate());

            boolean timePassedMidnight = false;


            int requestCode = -2;
            int systemTime = (int) System.currentTimeMillis();
            //Get the time that will be used for parsing, could be 25+
            if (flightActualTime == -2) {
                parseTime = flightSchedulke;

            } else {
                parseTime = flightActualTime;
            }

            //Convert time to object, still could be 25+
            FlightTimeObject timeObject = DataCheckingUtils.getConvertedTime(parseTime);
            int hour = timeObject.getHour();

            //If hour is 25+ then it is converted to proper military time.
            if (hour > 23) {
                hour = DataCheckingUtils.convertTimeThatPassedMidnightToProperTime(hour);
                timeObject.setHour(hour);
                timePassedMidnight = true;
            }
            //Converted time object to int;
            parseTime = DataCheckingUtils.convertTimeObjectToInt(timeObject);

            requestCode = requestCode + date;
            requestCode = requestCode + parseTime;
            requestCode = requestCode + systemTime;


            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Intent sendIntent = new Intent(this, ProcessAlarmReceiver.class);
            sendIntent.putExtra(IntentActions.INTENT_SEND_STRING_FLIGHT, name);
            sendIntent.putExtra(IntentActions.INTENT_SEND_FLIGHT_COLUMN_ID, flightColumnId);
            sendIntent.putExtra(IntentActions.INTENT_SEND_FLIGHT_STATUS, status);
            sendIntent.putExtra(IntentActions.INTENT_SEND_SECOND_INT, parseTime);
            sendIntent.putExtra(IntentActions.INTENT_SEND_THRID_INT, flightSchedulke);


            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //Get calendar instance
            Calendar calendar = Calendar.getInstance();

            //Setup hour
            if (timeObject.getHour() != -1) {
                //int hour = timeObject1.getHour();
                calendar.set(Calendar.HOUR_OF_DAY, timeObject.getHour());
            }

            //get minute
            int minute = timeObject.getMinute();
            if (timeObject.getMinute() != -1 || timeObject.getMinute() != -2) {
                calendar.set(Calendar.MINUTE, minute + 1);
            }


            Calendar newCalendar = DataCheckingUtils.adjustAlarmTime(calendar);
            Calendar calendarToUse = null;
            if (timePassedMidnight) {
                calendarToUse = newCalendar;
            } else if (!timePassedMidnight) {
                calendarToUse = calendar;
            }


            assert alarmManager != null;
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            alarmManager.setExact(AlarmManager.RTC,
                    calendarToUse.getTimeInMillis(),
                    pendingIntent);

            flight.setAlarm(requestCode);
            flightArrayAlarms.set(index, flight);

            Intent broadcastIntent = new Intent(IntentActions.ACTION_CONTINUE_SETTING_ALARMS);
            broadcastIntent.putExtra(IntentActions.INTENT_SEND_INT, index);
            broadcastIntent.putExtra(IntentActions.INTENT_SEND_STRING, name);
            ;
            broadcastIntent.putParcelableArrayListExtra(IntentActions.PUT_EXTRA_PARCEL_ARRAY, flightArrayAlarms);
            sendBroadcast(broadcastIntent);
        }
    }
}

