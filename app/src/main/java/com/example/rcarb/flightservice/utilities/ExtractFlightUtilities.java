package com.example.rcarb.flightservice.utilities;

import com.example.rcarb.flightservice.R;
import com.example.rcarb.flightservice.objects.FlightObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by rcarb on 2/20/2018.
 */

public class ExtractFlightUtilities {

    public static String getFlightInfoFromWeb(String uri) {
        String resString = "";
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(uri);//website + string1 + string2
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

        return resString;
    }

    public static FlightObject saveFlightStringToObject(String flightString) {

        FlightObject flight = new FlightObject();

        if (!flightString.contains("/airlines/")){
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

        int startIndex = flightString.indexOf("/airlines/");
        int nextIndex = -1;
        if ( flightString.indexOf("/airlines/", startIndex + 10)<0){
            nextIndex = flightString.indexOf("<!--- END Arrivals --->", startIndex + 10);
            flight.setIsLastFlight(true);


        }else if (flightString.indexOf("/airlines/", startIndex + 10)>-1){
            nextIndex = flightString.indexOf("/airlines/", startIndex + 10);
        }
        String currentSubstring =  flightString.substring(startIndex, nextIndex);

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

        if (fligthName.contains("&date")){
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
        if (!DataCheckingUtils.isNumber(scheduledTimeString)){
            scheduledTimeString = "-2";
        }
        int scheduledTime = Integer.valueOf(scheduledTimeString);
        flight.setFlightScheduledTime(scheduledTime);

        //get Actual
        int indexActual = currentSubstring.indexOf(":", indexScheduled + 5);
        String actualTimeString = currentSubstring.substring(indexActual - 2, indexActual + 3);
        actualTimeString = actualTimeString.replace(":", "");
        if (!DataCheckingUtils.isNumber(actualTimeString)){
            actualTimeString = "-2";
        }
        int actualTime = Integer.valueOf(actualTimeString);
        flight.setActualArrivalTime(actualTime);

        //get Status
        String statusString = ExtractFlightUtilities.getStatus(currentSubstring);
        flight.setFlightStatus(statusString);

        //Set next flight index
        flight.setNextFlightIndex(nextIndex-1);
        flight.setParsedString(flightString);

        return flight;
    }
    public static FlightObject saveFlightStringToObject(String flightString,
                                                        int startAtIndex) {

        if (startAtIndex <0){
            String a ="";
        }

        FlightObject flight = new FlightObject();

        int startIndex = startAtIndex;
        int nextIndex = -1;
        if ( flightString.indexOf("/airlines/", startIndex + 10)<0){
            nextIndex = flightString.indexOf("<!--- END Arrivals --->", startIndex + 10);
            flight.setIsLastFlight(true);


        }else if (flightString.indexOf("/airlines/", startIndex + 10)>-1){
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

        if (fligthName.contains("&date")){
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
        if (!DataCheckingUtils.isNumber(scheduledTimeString)){
            scheduledTimeString = "-2";
        }
        int scheduledTime = Integer.valueOf(scheduledTimeString);
        flight.setFlightScheduledTime(scheduledTime);

        //get Actual
        int indexActual = currentSubstring.indexOf(":", indexScheduled + 5);
        String actualTimeString = currentSubstring.substring(indexActual - 2, indexActual + 3);
        actualTimeString = actualTimeString.replace(":", "");
        if (!DataCheckingUtils.isNumber(actualTimeString)){
            actualTimeString = "-2";
        }
        int actualTime = Integer.valueOf(actualTimeString);
        flight.setActualArrivalTime(actualTime);

        //get Status
        String statusString = ExtractFlightUtilities.getStatus(currentSubstring);
        flight.setFlightStatus(statusString);

        //Set next flight index
        flight.setNextFlightIndex(nextIndex-1);
        flight.setParsedString(flightString);

        return flight;
    }

    private static String getStatus(String string) {

        if (string.contains("Landed")){
            return "Landed";
        }else if (string.contains("En Route")){
            return "En Route";
        }else if (string.contains("Cancelled")){
            return "Cancelled";
        }else if (string.contains("Scheduled")){
            return "Scheduled";
        }else{
            return "error";
        }


    }
}
