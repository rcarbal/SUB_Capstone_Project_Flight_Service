package com.example.rcarb.flightservice.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rcarb on 3/1/2018.
 */

public class FlightTimeObject implements Parcelable {

    private int mHour;
    private int mMinute;

    public FlightTimeObject() {
    }

    protected FlightTimeObject(Parcel in) {
        mHour = in.readInt();
        mMinute = in.readInt();
    }

    public static final Creator<FlightTimeObject> CREATOR = new Creator<FlightTimeObject>() {
        @Override
        public FlightTimeObject createFromParcel(Parcel in) {
            return new FlightTimeObject(in);
        }

        @Override
        public FlightTimeObject[] newArray(int size) {
            return new FlightTimeObject[size];
        }
    };

    public void setHour(int hour){
        mHour = hour;
    }
    public int getHour(){
        return mHour;
    }

    public void setMinute(int minute){
        mMinute = minute;
    }

    public int getMinute(){
        return mMinute;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mHour);
        dest.writeInt(mMinute);
    }
}
