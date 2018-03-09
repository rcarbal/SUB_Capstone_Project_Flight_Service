package com.example.rcarb.flightservice.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rcarb on 2/19/2018.
 */

public class SQLiteFlightDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME ="flight_databse.db";
    private static final int DATABASE_VERION = 1;

    public SQLiteFlightDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FlightContract.FlightEntry.SQL_CREATE_FLIGHT_ENTRIES);
        db.execSQL(FlightContract.FlightEntry.SQL_CREATE_LIST_OF_DATABASES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(FlightContract.FlightEntry.SQL_DELETE_FLIGHT_ENTRIES);
        db.execSQL(FlightContract.FlightEntry.SQL_DELETE_LIST_DATABASE);

        onCreate(db);
    }
}
