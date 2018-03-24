package com.example.rcarb.flightservice.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.util.Log;

import com.example.rcarb.flightservice.data.FlightContract;
import com.example.rcarb.flightservice.objects.CompareFlightObject;
import com.example.rcarb.flightservice.objects.FlightObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by rcarb on 2/20/2018.
 */

public class FlightExtractionTasks {
    //Task for extracting flight information.
    public static final String ACTION_EXTRACT_SINGLE_FLIGHT_INFORMATION = "extract-single-flight";
    public static final String ACTION_EXTRACT_FLIGHTS_NO_ALARM = "single-parse-re-aprese";
    public static final String SAVE_FLIGHTS_TO_DATABASE = "save-flights";
    public static final String ACTION_DATABASE_UPDATE_STATUS = "database-update-status";
    public static final String ACTION_GET_STATUS_OF_FLIGHT = "get-status-of-flight";
    public static final String ACTION_EXTRACT_SINGLE_FLIGHT_INFO_FOR_SECOND_ALARM = "get-second-status";
    public static final String ACTION_UPDATE_ALARMS_REQUEST_CODE = "update-alarms-request-code-databse";

    //Job Dispatcher
    public static final String ACTION_GET_FLIGHTS_TO_UPDATE = "get-flights-for-update";

    public static final String SETUP_ALARM_FOR_FLIGHT = "setup-alarms";
    public static final String INTENT_SEND_STRING = "send-string";
    public static final String ACTION_SAVE_FLIGHT_TABLE = "save-table";

    public static void executeTask(String action,
                                   ArrayList<FlightObject> arrayList,
                                   Context context,
                                   long id,
                                   String flightName,
                                   String flightStatus,
                                   FlightObject flightObject,
                                   String initial,
                                   int requestCode,
                                   int scheduleTime) {

        String initialCheck = initial;
        if (ACTION_EXTRACT_SINGLE_FLIGHT_INFORMATION.equals(action)) {
            extractSingleFlight(context, id, flightName, flightStatus, requestCode, scheduleTime);
        } else if (ACTION_EXTRACT_FLIGHTS_NO_ALARM.equals(action)) {
            getFlightsExcludedLandedCancelledWithNoAlarm(context, initial);
        } else if (SAVE_FLIGHTS_TO_DATABASE.equals(action)) {
            saveFlightsTodatabase(arrayList, context, initial);
        } else if (ACTION_GET_FLIGHTS_TO_UPDATE.equals(action)) {
            getFlightsExcludedLandedCancelled(context, initial);
        } else if (SETUP_ALARM_FOR_FLIGHT.equals(action)) {
            //setupAlarmSingle(arrayList, context);
            setupAlarmToFlight(arrayList, context);
        } else if (ACTION_DATABASE_UPDATE_STATUS.equals(action)) {
            updateDatabaseFlightStatus(context, flightObject);
        } else if (ACTION_EXTRACT_SINGLE_FLIGHT_INFO_FOR_SECOND_ALARM.equals(action)) {
            extratSingleFlightSecondAlarm(context, id, flightName, requestCode);
        } else if (ACTION_UPDATE_ALARMS_REQUEST_CODE.equals(action)) {
            setAlarmRequestCode(arrayList, context);
        }
    }


    private static void getUpdateFOrFlightStatus(long id, String flightName) {
    }


