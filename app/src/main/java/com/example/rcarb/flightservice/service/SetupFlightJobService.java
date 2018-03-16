package com.example.rcarb.flightservice.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.example.rcarb.flightservice.utilities.FlightExtractionTasks;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by rcarb on 2/21/2018.
 */

public class SetupFlightJobService extends JobService {

    private AsyncTask mBackgroundTask;
    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(final JobParameters job) {
        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Context context = SetupFlightJobService.this;
                FlightExtractionTasks.executeTask(FlightExtractionTasks.ACTION_GET_FLIGHTS_TO_UPDATE,
                        null,
                        context,
                        -2,
                        null,
                        null,
                        null,
                        "null",
                        -2);
                return null;
            }

            @Override
            protected void onPreExecute() {
                jobFinished(job, false );
            }
        };
        mBackgroundTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mBackgroundTask != null)mBackgroundTask.cancel(true);
        return true;
    }
}
