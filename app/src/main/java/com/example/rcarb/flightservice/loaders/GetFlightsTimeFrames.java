package com.example.rcarb.flightservice.loaders;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.example.rcarb.flightservice.data.FlightContract;
import com.example.rcarb.flightservice.objects.FlightObject;
import com.example.rcarb.flightservice.utilities.DataCheckingUtils;
import com.example.rcarb.flightservice.utilities.IntentActions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

/**
 * Created by rcarb on 3/14/2018.
 */

public class GetFlightsTimeFrames extends AsyncTaskLoader {
    public GetFlightsTimeFrames(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public ArrayList<Integer> loadInBackground() {

        ArrayList<Integer> frame = new ArrayList<>();

        Calendar instance = Calendar.getInstance();
        Calendar instanceTwo = Calendar.getInstance();
        Calendar instanceThree = Calendar.getInstance();
        Calendar instanceToCOnvert = Calendar.getInstance();

        int integerInstance = DataCheckingUtils.converCalendarToInt(instanceToCOnvert);

        Calendar frame15 = instance;
        frame15.add(Calendar.MINUTE, 30);
        int integer30 = DataCheckingUtils.converCalendarToInt(frame15);

        Calendar frame1hour = instanceTwo;
        frame1hour.add(Calendar.HOUR, 1);
        int integer1Hour = DataCheckingUtils.converCalendarToInt(frame1hour);

        Calendar frame2hours = instanceThree;
        frame2hours.add(Calendar.HOUR, 2);
        int integer2hour = DataCheckingUtils.converCalendarToInt(frame2hours);

        if (integerInstance > 2310) {
            ArrayList<FlightObject> mergeFlightObject = new ArrayList<>();
            // /Query the databse for greater than 2300
            int intInstance = integerInstance + 1;
            int parseTime = 2359;

            int adjust = 2359 - integerInstance;
            adjust = 59 - adjust;
            adjust = adjust + 100;

            String[] projection = {FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL,
                    FlightContract.FlightEntry.COLUMN_FLIGHT_SCHEDULE,
                    FlightContract.FlightEntry.COLUMN_FLIGHT_STATUS};
            String selection = FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL + " between ? and ? or " +
                    FlightContract.FlightEntry.COLUMN_FLIGHT_SCHEDULE + " between ? and ?";
            String[] selectionArgs = {String.valueOf(intInstance), String.valueOf(parseTime),
                    String.valueOf(intInstance), String.valueOf(parseTime)};
            String sortOrder = FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL;

            Cursor cursor = getContext().getContentResolver().query(
                    FlightContract.FlightEntry.BASE_CONTENT_URI_FLIGTHS,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder);
            if (cursor != null) {
                String dump = DatabaseUtils.dumpCursorToString(cursor);


                //Query database for greater than 0 with no alarm set
                int intInstance2 = 0;
                int parseTime2 = adjust;

                String[] projection2 = {FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL,
                        FlightContract.FlightEntry.COLUMN_FLIGHT_SCHEDULE,
                        FlightContract.FlightEntry.COLUMN_FLIGHT_STATUS};
                String selection2 = FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL + " between ? and ? or " +
                        FlightContract.FlightEntry.COLUMN_FLIGHT_SCHEDULE + " between ? and ? and " +
                        FlightContract.FlightEntry.COLUMN_FLIGHT_ALARM_SET + "!= ?";
                String[] selectionArgs2 = {String.valueOf(intInstance2), String.valueOf(parseTime2),
                        String.valueOf(intInstance2), String.valueOf(parseTime2), "-2"};
                String sortOrder2 = FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL;

                Cursor cursor2 = getContext().getContentResolver().query(
                        FlightContract.FlightEntry.BASE_CONTENT_URI_FLIGTHS,
                        projection2,
                        selection2,
                        selectionArgs2,
                        sortOrder2);
                if (cursor2 != null) {
                    int nextThirty = 0;
                    int nextHour = 0;
                    int nextTwoHour = 0;
                    String dump2 = DatabaseUtils.dumpCursorToString(cursor);


                    //parse cursor
                    if (cursor.moveToFirst()) {
                        do {
                            FlightObject object = new FlightObject();
                            String status = cursor.getString(cursor.getColumnIndex(
                                    FlightContract.FlightEntry.COLUMN_FLIGHT_STATUS));
                            int extractedActualTime = cursor.getInt(cursor.getColumnIndex(
                                    FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL));
                            int extractedScheduledTime = cursor.getInt(cursor.getColumnIndex(
                                    FlightContract.FlightEntry.COLUMN_FLIGHT_SCHEDULE));
                            object.setFlightStatus(status);
                            object.setActualArrivalTime(extractedActualTime);
                            object.setFlightScheduledTime(extractedScheduledTime);
                            mergeFlightObject.add(object);
                        } while (cursor.moveToNext());
                        cursor.close();
                    }
                    if (cursor2.moveToFirst()) {
                        do {
                            FlightObject object = new FlightObject();
                            String status = cursor.getString(cursor.getColumnIndex(
                                    FlightContract.FlightEntry.COLUMN_FLIGHT_STATUS));
                            int extractedActualTime = cursor.getInt(cursor.getColumnIndex(
                                    FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL));
                            int adjustedActualTime = DataCheckingUtils.convertPassedMidnight(
                                    extractedActualTime);
                            int extractedScheduledTime = cursor.getInt(cursor.getColumnIndex(
                                    FlightContract.FlightEntry.COLUMN_FLIGHT_SCHEDULE));
                            int adjustedScheduledTime = DataCheckingUtils.convertPassedMidnight(
                                    extractedScheduledTime);

                            object.setFlightStatus(status);
                            object.setActualArrivalTime(adjustedActualTime);
                            object.setFlightScheduledTime(adjustedScheduledTime);
                            mergeFlightObject.add(object);
                        } while (cursor2.moveToNext());
                        cursor2.close();
                    }
                }
            }
            //Parse through arraylist
            int instanceToUse = integerInstance;
            int thirty = -2;
            if (instanceToUse >= 2330){
                int adjustMod = instanceToUse - 2300;
                adjustMod = 60 % adjustMod;
                thirty = 2400 + adjustMod;
            }else{
                 thirty = instanceToUse + 30;
            }
            int oneHour = instanceToUse + 1000;
            int twoHours = instanceToUse + 2000;



            for (int i = 0; i < mergeFlightObject.size(); i++){
                FlightObject object = mergeFlightObject.get(i);

                String status = object.getFlightStatus();
                int extractedTime = -2;
                int scheduled = object.getFlightScheduledTime();
                int actual = object.getActualArrivalTime();

                if (actual == -2){
                    extractedTime = scheduled;
                }else {
                    extractedTime = actual;
                }
                if (status.equals("En Route") || status.equals("Scheduled")) {
                    if (extractedTime > integerInstance) {
                        if (extractedTime <= thirty) {
                            thirty++;
                            oneHour++;
                            twoHours++;
                        } else if (extractedTime > thirty && extractedTime <= oneHour) {
                            oneHour++;
                            twoHours++;
                        } else if (extractedTime > oneHour && extractedTime <= twoHours) {
                            twoHours++;
                        }
                    }
                }
            }
            frame.add(thirty);
            frame.add(oneHour);
            frame.add(twoHours);

        } else if (integerInstance < 2159) {

            int parseTime = integerInstance + 200;
            String[] projection = {FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL,
                    FlightContract.FlightEntry.COLUMN_FLIGHT_SCHEDULE,
                    FlightContract.FlightEntry.COLUMN_FLIGHT_STATUS};
            String selection = FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL + " between ? and ? or " +
                    FlightContract.FlightEntry.COLUMN_FLIGHT_SCHEDULE + " between ? and ?";
            String[] selectionArgs = {String.valueOf(integerInstance), String.valueOf(parseTime),
                    String.valueOf(integerInstance), String.valueOf(parseTime)};
            String sortOrder = FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL;

            Cursor cursor = getContext().getContentResolver().query(
                    FlightContract.FlightEntry.BASE_CONTENT_URI_FLIGTHS,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder);
            if (cursor != null) {
                int nextThirty = 0;
                int nextHour = 0;
                int nextTwoHour = 0;
                String dump = DatabaseUtils.dumpCursorToString(cursor);
                int size = cursor.getCount();


                if (cursor.moveToFirst()) {
                    do {
                        int extractedTime = -2;
                        String status = cursor.getString(cursor.getColumnIndex(
                                FlightContract.FlightEntry.COLUMN_FLIGHT_STATUS));
                        int extractedActualTime = cursor.getInt(cursor.getColumnIndex(
                                FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL));
                        int extractedScheduledTime = cursor.getInt(cursor.getColumnIndex(
                                FlightContract.FlightEntry.COLUMN_FLIGHT_SCHEDULE));
                        if (extractedActualTime == -2) {
                            extractedTime = extractedScheduledTime;
                        } else {
                            extractedTime = extractedActualTime;
                        }

                        if (status.equals("En Route") || status.equals("Scheduled")) {
                            if (extractedTime > integerInstance) {
                                if (extractedTime <= integer30) {
                                    nextThirty++;
                                    nextHour++;
                                    nextTwoHour++;
                                } else if (extractedTime > integer30 && extractedTime <= integer1Hour) {
                                    nextHour++;
                                    nextTwoHour++;
                                } else if (extractedTime > integer1Hour && extractedTime <= integer2hour) {
                                    nextTwoHour++;
                                }
                            }
                        }

                    } while (cursor.moveToNext());
                }
                frame.add(nextThirty);
                frame.add(nextHour);
                frame.add(nextTwoHour);
            }
            assert cursor != null;
            cursor.close();


        } else if (integerInstance > 2159) {
            Calendar instanceGreater = Calendar.getInstance();

            int parseTime = integerInstance + 200;

            String[] projection = {FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL,
                    FlightContract.FlightEntry.COLUMN_FLIGHT_SCHEDULE,
                    FlightContract.FlightEntry.COLUMN_FLIGHT_STATUS};
            String selection = FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL + " between ? and ? or " +
                    FlightContract.FlightEntry.COLUMN_FLIGHT_SCHEDULE + " between ? and ?";
            String[] selectionArgs = {String.valueOf(integerInstance), String.valueOf(parseTime),
                    String.valueOf(integerInstance), String.valueOf(parseTime)};
            String sortOrder = FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL;

            Cursor cursor = getContext().getContentResolver().query(
                    FlightContract.FlightEntry.BASE_CONTENT_URI_FLIGTHS,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder);

            int currentTimeINstance = DataCheckingUtils.converCalendarToInt(instanceGreater);
            double divisioni = 2359 - currentTimeINstance;
            divisioni = divisioni / 100;
            int division = (int) divisioni;

            if (division >= 1) {
                if (cursor != null) {
                    int nextThirty = 0;
                    int nextHour = 0;
                    String dump = DatabaseUtils.dumpCursorToString(cursor);
                    int size = cursor.getCount();


                    if (cursor.moveToFirst()) {
                        do {
                            int extractedTime = -2;
                            String status = cursor.getString(cursor.getColumnIndex(
                                    FlightContract.FlightEntry.COLUMN_FLIGHT_STATUS));
                            int extractedActualTime = cursor.getInt(cursor.getColumnIndex(
                                    FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL));
                            int extractedScheduledTime = cursor.getInt(cursor.getColumnIndex(
                                    FlightContract.FlightEntry.COLUMN_FLIGHT_SCHEDULE));
                            if (extractedActualTime == -2) {
                                extractedTime = extractedScheduledTime;
                            } else {
                                extractedTime = extractedActualTime;
                            }

                            if (status.equals("En Route") || status.equals("Scheduled")) {
                                if (extractedTime > integerInstance) {
                                    if (extractedTime <= integer30) {
                                        nextThirty++;
                                        nextHour++;
                                    } else if (extractedTime > integer30 && extractedTime <= integer1Hour) {
                                        nextHour++;
                                    }
                                }
                            }

                        } while (cursor.moveToNext());
                    }
                    frame.add(nextThirty);
                    frame.add(nextHour);
                }

            } else if (division < 1) {

                if (cursor != null) {
                    int nextThirty = 0;
                    String dump = DatabaseUtils.dumpCursorToString(cursor);
                    int size = cursor.getCount();


                    if (cursor.moveToFirst()) {
                        do {
                            int extractedTime = -2;
                            String status = cursor.getString(cursor.getColumnIndex(
                                    FlightContract.FlightEntry.COLUMN_FLIGHT_STATUS));
                            int extractedActualTime = cursor.getInt(cursor.getColumnIndex(
                                    FlightContract.FlightEntry.COLUMN_FLIGHT_TIME_ACTUAL));
                            int extractedScheduledTime = cursor.getInt(cursor.getColumnIndex(
                                    FlightContract.FlightEntry.COLUMN_FLIGHT_SCHEDULE));
                            if (extractedActualTime == -2) {
                                extractedTime = extractedScheduledTime;
                            } else {
                                extractedTime = extractedActualTime;
                            }

                            if (status.equals("En Route") || status.equals("Scheduled")) {
                                if (extractedTime > integerInstance) {
                                    if (extractedTime <= integer30) {
                                        nextThirty++;
                                    }
                                }
                            }

                        } while (cursor.moveToNext());
                    }
                    frame.add(nextThirty);
                }
            }

            assert cursor != null;
            cursor.close();
        }
        return frame;
    }
}