    private static void saveFlightsTodatabase(ArrayList<FlightObject> arrayList, Context context, String initial) {
        if (initial.equals(IntentActions.INTENT_INITIAL)) {
            if (arrayList != null) {

                int numberOfSavedFlights = 0;
                for (int i = 0; i < arrayList.size(); i++) {
                    FlightObject flight = arrayList.get(i);

                    ContentValues cv = new ContentValues();
                    cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_DATE, ExtractFlightUtilities.getDate());
                    cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_NUMBER, flight.getFlightName());
                    cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_GATE, flight.getGate());
                    cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_AIRLINE, flight.getAirline());
                    cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_SCHEDULE, flight.getFlightScheduledTime());
                    cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL, flight.getActualArrivalTime());
                    cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_STATUS, flight.getFlightStatus());
                    cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_ALARM_SET, flight.getAlarm());

                    Uri flightUri = context.getContentResolver().insert(FlightContract.FlightEntry.
                            BASE_CONTENT_URI_FLIGTHS, cv);


                    if (flightUri != null) {
                        numberOfSavedFlights++;
                    }
                }

                if (numberOfSavedFlights > 0) {
//                Intent sendToast = new Intent(IntentActions.INTENT_SEND_TOAST);
//                sendToast.putExtra(IntentActions.INTENT_SEND_STRING, "Database saved");
//                context.sendBroadcast(sendToast);
                    Intent databaseFailedIntent = new Intent(IntentActions.DATABASE_FLIGHT_INSERTED_SUCCESS);
                    databaseFailedIntent.putExtra(IntentActions.INTENT_SEND_ALARM_PASS_LABEL, IntentActions.INTENT_INITIAL);
                    context.sendBroadcast(databaseFailedIntent);
                } else if (numberOfSavedFlights == 0) {
                    Log.e("Recipe insert", "Uri insert unssuccessful");
                    Intent databaseFailedIntent = new Intent(IntentActions.DATABASE_FLIGHT_INSERTED_FAILURE);
                    databaseFailedIntent.putExtra(IntentActions.INTENT_SEND_ALARM_PASS_LABEL, IntentActions.INTENT_INITIAL);
                    context.sendBroadcast(databaseFailedIntent);

                }
            }


            //Saves the flights that are not currently found in the database.
        } else if (initial.equals("")) {

            if (arrayList != null) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);


                ArrayList<CompareFlightObject> compare = getFlightsComapare(context);

                int numberOfSavedFlights = 0;
                for (int i = 0; i < arrayList.size(); i++) {
                    FlightObject flight = arrayList.get(i);

                    //get FlightName
                    String flightName = flight.getFlightName();
                    boolean compareNames = checkFlightName(flightName, compare);
                    if (!compareNames) {

                        ContentValues cv = new ContentValues();
                        cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_DATE, ExtractFlightUtilities.getDate());
                        cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_NUMBER, flight.getFlightName());
                        cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_GATE, flight.getGate());
                        cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_AIRLINE, flight.getAirline());
                        cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_SCHEDULE, flight.getFlightScheduledTime());
                        cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL, flight.getActualArrivalTime());
                        cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_STATUS, flight.getFlightStatus());
                        cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_ALARM_SET, flight.getAlarm());

                        Uri flightUri = context.getContentResolver().insert(FlightContract.FlightEntry.
                                BASE_CONTENT_URI_FLIGTHS, cv);


                        if (flightUri != null) {
                            numberOfSavedFlights++;
                        }
                    }
                }

                //Send broadcast
                if (numberOfSavedFlights > 0) {

                    Intent databaseFailedIntent = new Intent(IntentActions.ACTION_NOT_INITIAL_DATABASE_SAVE_SUCCESSFUL);
                    databaseFailedIntent.putExtra(IntentActions.INTENT_SENDING_NEW_FLIGHTS_DATABASE, numberOfSavedFlights);
                    context.sendBroadcast(databaseFailedIntent);
                } else if (numberOfSavedFlights == 0) {
                    Log.e("Recipe insert", "Uri insert unssuccessful");
                    Intent databaseFailedIntent = new Intent(IntentActions.DATABASE_FLIGHT_INSERTED_FAILURE);
                    context.sendBroadcast(databaseFailedIntent);
                }
            }

        } else if (initial.equals(IntentActions.ACTION_INTENT_PARSE_CONCATINATE)) {
            if (arrayList != null) {
                //Get instance of time
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int convertedCalendar = DataCheckingUtils.converCalendarToInt(calendar, 0, 0, false);

                //Create the arraylist that willbe used to store all the flights
                ArrayList<FlightObject> mergeArray = new ArrayList<>();

                //{arse through all the flights
                for (int i = 0; i < arrayList.size(); i++) {
                    FlightObject object = arrayList.get(i);

                    //Variables that will be used to compare.
                    int time = -2;
                    int scheduled = object.getFlightScheduledTime();
                    int actual = object.getActualArrivalTime();

                    //Time to compare set
                    if (actual == -2) {
                        time = scheduled;
                    } else {
                        time = actual;
                    }
                    //if the arrival/schedule time is greater than the instance
                    if (time > convertedCalendar) {
                        mergeArray.add(object);
                        //If the time is less than the instance.
                    } else if (time < convertedCalendar) {
                        if (time < 460) {
                            if (scheduled != -2) {
                                int scheduleMilitary = DataCheckingUtils.convertPassedMidnight(scheduled);
                                object.setFlightScheduledTime(scheduleMilitary);
                            }
                            if (actual != -2) {
                                int actualMilitary = DataCheckingUtils.convertPassedMidnight(actual);
                                object.setActualArrivalTime(actualMilitary);
                            }
                            mergeArray.add(object);
                        }
                    }
                }

                //save to database
                //Add the flights to the database
                int numberOfSavedFlights = 0;
                for (int i = 0; i < mergeArray.size(); i++) {

                    FlightObject flight = arrayList.get(i);

                    ContentValues cv = new ContentValues();
                    cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_DATE, ExtractFlightUtilities.getDate());
                    cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_NUMBER, flight.getFlightName());
                    cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_GATE, flight.getGate());
                    cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_AIRLINE, flight.getAirline());
                    cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_SCHEDULE, flight.getFlightScheduledTime());
                    cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL, flight.getActualArrivalTime());
                    cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_STATUS, flight.getFlightStatus());
                    cv.put(FlightContract.FlightEntry.COLUMN_FLIGHT_ALARM_SET, flight.getAlarm());

                    Uri flightUri = context.getContentResolver().insert(FlightContract.FlightEntry.
                            BASE_CONTENT_URI_FLIGTHS, cv);
                    if (flightUri != null) {
                        numberOfSavedFlights++;
                    }
                }
                //Send broadcast
                if (numberOfSavedFlights > 0) {


                    Intent databaseFailedIntent = new Intent(IntentActions.ACTION_NOT_INITIAL_DATABASE_SAVE_SUCCESSFUL);
                    databaseFailedIntent.putExtra(IntentActions.INTENT_SENDING_NEW_FLIGHTS_DATABASE, numberOfSavedFlights);
                    context.sendBroadcast(databaseFailedIntent);
                } else if (numberOfSavedFlights == 0) {
                    Log.e("Recipe insert", "Uri insert unssuccessful");
                    Intent databaseFailedIntent = new Intent(IntentActions.DATABASE_FLIGHT_INSERTED_FAILURE);
                    context.sendBroadcast(databaseFailedIntent);

                }
            }
        }
    }


    private static ArrayList<CompareFlightObject> getFlightsComapare(Context context) {
        String[] projection = {FlightContract.FlightEntry.COLUMN_FLIGHT_NUMBER,
                FlightContract.FlightEntry.COLUMN_FLIGHT_DATE};


        Cursor cursor = context.getContentResolver().query(FlightContract.FlightEntry.BASE_CONTENT_URI_FLIGTHS,
                null,
                null,
                null,
                null);

        ArrayList<CompareFlightObject> array = new ArrayList<>();
        String dump = DatabaseUtils.dumpCursorToString(cursor);

        //loop and make compare object
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    CompareFlightObject compare = new CompareFlightObject();
                    compare.setFlight(cursor.getString(cursor.getColumnIndex(
                            FlightContract.FlightEntry.COLUMN_FLIGHT_NUMBER)));
                    compare.setDate(cursor.getString(cursor.getColumnIndex(
                            FlightContract.FlightEntry.COLUMN_FLIGHT_DATE)));
                    array.add(compare);
                } while (cursor.moveToNext());
            }

        }
        assert cursor != null;
        cursor.close();
        return array;
    }

    private static boolean checkFlightName(String flightName, ArrayList<CompareFlightObject> arrayList) {
        boolean flightFound = false;
        for (int i = 0; i < arrayList.size(); i++) {
            CompareFlightObject compareFlightObject = arrayList.get(i);
            String compareName = compareFlightObject.getFlight();
            if (compareName.equals(flightName)) {
                flightFound = true;
            }
        }
        return flightFound;
    }

    private static void getFlightsExcludedLandedCancelled(Context context, String initial) {
        if (initial.equals(IntentActions.INTENT_INITIAL)) {
            String selection = FlightContract.FlightEntry.COLUMN_FLIGHT_STATUS + "!= ? and " +
                    FlightContract.FlightEntry.COLUMN_FLIGHT_STATUS + "!= ?";
            String[] selectionArgs = {"Landed", "Cancelled"};
            String sortOrder = FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL;

            Cursor cursor = context.getContentResolver().query(FlightContract.FlightEntry.BASE_CONTENT_URI_FLIGTHS,
                    null,
                    selection,
                    selectionArgs,
                    sortOrder);

            ArrayList<FlightObject> flightsArray = new ArrayList<>();
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        FlightObject object = new FlightObject();
                        object.setColumnId(cursor.getLong(cursor.getColumnIndex(
                                FlightContract.FlightEntry._ID)));
                        object.setDate(cursor.getInt(cursor.getColumnIndex(
                                FlightContract.FlightEntry.COLUMN_FLIGHT_DATE)));
                        object.setFlightName(cursor.getString(cursor.getColumnIndex(
                                FlightContract.FlightEntry.COLUMN_FLIGHT_NUMBER)));
                        object.setAirline(cursor.getString(cursor.getColumnIndex(
                                FlightContract.FlightEntry.COLUMN_FLIGHT_AIRLINE)));
                        object.setGate(cursor.getInt(cursor.getColumnIndex(
                                FlightContract.FlightEntry.COLUMN_FLIGHT_GATE)));
                        object.setFlightScheduledTime(cursor.getInt(cursor.getColumnIndex(
                                FlightContract.FlightEntry.COLUMN_FLIGHT_SCHEDULE)));
                        object.setActualArrivalTime(cursor.getInt(cursor.getColumnIndex(
                                FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL)));
                        object.setFlightStatus(cursor.getString(cursor.getColumnIndex(
                                FlightContract.FlightEntry.COLUMN_FLIGHT_STATUS)));
                        flightsArray.add(object);
                    } while (cursor.moveToNext());
                }
            }
            String dump = DatabaseUtils.dumpCursorToString(cursor);
            if (cursor != null) cursor.close();


            Intent databaseReadyIntent = new Intent(IntentActions.FLIGHT_ARRAYLIST_FOR_UPDATE_EXTRACTED);
            databaseReadyIntent.putParcelableArrayListExtra(IntentActions.PUT_EXTRA_PARCEL_ARRAY,
                    flightsArray);
            databaseReadyIntent.putExtra(IntentActions.INTENT_SEND_ALARM_PASS_LABEL, IntentActions.INTENT_INITIAL);
            context.sendBroadcast(databaseReadyIntent);
        }
    }

    private static void getFlightsExcludedLandedCancelledWithNoAlarm(Context context, String initial) {
        if (initial.equals("")) {
            String selection = FlightContract.FlightEntry.COLUMN_FLIGHT_STATUS + "!= ? and " +
                    FlightContract.FlightEntry.COLUMN_FLIGHT_STATUS + "!= ? and " +
                    FlightContract.FlightEntry.COLUMN_FLIGHT_ALARM_SET + "= ?";
            String[] selectionArgs = {"Landed", "Cancelled", "-2"};
            String sortOrder = FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL;

            Cursor cursor = context.getContentResolver().query(FlightContract.FlightEntry.BASE_CONTENT_URI_FLIGTHS,
                    null,
                    selection,
                    selectionArgs,
                    sortOrder);

            ArrayList<FlightObject> flightsArray = new ArrayList<>();
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        FlightObject object = new FlightObject();
                        object.setColumnId(cursor.getLong(cursor.getColumnIndex(
                                FlightContract.FlightEntry._ID)));
                        object.setDate(cursor.getInt(cursor.getColumnIndex(
                                FlightContract.FlightEntry.COLUMN_FLIGHT_DATE)));
                        object.setFlightName(cursor.getString(cursor.getColumnIndex(
                                FlightContract.FlightEntry.COLUMN_FLIGHT_NUMBER)));
                        object.setAirline(cursor.getString(cursor.getColumnIndex(
                                FlightContract.FlightEntry.COLUMN_FLIGHT_AIRLINE)));
                        object.setGate(cursor.getInt(cursor.getColumnIndex(
                                FlightContract.FlightEntry.COLUMN_FLIGHT_GATE)));
                        object.setFlightScheduledTime(cursor.getInt(cursor.getColumnIndex(
                                FlightContract.FlightEntry.COLUMN_FLIGHT_SCHEDULE)));
                        object.setActualArrivalTime(cursor.getInt(cursor.getColumnIndex(
                                FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL)));
                        object.setFlightStatus(cursor.getString(cursor.getColumnIndex(
                                FlightContract.FlightEntry.COLUMN_FLIGHT_STATUS)));
                        flightsArray.add(object);
                    } while (cursor.moveToNext());
                }
            }
            String dump = DatabaseUtils.dumpCursorToString(cursor);
            if (cursor != null) cursor.close();


            Intent databaseReadyIntent = new Intent(IntentActions.FLIGHT_ARRAYLIST_FOR_UPDATE_EXTRACTED);
            databaseReadyIntent.putParcelableArrayListExtra(IntentActions.PUT_EXTRA_PARCEL_ARRAY,
                    flightsArray);
            databaseReadyIntent.putExtra(IntentActions.INTENT_SEND_ALARM_PASS_LABEL, IntentActions.INTENT_INITIAL);
            context.sendBroadcast(databaseReadyIntent);
        }
    }

    //setup alarm for re-udated flight:
    public static void setupReUpdateForAlarm(FlightObject object, Context context) {
        String flight = object.getFlightName();
        int scheduled = object.getFlightScheduledTime();
        int actual = object.getActualArrivalTime();
        long id = object.getColumnId();

        boolean alarmSet = ExtractFlightUtilities.setupAlarm(
                flight,
                id,
                actual,
                1000,
                context
        );
    }

    public static void setupAlarmSingle(FlightObject flight, Context context, int time) {
        int numberOfAlarms = 0;
        int requestCode = 4;
        FlightObject flightObject = flight;
        String name = flightObject.getFlightName();
        long flightColumnId = flightObject.getColumnId();
        //int flightActualTime = flight.getActualArrivalTime();
        int flightActualTime = time;
        int scheduledTime = flightObject.getFlightScheduledTime();
        int timeToParse = -2;
        if (flightActualTime == -2) {
            timeToParse = scheduledTime;
        } else {
            timeToParse = flightActualTime;
        }
        boolean alarmSet = ExtractFlightUtilities.setupAlarm(
                name,
                flightColumnId,
                timeToParse,
                requestCode,
                context);

        if (alarmSet) {
            numberOfAlarms++;
            requestCode++;
        }
        Intent intent = new Intent(IntentActions.ALARM_SET);
        intent.putExtra(IntentActions.INTENT_SEND_INT, numberOfAlarms);
        context.sendBroadcast(intent);

    }

    private static void setupAlarmToFlight(ArrayList<FlightObject> flightArray, Context context) {
        int times = 1750;


        int numberOfAlarms = 0;
        int requestCode = 0;

        for (int i = 0; i < 3; i++) {
            FlightObject flight = flightArray.get(i);
            String name = flight.getFlightName();
            long flightColumnId = flight.getColumnId();
            int flightActualTime = flight.getActualArrivalTime();
            int scheduledTime = flight.getFlightScheduledTime();
            int timeToParse = -2;
            if (flightActualTime == -2) {
//                timeToParse = scheduledTime;
                timeToParse = times;
            } else {
//                timeToParse = flightActualTime;
                timeToParse = times;
            }
            boolean alarmSet = ExtractFlightUtilities.setupAlarm(
                    name,
                    flightColumnId,
                    timeToParse,
                    requestCode,
                    context);

            if (alarmSet) {
                numberOfAlarms++;
                requestCode++;
                times++;
            }
        }
        Intent intent = new Intent(IntentActions.ALARM_SET);
        intent.putExtra(IntentActions.INTENT_SEND_INT, numberOfAlarms);
        context.sendBroadcast(intent);
    }


    private static void updateDatabaseFlightStatus(Context context, FlightObject object) {
        //Parse LAX site for update of flight.

        int scheduled = object.getFlightScheduledTime();
        int actual = object.getActualArrivalTime();
        long id = object.getColumnId();
        String status = object.getPostStatus();

        ContentValues contentValues = new ContentValues();
        contentValues.put(FlightContract.FlightEntry._ID, id);
        contentValues.put(FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL, actual);
        contentValues.put(FlightContract.FlightEntry.COLUMN_FLIGHT_STATUS, status);


        //Criteria of the rows you want to update.
        String flightId = Long.toString(id);


        //Uri that will be updated
        Uri uri = FlightContract.FlightEntry.BASE_CONTENT_URI_FLIGTHS;
        uri = uri.buildUpon().appendPath(flightId).build();

        int flightUpdate = context.getContentResolver().update(
                uri,
                contentValues,
                null,
                null);

        if (flightUpdate > 0) {
            Intent intent = new Intent(IntentActions.INTENT_SEND_TOAST);
            intent.putExtra(IntentActions.INTENT_SEND_TOAST_MESSAGE, "Flight update successful.");
            context.sendBroadcast(intent);

        }
    }

    //update the list of flights that have an alarm already set.
    private static void setAlarmRequestCode(ArrayList<FlightObject> arrayList, Context context) {
        if (arrayList != null) {

            int numberOfAlarms = 0;
            for (int i = 0; i < arrayList.size(); i++) {
                ContentValues contentValues = new ContentValues();

                FlightObject flightObject = arrayList.get(i);
                long id = flightObject.getColumnId();
                int requestCode = flightObject.getAlarm();

                contentValues.put(FlightContract.FlightEntry.COLUMN_FLIGHT_ALARM_SET, requestCode);
                //Criteria of the rows you want to update.
                String flightId = Long.toString(id);

                //Uri that will be updated
                Uri uri = FlightContract.FlightEntry.BASE_CONTENT_URI_FLIGTHS;
                uri = uri.buildUpon().appendPath(flightId).build();

                int flightUpdate = context.getContentResolver().update(
                        uri,
                        contentValues,
                        null,
                        null);
                if (flightUpdate > 0) {

                    numberOfAlarms++;
                }
            }
            if (numberOfAlarms == arrayList.size()) {
                Intent intent = new Intent(IntentActions.ACTION_CURRENT_ARRAYLIST_REQUEST_CODES_COMPLETED);
                context.sendBroadcast(intent);
            }
        }
    }

    //Method to extract flight
    private static void extractSingleFlight(Context context, long id, String flight, String status,
                                            int extractTime, int scheduleTime) {
        FlightObject main = new FlightObject();
        main.setFlightStatus(status);
        main.setColumnId(id);
        main.setFlightName(flight);
        String currentStatus = status;
        String flightName = flight;
        int scheduleTimeExt = scheduleTime;
        int timeTOExtract = extractTime;


        String site = "https://www.airport-la.com/lax/flight?flight_arrival=" + flight;
        String parseString = ExtractFlightUtilities.getFlightInfoFromWeb(site);
        FlightObject firstCompare = ExtractFlightUtilities.getInfoForSIngleFlightSite1(parseString);

        firstCompare.setFlightName(flight);
        firstCompare.setColumnId(id);
        int arrival = firstCompare.getActualArrivalTime();

        String postStatus = firstCompare.getPostStatus();
        boolean parseSecondSite = false;
        if (postStatus.equals(status)) {
            parseSecondSite = true;
        }

        FlightObject secondObject = null;

        if (parseSecondSite) {
            String laxOfficial = "https://www.flylax.com/en/flight-info?fn=" + flight + "&type=arr";
            String secondObjectParse = ExtractFlightUtilities.getFlightInfoFromWeb(laxOfficial);
            secondObject = ExtractFlightUtilities.getInfoForSingleFlightsSite2(secondObjectParse);
        }
        boolean setupSecondAlarm = false;
        boolean passedDalyed = false;
        boolean cancelFlight = false;
        boolean nextDay = false;
        int secondTIme = -2;
        if (secondObject != null) {
            secondTIme = secondObject.getActualArrivalTime();
        }
        main.setPostStatus(postStatus);
        main.setActualArrivalTime(secondTIme);
        //Current time
        Calendar calendar = Calendar.getInstance();
        int convertedCalendar = DataCheckingUtils.converCalendarToInt(calendar, 0, 0, false);
        int firstDelay = timeTOExtract - convertedCalendar;


//        String flightStatus = postStatus;
//
//        switch (flightStatus) {
//            case "Scheduled":
//                setupSecondAlarm = true;
//                break;
//            case "En Route":
//                setupSecondAlarm = true;
//                break;
//            case "error":
//                setupSecondAlarm = true;
//                break;
//        }
        //If flight time is less than current time set alarm.
        int delaySaved = -2;
        if (secondTIme != -2) {
            if (convertedCalendar >= secondTIme) {
                //If delay is less than 4 hours.
                int delay = secondTIme - convertedCalendar;
                delaySaved = delay;
                if (delay > -400) {
                    //set alarm.
                    setupSecondAlarm = true;

                } else {
                    cancelFlight = true;
                    main.setPostStatus("unconfirmed");
                }
            }
        } else if (convertedCalendar <= secondTIme) {
            //flight hasn't landed
            setupSecondAlarm = true;

        }

        //if its passed 2000
        if (scheduleTime > 2200) {
            if (timeTOExtract < 60) {
                setupSecondAlarm = true;
                nextDay = true;
            }
        }


        //Check if alarm needs to be set.
        if (setupSecondAlarm) {
            //setup second alarm.
            Intent intent = new Intent(IntentActions.ACTION_SETUP_SECOND_ALARM);
            intent.putExtra(IntentActions.INTENT_SEND_PARCEL_FOR_SECOND_ALARM, main);
            intent.putExtra(IntentActions.INTENT_SEND_BOOLEAN, nextDay);
            context.sendBroadcast(intent);
        }
        //Update the database with the new information
        Intent intent = new Intent();
        intent.setAction(IntentActions.FLIGHT_UPDATE_DATABASE_STATUS);
        intent.putExtra(IntentActions.INTENT_SEND_PARCEL, main);
        context.sendBroadcast(intent);
        Intent completeFirstAlarm = new Intent(IntentActions.ACTION_FIRST_ALARM_COMPLETE);
        context.sendBroadcast(completeFirstAlarm);
    }


    //Second alarm update
    private static void extratSingleFlightSecondAlarm(Context context, long id, String flight, int requestCode) {
        String site = "https://www.airport-la.com/lax/flight?flight_arrival=" + flight;
        String parseString = ExtractFlightUtilities.getFlightInfoFromWeb(site);
        FlightObject flightUpdated = ExtractFlightUtilities.getInfoForSIngleFlightSite1(parseString);
        flightUpdated.setFlightName(flight);
        flightUpdated.setColumnId(id);

        String status = flightUpdated.getPostStatus();

        boolean shouldCancel = false;

        switch (status) {
            case "Landed":
                shouldCancel = true;
                break;
            case "Cancelled":
                shouldCancel = true;
                break;
            case "null":
                shouldCancel = true;
                break;
        }

        //Check if alarm needs to be set.
        if (shouldCancel) {
            //cancel alarm
            Intent cancelAlarm = new Intent(IntentActions.ACTION_INTENT_CANCEL_ALARM);
            cancelAlarm.putExtra(IntentActions.INTENT_SEND_STRING_FLIGHT, flight);
            cancelAlarm.putExtra(IntentActions.INTENT_REQUEST_CODE, requestCode);
            context.sendBroadcast(cancelAlarm);

        }
        //Update the database with the new information
        Intent intent = new Intent();
        intent.setAction(IntentActions.FLIGHT_UPDATE_DATABASE_STATUS);
        intent.putExtra(IntentActions.INTENT_SEND_PARCEL, flightUpdated);
        context.sendBroadcast(intent);

//        //Update the second alarm count
//        Intent intentTwo = new Intent(IntentActions.ACTION_SECOND_ALARM_COMPLETE);
//        context.sendBroadcast(intentTwo);
    }
}
