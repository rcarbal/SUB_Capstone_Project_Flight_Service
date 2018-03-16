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


                FlightObject flight = flightArrayAlarms.get(index);
                String name = flight.getFlightName();
                long flightColumnId = flight.getColumnId();
                int flightActualTime = flight.getActualArrivalTime();
                int flightSchedulke = flight.getFlightScheduledTime();
                String status = flight.getFlightStatus();
                int parseTime = -2;
                int date = Integer.valueOf(ExtractFlightUtilities.getDate());

                int requestCode = -2;
                int systemTime = (int) System.currentTimeMillis();

                if (flightActualTime == -2) {
                    parseTime = flightSchedulke;

                } else {
                    parseTime = flightActualTime;
                }

                requestCode = requestCode + date;
                requestCode = requestCode + parseTime;
                requestCode = requestCode + systemTime;


                FlightTimeObject timeObject = DataCheckingUtils.getConvertedTime(parseTime);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                Intent sendIntent = new Intent(this, ProcessAlarmReceiver.class);
                sendIntent.putExtra(IntentActions.INTENT_SEND_STRING_FLIGHT, name);
                sendIntent.putExtra(IntentActions.INTENT_SEND_FLIGHT_COLUMN_ID, flightColumnId);
                sendIntent.putExtra(IntentActions.INTENT_SEND_FLIGHT_STATUS, status);


                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Get calendar instance
                Calendar calendar = Calendar.getInstance();

//            FlightTimeObject timeObject1 = DataCheckingUtils.getConvertedTime(time);

                //get hour
                if (timeObject.getHour() != -1) {
                    int hour = timeObject.getHour();
//                int hour = timeObject1.getHour();
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                }

                //get minute
                if (timeObject.getMinute() != -1) {
                    int minute = timeObject.getMinute();
//                int minute = timeObject1.getMinute();
                    calendar.set(Calendar.MINUTE, minute);


                    calendar.set(Calendar.SECOND, 0);
//                Calendar newCalendar = DataCheckingUtils.adjustAlarmTime(calendar);

                    assert alarmManager != null;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    alarmManager.setExact(AlarmManager.RTC,
                            calendar.getTimeInMillis(),
                            pendingIntent);

                    flight.setAlarm(requestCode);
                    flightArrayAlarms.set(index, flight);

                    Intent broadcastIntent = new Intent(IntentActions.ACTION_CONTINUE_SETTING_ALARMS);
                    broadcastIntent.putExtra(IntentActions.INTENT_SEND_INT, index);
                    broadcastIntent.putExtra(IntentActions.INTENT_SEND_STRING, name);
//                broadcastIntent.putExtra(IntentActions.INTENT_SEND_SECOND_INT, time);
                    broadcastIntent.putParcelableArrayListExtra(IntentActions.PUT_EXTRA_PARCEL_ARRAY, flightArrayAlarms);
                    sendBroadcast(broadcastIntent);
                }
            }
//        }
//        else{
//            Intent finsihAlarms = new Intent(IntentActions.ACTION_FINISHED_ALARMS);
//            sendBroadcast(finsihAlarms);
//        }
    }
}
