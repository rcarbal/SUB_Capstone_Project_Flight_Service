package com.example.rcarb.flightservice.data;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by rcarb on 2/19/2018.
 */

public class CheckForDatabase {

    //Checks is a databse exists and can be read.
    public static boolean checkDatabse(String databaseName){
        SQLiteDatabase checkDb = null;

        try {
            checkDb = SQLiteDatabase.openDatabase(databaseName, null,
                    SQLiteDatabase.OPEN_READONLY);
            checkDb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return checkDb!= null;
    }
}

