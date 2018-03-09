package com.example.rcarb.flightservice.utilities;

import com.example.rcarb.flightservice.objects.FlightTimeObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by rcarb on 2/21/2018.
 */

public class DataCheckingUtils {

    public static boolean isNumber(String value){
        try {
            Double v = Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;

    }

    public static FlightTimeObject getConvertedTime(int time){
        FlightTimeObject object = new FlightTimeObject();

        if (time< 99){
            object.setHour(0);
            object.setMinute(time);
        }
        else if (time > 99 && time <999){

            String timeLength = String.valueOf(time);

            int hour = Integer.parseInt(String.valueOf(time).substring(0,1));
            object.setHour(hour);

            int minute = Integer.parseInt(timeLength.substring(1, timeLength.length()));
            object.setMinute(minute);

        }else if (time> 999 && time <2359){
            String timeLength = String.valueOf(time);


            int hour = Integer.parseInt(timeLength.substring(0,2));
            object.setHour(hour);
            int minute = Integer.parseInt(timeLength.substring(2, timeLength.length()));
            object.setMinute(minute);
        }else if (time == 0){
            object.setHour(0);
            object.setMinute(0);
        }else{
            object.setHour(-1);
            object.setMinute(-1);
        }
        return object;
    }

    public static Calendar adjustAlarmTime(Calendar alarmTimeCalendar){
        Calendar adjustedDateCalendar = Calendar.getInstance();
        int hour = alarmTimeCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = alarmTimeCalendar.get(Calendar.MINUTE);
        adjustedDateCalendar.set(Calendar.HOUR_OF_DAY, hour);
        adjustedDateCalendar.set(Calendar.MINUTE, minute);
        adjustedDateCalendar.set(Calendar.SECOND, 0);
        Date currentDate = adjustedDateCalendar.getTime();
        Date alarmDate = adjustedDateCalendar.getTime();
        long difference = alarmDate.getTime() - currentDate.getTime();
        if (difference < 0) {
            adjustedDateCalendar.add(Calendar.DATE, 1);
        }
        return adjustedDateCalendar;
    }
}
