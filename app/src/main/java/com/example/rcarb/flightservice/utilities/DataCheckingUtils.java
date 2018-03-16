package com.example.rcarb.flightservice.utilities;

import com.example.rcarb.flightservice.objects.FlightTimeObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by rcarb on 2/21/2018.
 */

public class DataCheckingUtils {

    public static boolean isNumber(String value) {
        try {
            Double v = Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;

    }

    public static FlightTimeObject getConvertedTime(int time) {
        FlightTimeObject object = new FlightTimeObject();

        if (time < 99) {
            object.setHour(0);
            object.setMinute(time);
        } else if (time > 99 && time < 999) {

            String timeLength = String.valueOf(time);

            int hour = Integer.parseInt(String.valueOf(time).substring(0, 1));
            object.setHour(hour);

            int minute = Integer.parseInt(timeLength.substring(1, timeLength.length()));
            object.setMinute(minute);

        } else if (time > 999 && time < 2359) {
            String timeLength = String.valueOf(time);


            int hour = Integer.parseInt(timeLength.substring(0, 2));
            object.setHour(hour);
            int minute = Integer.parseInt(timeLength.substring(2, timeLength.length()));
            object.setMinute(minute);
        } else if (time == 0) {
            object.setHour(0);
            object.setMinute(0);
        } else {
            object.setHour(-1);
            object.setMinute(-1);
        }
        return object;
    }

    public static FlightTimeObject getConvertedTimeWithPm(int time, boolean pm) {
        boolean hasPm = pm;
        FlightTimeObject object = new FlightTimeObject();

        if (time < 99) {
            object.setHour(0);
            object.setMinute(time);
        } else if (time > 99 && time < 999) {
            if (!hasPm) {
                String timeLength = String.valueOf(time);
                int hour = Integer.parseInt(String.valueOf(time).substring(0, 1));
                object.setHour(hour);

                int minute = Integer.parseInt(timeLength.substring(1, timeLength.length()));
                object.setMinute(minute);

            } else if (hasPm) {
                String timeLength = String.valueOf(time);
                int hour = Integer.parseInt(String.valueOf(time).substring(0, 1));
                hour = hour + 12;
                object.setHour(hour);

                int minute = Integer.parseInt(timeLength.substring(1, timeLength.length()));
                object.setMinute(minute);
            }


        } else if (time > 999 && time < 2359) {
            if (!pm) {
                String timeLength = String.valueOf(time);

                int hour = Integer.parseInt(timeLength.substring(0, 2));
                if (hour == 12) {
                    hour = hour - 12;
                }
                object.setHour(hour);
                int minute = Integer.parseInt(timeLength.substring(2, timeLength.length()));
                object.setMinute(minute);
            } else if (pm) {
                //if its 12
                String timeLength = String.valueOf(time);

                int hour = Integer.parseInt(timeLength.substring(0, 2));
                if (hour == 12) {
                    hour = 12;
                } else if (hour == 10 || hour == 11) {
                    hour = hour + 12;
                }
                object.setHour(hour);
                int minute = Integer.parseInt(timeLength.substring(2, timeLength.length()));
                object.setMinute(minute);
            }
        } else if (time == 0) {
            object.setHour(0);
            object.setMinute(0);
        } else {
            object.setHour(-1);
            object.setMinute(-1);
        }
        return object;
    }

    public static Calendar adjustAlarmTime(Calendar alarmTimeCalendar) {
        Calendar current = Calendar.getInstance();
        Calendar adjustedDateCalendar = Calendar.getInstance();

        int currentHour = current.get(Calendar.HOUR_OF_DAY);
        int currentMinute = current.get(Calendar.MINUTE);

        int hour = alarmTimeCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = alarmTimeCalendar.get(Calendar.MINUTE);

        adjustedDateCalendar.set(Calendar.HOUR_OF_DAY, hour);
        adjustedDateCalendar.set(Calendar.MINUTE, minute);
        adjustedDateCalendar.set(Calendar.SECOND, 0);
        Date currentDate = current.getTime();

        Date alarmDate = adjustedDateCalendar.getTime();
        long difference = alarmDate.getTime() - currentDate.getTime();
        if (difference < 0) {
            adjustedDateCalendar.add(Calendar.DATE, 1);
        }
        int day = adjustedDateCalendar.get(Calendar.DAY_OF_MONTH);
        return adjustedDateCalendar;
    }

    public static int getNumberOfDailyAlarms(int initialHour) {

        int numberOfAlarmsReturned = 0;
        if (initialHour == 0 || initialHour == 1) {
            numberOfAlarmsReturned = 11;
        } else if (initialHour == 2 || initialHour == 3) {
            numberOfAlarmsReturned = 10;
        } else if (initialHour == 4 || initialHour == 5) {
            numberOfAlarmsReturned = 9;
        } else if (initialHour == 6 || initialHour == 7) {
            numberOfAlarmsReturned = 8;
        } else if (initialHour == 8 || initialHour == 9) {
            numberOfAlarmsReturned = 7;
        } else if (initialHour == 10 || initialHour == 11) {
            numberOfAlarmsReturned = 6;
        } else if (initialHour == 12 || initialHour == 13) {
            numberOfAlarmsReturned = 5;
        } else if (initialHour == 14 || initialHour == 15) {
            numberOfAlarmsReturned = 4;
        } else if (initialHour == 16 || initialHour == 17) {
            numberOfAlarmsReturned = 3;
        } else if (initialHour == 18 || initialHour == 19) {
            numberOfAlarmsReturned = 2;
        } else if (initialHour == 20 || initialHour == 21) {
            numberOfAlarmsReturned = 1;
        } else if (initialHour == 22 || initialHour == 23) {
            numberOfAlarmsReturned = 0;
        }
        return numberOfAlarmsReturned;
    }

    public static int converCalendarToInt(Calendar calendar){
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String stringMinute = "";
        if (minute < 10){
            String a = String.valueOf(minute);
            String b ="0";
            stringMinute= ""+b+a;
        }else {
            stringMinute = String.valueOf(minute);
        }

        String stringHour = String.valueOf(hour);


        String timeConc =""+stringHour+stringMinute;

        return Integer.valueOf(timeConc);
    }

    public static int convertPassedMidnight(int time){
        if (time < 60){
            return 2400+time;
        }else if (time > 100 && time < 200){
            time = time %100;
            return time + 2500;
        }
        return time;
    }

}
