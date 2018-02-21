package com.example.rcarb.flightservice.objects;

/**
 * Created by rcarb on 2/20/2018.
 */

public class FlightObject {
    private String mFlightId;
    private int mNextFlightIdIndex;

    private int mFlightArrivalScheduled;
    private int mFlightArrivalScheduledIndex;

    private int mActualArrival;
    private String mStatusOfFlight;

    private String mAirport;
    private  String mAirline;

    private boolean mIsLastFLight = false;
    String mParsedString;




    public void setFlightName(String flightName){
        mFlightId = flightName;
    }
    public String getFlightName(){
        return mFlightId;
    }
    public void setFlightScheduledTime(int flightArrival){
        mFlightArrivalScheduled = flightArrival;
    }
    public int getFlightScheduledTime(){
        return mFlightArrivalScheduled;
    }

    public void setActualArrivalTime(int arrivalTime){
        mActualArrival = arrivalTime;
    }

    public int getActualArrivalTime(){
        return mActualArrival;
    }

    public void setFlightStatus(String flightStatus){
        mStatusOfFlight = flightStatus;
    }

    public String getFlightStatus(){
        return mStatusOfFlight;
    }

    public void setNextFlightIndex(int index){
        mNextFlightIdIndex= index;
    }

    public int getNextFlightIndex(){
        return mNextFlightIdIndex;
    }

    public void setNextFlightArrivalIndex(int index){
        mFlightArrivalScheduledIndex= index;
    }

    public int getFlightArrivalIndex(){
        return mFlightArrivalScheduledIndex;
    }

    public void setAirport(String airport){
        mAirport = airport;
    }

    public String getAirpot(){
        return mAirport;
    }

    public void setAirline(String airline){
        mAirline = airline;
    }

    public String getAirline(){
        return mAirline;
    }

    public void setIsLastFlight(boolean check){
        mIsLastFLight = check;
    }
    public boolean getIsLastFlight(){
        return mIsLastFLight;
    }

    public void setParsedString(String parse){
        mParsedString = parse;
    }

    public String getParsedString(){
        return mParsedString;
    }
}
