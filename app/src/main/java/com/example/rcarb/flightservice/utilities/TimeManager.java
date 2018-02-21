package com.example.rcarb.flightservice.utilities;

import android.text.format.DateFormat;

/**
 * Created by rcarb on 2/20/2018.
 */

public class TimeManager {

    public static int timeEtraction(){
        //gets a int of the current military time.
        String getTimeString = DateFormat.format("kk", System.currentTimeMillis()).toString();
        int convertTimeToInt = Integer.valueOf(getTimeString);
        int additionToNext = -1;

        if (convertTimeToInt == 0 || convertTimeToInt == 1) {
            additionToNext = 11;
        }
        else if (convertTimeToInt == 2 || convertTimeToInt == 3) {
            additionToNext = 10;
        }
        else if (convertTimeToInt == 4 || convertTimeToInt == 5) {
            additionToNext = 9;
        }
        else if (convertTimeToInt == 6 || convertTimeToInt == 7) {
            additionToNext = 8;
        }
        else if (convertTimeToInt == 8 || convertTimeToInt == 9) {
            additionToNext = 7;
        }

        else if (convertTimeToInt == 10 || convertTimeToInt == 11) {
            additionToNext = 6;
        }

        else if (convertTimeToInt == 12 || convertTimeToInt == 13) {
            additionToNext = 5;
        }
        else if (convertTimeToInt == 14 || convertTimeToInt == 15) {
            additionToNext = 4;

        }
        else if (convertTimeToInt == 16 || convertTimeToInt == 17) {
            additionToNext = 3;
        }
        else if (convertTimeToInt == 18 || convertTimeToInt == 19) {
            additionToNext = 2;
        }
        else if (convertTimeToInt == 20 || convertTimeToInt == 21) {
            additionToNext = 1;
        }
        else if (convertTimeToInt == 22 || convertTimeToInt == 23) {
            additionToNext = 0;
        }

        return additionToNext;
    }
}
