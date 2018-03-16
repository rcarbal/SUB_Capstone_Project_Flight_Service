package com.example.rcarb.flightservice.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rcarb on 2/20/2018.
 */

public class FlightObject implements Parcelable {


    private long mIdColumn = -2;
    private int mDate = -2;
    private int mGate = -2;
    private String mFlightId = "null";
    private int mNextFlightIdIndex = -2;

    private int mFlightArrivalScheduled = -2;
    private int mFlightArrivalScheduledIndex= -2;

    private int mActualArrival =-2;
    private String mPreStatus = "null";

    private String mAirport= "null";
    private  String mAirline = "null";

    private boolean mIsLastFLight = false;
    private String mParsedString = "null";
    private int mAlarmSet = -2;
    private String mPostStatus = null;
    private String mDay = "null";

    public FlightObject() {
    }

    protected FlightObject(Parcel in) {

        mIdColumn = in.readLong();
        mDate = in.readInt();
        mGate = in.readInt();
        mFlightId = in.readString();
        mNextFlightIdIndex = in.readInt();
        mFlightArrivalScheduled = in.readInt();
        mFlightArrivalScheduledIndex = in.readInt();
        mActualArrival = in.readInt();
        mPreStatus = in.readString();
        mAirport = in.readString();
        mAirline = in.readString();
        mIsLastFLight = in.readByte() != 0;
        mParsedString = in.readString();
        mAlarmSet = in.readInt();
         mPostStatus = in.readString();
         mDay = in.readString();
    }

    public static final Creator<FlightObject> CREATOR = new Creator<FlightObject>() {
        @Override
        public FlightObject createFromParcel(Parcel in) {
            return new FlightObject(in);
        }

        @Override
        public FlightObject[] newArray(int size) {
            return new FlightObject[size];
        }
    };

    public void setColumnId(long column){
        mIdColumn = column;
    }
    public long getColumnId(){
        return mIdColumn;
    }

    public void setDate(int date){
        mDate = date;
    }

    public void setFlightName(String flightName){
        mFlightId = flightName;
    }
    public String getFlightName(){
        return mFlightId;
    }
    public void setFlightScheduledTime(int flightArrival){
        mFlightArrivalScheduled = flightArrival;
    }

    public int getGate(){
        return mGate;
    }

    public void setGate(int gate){
        mGate = gate;
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
        mPreStatus = flightStatus;
    }

    public String getFlightStatus(){
        return mPreStatus;
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

    public int getAlarm(){
        return mAlarmSet;
    }
    public void setAlarm(int alarm){
        mAlarmSet = alarm;
    }

    public String getParsedString(){
        return mParsedString;
    }

    public void setPostStatus(String post){
        mPostStatus = post;
    }

    public String getPostStatus(){
        return mPostStatus;
    }

    public void setDay(String day){
        mDay = day;
    }
    public String getDay(){
        return mDay;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mIdColumn);
        dest.writeInt(mDate);
        dest.writeInt(mGate);
        dest.writeString(mFlightId);
        dest.writeInt(mNextFlightIdIndex);
        dest.writeInt(mFlightArrivalScheduled);
        dest.writeInt(mFlightArrivalScheduledIndex);
        dest.writeInt(mActualArrival);
        dest.writeString(mPreStatus);
        dest.writeString(mAirport);
        dest.writeString(mAirline);
        dest.writeByte((byte) (mIsLastFLight ? 1 : 0));
        if (!mParsedString.equals("null")){
            mParsedString = "null";
        }
        dest.writeString(mParsedString);
        dest.writeInt(mAlarmSet);
        dest.writeString(mPostStatus);
        dest.writeString(mDay);
    }
}
