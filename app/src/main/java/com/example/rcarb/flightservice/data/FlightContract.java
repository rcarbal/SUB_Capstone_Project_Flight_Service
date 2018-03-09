package com.example.rcarb.flightservice.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.example.rcarb.flightservice.utilities.ExtractFlightUtilities;

/**
 * Created by rcarb on 2/19/2018.
 */

public class FlightContract {

    //Authority for the uri matcher.
    public static final String AUTHORITY = "com.example.rcarb.flightservice";

    //String for accessing the favorite table.
    public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FLIGHTS = "Flight_service";
    public static final String PATH_LIST_DATABASE = "List_database";

    public static final int INVALID_FLIGHT_ENTRY = -1;

    public static class FlightEntry implements BaseColumns {
        //Uri that points to the Flights table
        public static final Uri BASE_CONTENT_URI_FLIGTHS=
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FLIGHTS).build();
        //Uri that points to the list databse table.
        public static final Uri BASE_CONTENT_URI_LIST_DATABSE =
                BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_LIST_DATABASE).build();



        //Single day constants.
        public static final String TABLE_NAME_DAY_FLIGHTS= "Flights";//+ ExtractFlightUtilities.getDate();
        public static final String COLUMN_FLIGHT_NUMBER = "Flight_Number";
        public static final String COLUMN_FLIGHT_SCHEDULE = "Sheduled_Time";
        public static final String COLUMN_FLIGHT_TIME_ACTUAL = "Actual_time";
        public static final String COLUMN_FLIGHT_GATE= "Gate";
        public static final String COLUMN_FLIGHT_AIRLINE = "Airline";
        public static final String COLUMN_FLIGHT_STATUS = "Status";
        public static final String COLUMN_FLIGHT_DATE = "Date";
        public static final String COLUMN_FLIGHT_ALARM_SET ="Alarm_Set";


        //Constants for to keep track of current database
        public static final String TABLE_DATABASES_NAME = "List_of_databses";
        public static final String COLUMN_DATABASE_STATUS= "Status";


        //Create SQL tables Strings.
        //String for Flight table.
        public final static String SQL_CREATE_FLIGHT_ENTRIES =
                "CREATE TABLE " + FlightEntry.TABLE_NAME_DAY_FLIGHTS + " (" +
                        FlightEntry._ID + " INTEGER PRIMARY KEY, " +
                        FlightEntry.COLUMN_FLIGHT_DATE + " TEXT NOT NULL, "+
                        FlightEntry.COLUMN_FLIGHT_NUMBER + " TEXT NOT NULL, " +
                        FlightEntry.COLUMN_FLIGHT_AIRLINE + " TEXT NOT NULL, " +
                        FlightEntry.COLUMN_FLIGHT_GATE + " INTEGER NOT NULL, " +
                        FlightEntry.COLUMN_FLIGHT_SCHEDULE + " INTEGER NOT NULL, " +
                        FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL+ " INTEGER NOT NULL, " +
                        FlightEntry.COLUMN_FLIGHT_ALARM_SET+ " INTEGER NOT NULL, "+
                        FlightEntry.COLUMN_FLIGHT_STATUS + " TEXT NOT NULL)";

        public final static String SQL_CREATE_LIST_OF_DATABASES =
                                "CREATE TABLE " + FlightEntry.TABLE_DATABASES_NAME + " (" +
        FlightEntry._ID + " INTEGER PRIMARY KEY, " +
        FlightEntry.COLUMN_DATABASE_STATUS + " TEXT NOT NULL)";

        //Deletes the flight names databse
        public static final String SQL_DELETE_FLIGHT_ENTRIES =
                "DROP TABLE IF EXISTS " + FlightEntry.TABLE_NAME_DAY_FLIGHTS;
        //Delete the list databse
        public static final String SQL_DELETE_LIST_DATABASE =
                "DROP TABLE IF EXISTS " + FlightEntry.TABLE_NAME_DAY_FLIGHTS;
    }
}

