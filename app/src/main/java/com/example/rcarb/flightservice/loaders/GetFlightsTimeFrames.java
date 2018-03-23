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

    private boolean mReset;

    public GetFlightsTimeFrames(@NonNull Context context,
                                boolean reset) {
        super(context);
        mReset = reset;
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

        int integerInstance = DataCheckingUtils.converCalendarToInt(instanceToCOnvert,0,0, mReset);

        Calendar frame15 = instance;
        frame15.add(Calendar.MINUTE, 30);
        int integer30 = DataCheckingUtils.converCalendarToInt(frame15,30,0, mReset);

        Calendar frame1hour = instanceTwo;
        frame1hour.add(Calendar.HOUR, 1);
        int integer1Hour = DataCheckingUtils.converCalendarToInt(frame1hour,0,1, mReset);

        Calendar frame2hours = instanceThree;
        frame2hours.add(Calendar.HOUR, 2);
        int integer2hour = DataCheckingUtils.converCalendarToInt(frame2hours,0,2, mReset);


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

        return frame;
    }
}
