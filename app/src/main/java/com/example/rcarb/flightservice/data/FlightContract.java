package com.example.rcarb.flightservice.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by rcarb on 2/19/2018.
 */

public class FlightContract {

    //Authority for the uri matcher.
    public static final String AUTHORITY = "com.example.rcarb.flightservice";

    //String for accessing the favorite table.
    public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FLIGHTS = "Flight_service";

    public static final int INVALID_FLIGHT_ENTRY = -1;

    public static class FlightEntry implements BaseColumns {
        //Urii that ponts to the favorites table.
        public static final Uri BASE_CONTENT_URI_FLIGTHS=
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FLIGHTS).build();

        public static final String TABLE_NAME = "Flights";
        public static final String COLUMN_FLIGHT_NUMBER = "Flight#";
        public static final String COLUMN_FLIGHT_STATUS = "Status";
        public static final String COLUMN_FLIGHT_SCHEDULE = "Sheduled_Time";
        public static final String COLUMN_FLIGHT_TIME_ACTUAL = "Actual_time";
        public static final String COLUMN_FLIGHT_GATE= "Gate";
        public static final String COLUMN_FLIGHT_AIRLINE = "Airline";

        //Create SQL tables Strings.
        //String for Flight table.
        public final static String SQL_CREATE_RECIPE_ENTRIES =
                "CREATE TABLE " + FlightEntry.TABLE_NAME + " (" +
                        FlightEntry._ID + " INTEGER PRIMARY KEY, " +
                        FlightEntry.COLUMN_FLIGHT_NUMBER + " TEXT NOT NULL, " +
                        FlightEntry.COLUMN_FLIGHT_STATUS + " TEXT NOT NULL, " +
                        FlightEntry.COLUMN_FLIGHT_SCHEDULE + " INTEGER NOT NULL, " +
                        FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL + " TEXT NOT NULL, " +
                        FlightEntry.COLUMN_FLIGHT_GATE + " TEXT NOT NULL" +
                        FlightEntry.COLUMN_FLIGHT_AIRLINE + " TEXT NOT NULL)";

        //Deletes the recipe databse
        public static final String SQL_DELETE_FLIGHT_ENTRIES =
                "DROP TABLE IF EXISTS " + FlightEntry.TABLE_NAME;
    }
}

