package com.example.rcarb.flightservice.utilities;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.rcarb.flightservice.service.SetupFlightJobService;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by rcarb on 2/21/2018.
 */

public class ReminderUtilities {
    private static final int REMINDER_INTERVAL_MINUTES = 1;
    private static final int REMINDER_INTERVAL_SECONDS = (int)TimeUnit.MINUTES.toSeconds(REMINDER_INTERVAL_MINUTES);
    private static final int SYNC_FLEXTIME_SECONDS = REMINDER_INTERVAL_SECONDS;

    //Job tag
    private static final String CHECK_FLIGHT_TAG_JOB = "check-flight-tag";

    private static boolean sInitialized;

    synchronized public static void scheduleFlightUpdate(@NonNull final Context context){
        if (sInitialized) return;

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job constraintSetupJob = dispatcher.newJobBuilder()
                //set the service to the new job service.
                .setService(SetupFlightJobService.class)
                //set tag
                .setTag(CHECK_FLIGHT_TAG_JOB)
                //set constraints
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(false)
                .setTrigger(Trigger.executionWindow(
                        REMINDER_INTERVAL_SECONDS,
                        REMINDER_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(constraintSetupJob);
        sInitialized = true;
    }
}
