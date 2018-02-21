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

public class GetFlightObjectLoader extends AsyncTaskLoader {

    private String mUri;

    public GetFlightObjectLoader(@NonNull Context context,
                                 String uri) {
        super(context);
        mUri = uri;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public FlightObject loadInBackground() {
       String flightString = ExtractFlightUtilities.getFlightInfoFromWeb(mUri);
       FlightObject flight = ExtractFlightUtilities.saveFlightStringToObject(flightString);
       return flight;
    }
}
