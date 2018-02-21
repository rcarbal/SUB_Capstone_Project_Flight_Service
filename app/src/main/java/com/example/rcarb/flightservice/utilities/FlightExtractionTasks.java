package com.example.rcarb.flightservice.utilities;

/**
 * Created by rcarb on 2/20/2018.
 */

public class FlightExtractionTasks {
    //Task for extracting flight information.
    public static final String ACTION_EXTRACT_SINGLE_FLIGHT_INFORMATION = "extract-single-flight";

    public static void executeTask(String action){
        if (ACTION_EXTRACT_SINGLE_FLIGHT_INFORMATION.equals(action)){
            extractSingleFlight();
        }
    }

    //Method to extract flight
    private static void extractSingleFlight() {
        String sampleArrivals ="https://www.airport-la.com/lax/arrivals";
        ExtractFlightUtilities.getFlightInfoFromWeb(sampleArrivals);
    }
}
