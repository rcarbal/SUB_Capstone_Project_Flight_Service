package com.example.rcarb.flightservice.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.rcarb.flightservice.utilities.FlightExtractionTasks;

/**
 * Created by rcarb on 2/20/2018.
 */

public class FlightIntentService extends IntentService {

    public FlightIntentService() {
        super("FlightIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        FlightExtractionTasks.executeTask(action);
    }
}
