package com.example.rcarb.flightservice.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by rcarb on 2/19/2018.
 */

public class FlightContentProvider extends ContentProvider {
    //Integer constants
    private static final int FLIGHTS = 100;
    private static final int FLIGHTS_WITH_ID = 101;

    //Uri matcher for global variables.
    private static UriMatcher sUriMatcher = buildUriMatcher();

    //Helper UriMatcher function.
    private static UriMatcher buildUriMatcher() {
        //Empty matcher stated by the NO_MATCH.
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //Uri's that the matcher will recognize.
        //Uri for the complete table
        uriMatcher.addURI(FlightContract.AUTHORITY, FlightContract.PATH_FLIGHTS, FLIGHTS);
        //Uri of the table with an item.
        uriMatcher.addURI(FlightContract.AUTHORITY, FlightContract.PATH_FLIGHTS + "/#",FLIGHTS_WITH_ID );

        return uriMatcher;

    }

    private SQLiteFlightDBHelper mDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDbHelper = new SQLiteFlightDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        //Get readable database.
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        //get the matched uri from the matcher.
        int match = sUriMatcher.match(uri);

        Cursor cursor;

        switch (match){
            //Query the flight directory.
            case FLIGHTS:
                cursor = db.query(FlightContract.FlightEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        FlightContract.FlightEntry._ID);
                break;
            default:
                throw new UnsupportedOperationException("Unknown ur: " + uri);
        }
        //Tell the cursor what content uri it was created for.
        //noinspection ConstantConditions
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri,
                      @Nullable ContentValues values) {
       //Get writable database.
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        //get uri match
        int match = sUriMatcher.match(uri);
        //Variable that will store the returned uri from the switch statement.
        Uri returnedUri;
        ContentValues contentValues = new ContentValues(values);

        switch (match){
            case FLIGHTS:
                long id = db.insert(FlightContract.FlightEntry.TABLE_NAME,
                        null, contentValues);
                if (id>0){
                    //Success
                    returnedUri = ContentUris.withAppendedId(
                            FlightContract.FlightEntry.BASE_CONTENT_URI_FLIGTHS,
                            id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
                default:
                    throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        //Notify the resolver that a change has occured in a particular uri.
        getContext().getContentResolver().notifyChange(uri, null);
        return returnedUri;
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        //Get writable database.
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        //get uri match.
        int match = sUriMatcher.match(uri);
        //number of deleted flights.
        int flightsDeleted;
        switch (match) {
            case FLIGHTS_WITH_ID:
                //get id from the uri.
                String id = uri.getPathSegments().get(1);
                //Use selection/selectionArgs to filter for this id.
                flightsDeleted = db.delete(FlightContract.FlightEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //Notify the resolver of a change.
        if (flightsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return flightsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues values,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }
}
