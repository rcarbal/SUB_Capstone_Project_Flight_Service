package com.example.rcarb.flightservice.utilities;

/**
 * Created by rcarb on 2/21/2018.
 */

public class DataCheckingUtils {

    public static boolean isNumber(String value){
        try {
            Double v = Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;

    }
}
