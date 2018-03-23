package com.example.rcarb.flightservice.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.rcarb.flightservice.objects.FlightObject;
import com.example.rcarb.flightservice.objects.FlightTimeObject;
import com.example.rcarb.flightservice.receivers.ProcessAlarmReceiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.net.ssl.SSLContext;

/**
 * Created by rcarb on 2/20/2018.
 */

public class ExtractFlightUtilities {

    public static String getFlightInfoFromWeb(String uri) {
        String parseUri =uri;
        String resString = "";
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(parseUri);//website + string1 + string2
        try {
            HttpResponse responce;
            responce = httpClient.execute(httpGet);
            HttpEntity entity = responce.getEntity();
            InputStream is = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "windows-1251"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            resString = sb.toString();
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        String a ="";

        return resString;
    }


    public static FlightObject saveFlightStringToObject(String flightString) {

        FlightObject flight = new FlightObject();

        if (!flightString.contains("/airlines/")) {
            flight.setFlightName("null");
            flight.setNextFlightIndex(-2);
            flight.setFlightScheduledTime(-2);
            flight.setActualArrivalTime(-2);
            flight.setActualArrivalTime(-2);
            flight.setFlightStatus("null");
            flight.setAirport("null");
            flight.setAirline("null");
            flight.setIsLastFlight(true);
            flight.setParsedString("null");
            flight.setFlightScheduledTime(-2);
            return flight;
        }
        //Get day
        String day = ExtractFlightUtilities.getDay(flightString);
        flight.setDay(day);

        int startIndex = flightString.indexOf("/airlines/");
        int nextIndex = -1;
        if (flightString.indexOf("/airlines/", startIndex + 10) < 0) {
            nextIndex = flightString.indexOf("<!--- END Arrivals --->", startIndex + 10);
            flight.setIsLastFlight(true);


        } else if (flightString.indexOf("/airlines/", startIndex + 10) > -1) {
            nextIndex = flightString.indexOf("/airlines/", startIndex + 10);
        }
        String currentSubstring = flightString.substring(startIndex, nextIndex);

        //get airline
        int startAirlineIndex = currentSubstring.indexOf(">");
        int endAirlineIndex = currentSubstring.indexOf("</a>");
        String airline = currentSubstring.substring(startAirlineIndex + 1, endAirlineIndex);
        flight.setAirline(airline);

        //get flight name
        String flightArrival = "flight_arrival=";
        int flightArrivalLength = flightArrival.length();
        int startFlightNameIndex = currentSubstring.indexOf("flight_arrival=");
        int endFlightNameIndex = currentSubstring.indexOf("\"", startFlightNameIndex);
        String fligthName = currentSubstring.substring(startFlightNameIndex + flightArrivalLength, endFlightNameIndex);

        if (fligthName.contains("&date")) {
            int nameLength = fligthName.length();
            int dateIndex = fligthName.indexOf("&");
            String replacedString = fligthName.substring(dateIndex, nameLength);
            fligthName = fligthName.replace(replacedString, "");
        }
        if (fligthName.equals("WN6560")){

            String a ="";
        }

        flight.setFlightName(fligthName);

        //get airport
        String airportInText = "in_text\">";
        int airportInTextLength = airportInText.length();
        int airportStartIndex = currentSubstring.indexOf("in_text", endFlightNameIndex);
        int airportEndIndex = currentSubstring.indexOf("</a>", airportStartIndex);
        String airport = currentSubstring.substring(airportStartIndex + airportInTextLength, airportEndIndex);
        flight.setAirport(airport);

        //get scheduled time
        int indexScheduled = currentSubstring.indexOf(":", airportEndIndex);
        String scheduledTimeString = currentSubstring.substring(indexScheduled - 2, indexScheduled + 3);
        scheduledTimeString = scheduledTimeString.replace(":", "");
        if (!DataCheckingUtils.isNumber(scheduledTimeString)) {
            scheduledTimeString = "-2";
        }
        int scheduledTime = Integer.valueOf(scheduledTimeString);
        flight.setFlightScheduledTime(scheduledTime);

        //get Actual
        int indexActual = currentSubstring.indexOf(":", indexScheduled + 5);
        String actualTimeString = currentSubstring.substring(indexActual - 2, indexActual + 3);
        actualTimeString = actualTimeString.replace(":", "");
        if (!DataCheckingUtils.isNumber(actualTimeString)) {
            actualTimeString = "-2";
        }
        int actualTime = Integer.valueOf(actualTimeString);
        flight.setActualArrivalTime(actualTime);

        //get Status
        String statusString = ExtractFlightUtilities.getStatus(currentSubstring);
        flight.setFlightStatus(statusString);

        //Get/set gate
        int tdLengeth = "<td>".length();
        int terminalIndex = currentSubstring.indexOf("<td>", indexActual + 5);
        String terminal = currentSubstring.substring(terminalIndex + tdLengeth,
                terminalIndex + tdLengeth + 1);
        int terminalInt = -2;
        if (DataCheckingUtils.isNumber(terminal)) {
            terminalInt = Integer.valueOf(terminal);
        }
        flight.setNextFlightIndex(nextIndex - 1);
        flight.setGate(terminalInt);


        flight.setParsedString(flightString);

        return flight;
    }

    //Extract the second flight
    public static FlightObject getInfoForSingleFlightsSite2(String parseString){
        FlightObject object = new FlightObject();

        int indexStart = parseString.indexOf("Actual");
        int indexEnd = parseString.indexOf("Actual", indexStart+2);

        //surround time
        int estimated = parseString.indexOf("Estimated", indexEnd+20);


        String currentString = null;

        try {
            currentString = parseString.substring(indexEnd, estimated+15);
        } catch (Exception e) {
            e.printStackTrace();
            FlightObject flightObject = new FlightObject();
            return flightObject;
        }

        //get index surrounding the the time along with AM/PM
        //get Actual
        int indexToSkip = currentString.indexOf(":");
        int indexActual = currentString.indexOf(":", indexToSkip+2);
        String findPm = currentString.substring(indexActual, indexActual+8);
        boolean hasPm = false;
        if (findPm.contains("PM")){
            hasPm = true;
        }
        //Get string of time.
        String actualTimeString = currentString.substring(indexActual - 2, indexActual + 3);
        actualTimeString = actualTimeString.replace(":", "");

        if (!DataCheckingUtils.isNumber(actualTimeString)) {
            actualTimeString = "-2";
        }
        //Change string int
        int actualTime = Integer.valueOf(actualTimeString);
        //Convert time to int value
        int convertedToMilitary = DataCheckingUtils.convertTimeToMilitary(actualTime, hasPm);

        object.setActualArrivalTime(convertedToMilitary);

        return object;
    }

    //get inforamtion for a single flight.
    public static FlightObject getInfoForSIngleFlightSite1(String parseString) {
        FlightObject object = new FlightObject();

        int indexStart = parseString.indexOf("flight_arrival=");
        int indexEnd = parseString.indexOf("<!--- END Flights --->");

        String currentString = null;
        try {
            currentString = parseString.substring(indexStart, indexEnd);
        } catch (Exception e) {
            e.printStackTrace();
            FlightObject flightObject = new FlightObject();
            return flightObject;
        }


        //Get the scheduled time of the flight
        int colonIndex = currentString.indexOf(":");
        String scheduled;
        int timeIndex =-2;
        if (colonIndex <0) {
            scheduled = "-2";
        } else {
            timeIndex = currentString.indexOf(":", colonIndex + 2);
            scheduled = currentString.substring(timeIndex - 2, timeIndex + 3);
            scheduled = scheduled.replace(":", "");
            if (!DataCheckingUtils.isNumber(scheduled)) {
                scheduled = "-2";
            }
            int scheduledInteger = Integer.valueOf(scheduled);
            object.setFlightScheduledTime(scheduledInteger);
        }


            //Get actual flight
            String actual;
            int actualColon = currentString.indexOf(":", timeIndex + 1);
            if (actualColon<0){
            actual = "-2";
            }else {
                actual = currentString.substring(actualColon - 2, actualColon + 3);
                actual = actual.replace(":", "");
                if (!DataCheckingUtils.isNumber(actual)) {
                    actual = "-2";
                }
            }

            int actualInteger = Integer.valueOf(actual);
            object.setActualArrivalTime(actualInteger);

            //Get Status
            int indexStarting = currentString.indexOf("img src=");
            int indexEnding = currentString.length();
            String statusStringToParse = currentString.substring(indexStarting, indexEnding);
            String statusString = ExtractFlightUtilities.getStatus(statusStringToParse);
            object.setPostStatus(statusString);


            return object;


        }

    public static FlightObject saveFlightStringToObject(String flightString,
                                                        int startAtIndex) {

        if (startAtIndex < 0) {
            String a = "";
        }


        FlightObject flight = new FlightObject();

        //Get day
        String day = ExtractFlightUtilities.getDay(flightString);
        flight.setDay(day);

        int startIndex = startAtIndex;
        int nextIndex = -1;
        if (flightString.indexOf("/airlines/", startIndex + 10) < 0) {
            nextIndex = flightString.indexOf("<!--- END Arrivals --->", startIndex + 10);
            flight.setIsLastFlight(true);


        } else if (flightString.indexOf("/airlines/", startIndex + 10) > -1) {
            nextIndex = flightString.indexOf("/airlines/", startIndex + 10);
        }


        String currentSubstring = flightString.substring(startIndex, nextIndex);



        //get airline
        int startAirlineIndex = currentSubstring.indexOf(">");
        int endAirlineIndex = currentSubstring.indexOf("</a>");
        String airline = currentSubstring.substring(startAirlineIndex + 1, endAirlineIndex);
        flight.setAirline(airline);

        //get flight name
        String flightArrival = "flight_arrival=";
        int flightArrivalLength = flightArrival.length();
        int startFlightNameIndex = currentSubstring.indexOf("flight_arrival=");
        int endFlightNameIndex = currentSubstring.indexOf("\"", startFlightNameIndex);
        String fligthName = currentSubstring.substring(startFlightNameIndex + flightArrivalLength, endFlightNameIndex);

        if (fligthName.contains("&date")) {
            int nameLength = fligthName.length();
            int dateIndex = fligthName.indexOf("&");
            String replacedString = fligthName.substring(dateIndex, nameLength);
            fligthName = fligthName.replace(replacedString, "");
        }

        flight.setFlightName(fligthName);

        //get airport
        String airportInText = "in_text\">";
        int airportInTextLength = airportInText.length();
        int airportStartIndex = currentSubstring.indexOf("in_text", endFlightNameIndex);
        int airportEndIndex = currentSubstring.indexOf("</a>", airportStartIndex);
        String airport = currentSubstring.substring(airportStartIndex + airportInTextLength, airportEndIndex);
        flight.setAirport(airport);

        //get scheduled time
        int indexScheduled = currentSubstring.indexOf(":", airportEndIndex);
        String scheduledTimeString = currentSubstring.substring(indexScheduled - 2, indexScheduled + 3);
        scheduledTimeString = scheduledTimeString.replace(":", "");
        if (!DataCheckingUtils.isNumber(scheduledTimeString)) {
            scheduledTimeString = "-2";
        }
        int scheduledTime = Integer.valueOf(scheduledTimeString);
        flight.setFlightScheduledTime(scheduledTime);

        //get Actual
        int indexActual = currentSubstring.indexOf(":", indexScheduled + 5);
        String actualTimeString = currentSubstring.substring(indexActual - 2, indexActual + 3);
        actualTimeString = actualTimeString.replace(":", "");
        if (!DataCheckingUtils.isNumber(actualTimeString)) {
            actualTimeString = "-2";
        }
        int actualTime = Integer.valueOf(actualTimeString);
        flight.setActualArrivalTime(actualTime);

        //get Status
        String statusString = ExtractFlightUtilities.getStatus(currentSubstring);
        flight.setFlightStatus(statusString);

        //Get/set gate
        int tdLengeth = "<td>".length();
        int terminalIndex = currentSubstring.indexOf("<td>", indexActual + 5);
        String terminal = currentSubstring.substring(terminalIndex + tdLengeth,
                terminalIndex + tdLengeth + 1);
        int terminalInt = -2;
        if (DataCheckingUtils.isNumber(terminal)) {
            terminalInt = Integer.valueOf(terminal);
        }
        flight.setGate(terminalInt);

        flight.setNextFlightIndex(nextIndex - 1);
        flight.setParsedString(flightString);

        return flight;
    }

    public static String getDate() {
        SimpleDateFormat curFormater = new SimpleDateFormat("ddMMyyyy");
        Date date = new Date();
        return curFormater.format(date);
    }

    //Sets up alarm for a specified flight.
    public static boolean setupAlarm(String flightName, long columnId, int time,int requestCode, Context context) {
        AlarmManager alarmManager;


        FlightTimeObject timeObject = DataCheckingUtils.getConvertedTime(time);

        try {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context.getApplicationContext(), ProcessAlarmReceiver.class);
            intent.putExtra(IntentActions.INTENT_REQUEST_CODE, requestCode);
            intent.putExtra(IntentActions.INTENT_SEND_STRING_FLIGHT, flightName);
            intent.putExtra(IntentActions.INTENT_SEND_FLIGHT_COLUMN_ID, columnId);


            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            //Get calendar instance
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

            //get hour
            if (timeObject.getHour()!= -1) {
                int hour = timeObject.getHour();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
            }

            //get minute
            if (timeObject.getMinute()!=-1){
                int minute = timeObject.getMinute();
                calendar.set(Calendar.MINUTE, minute);
            }

            calendar.set(Calendar.SECOND, 0);
            Calendar newCalendar = DataCheckingUtils.adjustAlarmTime(calendar);

            assert alarmManager != null;

            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    newCalendar.getTimeInMillis(),
                    pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static String getStatus(String string) {
        if (string.contains("Landed")) {
            return "Landed";
        } else if (string.contains("En Route")) {
            return "En Route";
        } else if (string.contains("Cancelled")) {
            return "Cancelled";
        } else if (string.contains("Scheduled")) {
            return "Scheduled";
        } else if (string.contains("Canceled")){
            return "Cancelled";
        }
        else {
            return "error";
        }
    }

    private static String getDay(String string){

        String day ="";
        if (string.contains("Monday")){
            day = "Monday";
        }else if (string.contains("Tuesday")){
            day = "Tuesday";
        }else if (string.contains("Wednesday")){
            day = "Wednesday";
        }else if (string.contains("Thursday")){
            day = "Thursday";
        }else if (string.contains("Friday")){
            day = "Friday";
        }else if (string.contains("Saturday")){
            day = "Saturday";
        }else if (string.contains("Sunday")){
            day = "Sunday";
        }
        return day;
    }


}
