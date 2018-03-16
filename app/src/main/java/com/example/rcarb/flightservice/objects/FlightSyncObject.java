package com.example.rcarb.flightservice.objects;

/**
 * Created by rcarb on 3/16/2018.
 */

public class FlightSyncObject {

    private int thirty;
    private int hour;
    private int twoHour;
    private String stamp;

    public FlightSyncObject() {
            }

    public FlightSyncObject(int thirty, int hour, int twohour, String stamp){
        this.thirty = thirty;
        this.hour = hour;
        this.twoHour = twohour;
        this.stamp = stamp;
    }

    public void setThirty(int thirty){
        this.thirty = thirty;
    }
    public int getThirty(){
        return thirty;
    }
    public void setHour(int hour){
        this.hour = hour;
    }
    public int getHour(){
        return hour;
    }
    public void setTwoHour(int twoHour){
        this.twoHour = twoHour;
    }
    public int getTwoHour(){
        return twoHour;
    }
    public void setStamp(String stamp){
        this.stamp = stamp;
    }
    public String getStamp(){
        return stamp;
    }
}
