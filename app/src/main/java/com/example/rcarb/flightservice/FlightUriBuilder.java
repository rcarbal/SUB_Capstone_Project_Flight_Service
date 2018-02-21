package com.example.rcarb.flightservice;

/**
 * Created by rcarb on 2/20/2018.
 */

public class FlightUriBuilder {

    private static final String BASE_CONTENT_URI = "https://www.airport-la.com/lax/arrivals";
    private static final String APPEND_PAGE = "?t=";

    public static String buildUri(int timeFrame){

        String returnedUri ="";

        if (timeFrame >0 || timeFrame <0){
            returnedUri = BASE_CONTENT_URI + APPEND_PAGE +timeFrame;
        }
        else if (timeFrame == 0){
            returnedUri = BASE_CONTENT_URI;
        }
        return returnedUri;
    }
}
