package com.example.rcarb.flightservice.loaders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.example.rcarb.flightservice.objects.FlightObject;
import com.example.rcarb.flightservice.utilities.ExtractFlightUtilities;

/**
 * Created by rcarb on 2/20/2018.
 */

public class GetFlightsFromParsedStringLoader extends AsyncTaskLoader {

    private String mStringFlight;
    private int mIndex;
    public GetFlightsFromParsedStringLoader(@NonNull Context context,
                                            String string,
                                            int startIndex) {
        super(context);
        mStringFlight = string;
        mIndex = startIndex;

    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public FlightObject loadInBackground() {
       FlightObject flightObject = ExtractFlightUtilities.saveFlightStringToObject(mStringFlight,
               mIndex);
       return flightObject;
    }
}
