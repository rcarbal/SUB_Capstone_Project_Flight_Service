package com.example.rcarb.flightservice.objects;

/**
 * Created by rcarb on 3/8/2018.
 */

public class CompareFlightObject {
    private String mFlight = "null";
    private String mDate = "null";
    private String mStatus = null;


    public CompareFlightObject() {
    }

    public void setFlight(String flight){
        mFlight = flight;
    }

    public void setDate(String date){
        mDate = date;
    }

    public String getFlight(){
        return mFlight;
    }

    public String getDate(){
        return mDate;
    }

    public void setStatus(String status){
        mStatus = status;
    }
    public String getStatus(){
        return mStatus;
    }
}
