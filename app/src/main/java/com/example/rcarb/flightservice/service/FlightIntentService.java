package com.example.rcarb.flightservice.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.rcarb.flightservice.objects.FlightObject;
import com.example.rcarb.flightservice.utilities.FlightExtractionTasks;
import com.example.rcarb.flightservice.utilities.IntentActions;

import java.util.ArrayList;

/**
 * Created by rcarb on 2/20/2018.
 */

public class FlightIntentService extends IntentService {

    public FlightIntentService() {
        super("FlightIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        String action = intent.getAction();
        Context context = getApplicationContext();
        String initial = intent.getStringExtra(IntentActions.INTENT_SEND_ALARM_PASS_LABEL);
        if (action.equals(FlightExtractionTasks.SAVE_FLIGHTS_TO_DATABASE)) {
            ArrayList<FlightObject> flightObjectArrayList = intent.
                    getParcelableArrayListExtra(IntentActions.PUT_EXTRA_PARCEL_ARRAY);
            FlightExtractionTasks.executeTask(
                    action,
                    flightObjectArrayList,
                    context,
                    -2,
                    null,
                    null,
                    null,
                    initial,
                    -2,
                    -2);
        } else if (action.equals(FlightExtractionTasks.ACTION_GET_FLIGHTS_TO_UPDATE)) {

            FlightExtractionTasks.executeTask(action,
                    null,
                    context,
                    -2,
                    null,
                    null,
                    null,
                    initial,
                    -2,
                    -2);
        } else if (action.equals(FlightExtractionTasks.ACTION_EXTRACT_FLIGHTS_NO_ALARM)){

            FlightExtractionTasks.executeTask(action,
                    null,
                    context,
                    -2,
                    null,
                    null,
                    null,
                    initial,
                    -2,
                    -2);

        } else if (action.equals(FlightExtractionTasks.SETUP_ALARM_FOR_FLIGHT)) {
            ArrayList<FlightObject> flightArrayAlarms =
                    intent.getParcelableArrayListExtra(IntentActions.PUT_EXTRA_PARCEL_ARRAY);
            FlightExtractionTasks.executeTask(action,
                    flightArrayAlarms,
                    context,
                    -2,
                    null,
                    null,
                    null,
                    initial,
                    -2,
                    -2);
        } else if (action.equals(FlightExtractionTasks.ACTION_EXTRACT_SINGLE_FLIGHT_INFORMATION)) {

            long id = intent.getLongExtra(IntentActions.INTENT_SEND_FLIGHT_COLUMN_ID, -2);
            String flight = intent.getStringExtra(IntentActions.INTENT_SEND_STRING_FLIGHT);
            String status = intent.getStringExtra(IntentActions.INTENT_SEND_FLIGHT_STATUS);
            int time = intent.getIntExtra(IntentActions.INTENT_SEND_SECOND_INT, -2);
            int scheduleTime = intent.getIntExtra(IntentActions.INTENT_SEND_THRID_INT, -2);
            if (id == -2 || flight.equals("")) {
//                Intent sendToast = new Intent(IntentActions.INTENT_SEND_TOAST);
//                sendToast.putExtra(IntentActions.INTENT_SEND_STRING, "no flight or id in alarm");
//                sendBroadcast(sendToast);
            } else {

                FlightExtractionTasks.executeTask(
                        action,
                        null,
                        context,
                        id,
                        flight,
                        status,
                        null,
                        initial,
                        time,
                        scheduleTime);
            }
        }

        else if (action.equals(FlightExtractionTasks.ACTION_EXTRACT_SINGLE_FLIGHT_INFO_FOR_SECOND_ALARM)) {

            long id = intent.getLongExtra(IntentActions.INTENT_SEND_FLIGHT_COLUMN_ID, -2);
            String flight = intent.getStringExtra(IntentActions.INTENT_SEND_STRING_FLIGHT);
            int requestCode = intent.getIntExtra(IntentActions.INTENT_REQUEST_CODE, -2);
            if (id == -2 || flight.equals("")) {
//                Intent sendToast = new Intent(IntentActions.INTENT_SEND_TOAST);
//                sendToast.putExtra(IntentActions.INTENT_SEND_STRING, "no flight or id in alarm");
//                sendBroadcast(sendToast);
            } else {

                FlightExtractionTasks.executeTask(
                        action,
                        null,
                        context,
                        id,
                        flight,
                        null,
                        null,
                        initial,
                        requestCode,
                        -2);
            }
        }

        else if (action.equals(FlightExtractionTasks.ACTION_UPDATE_ALARMS_REQUEST_CODE)){
            ArrayList<FlightObject> arrayList = intent.getParcelableArrayListExtra(
                    IntentActions.ACTION_SEND_PARCEL_REQUEST_CODE);
            FlightExtractionTasks.executeTask(
                    action,
                    arrayList,
                    context,
                    -2,
                    null,
                    null,
                    null,
                    initial,
                    -2,
                    -2);
        }


        else if (action.equals(FlightExtractionTasks.ACTION_DATABASE_UPDATE_STATUS)) {

             FlightObject flight= intent.getExtras().getParcelable(IntentActions.INTENT_SEND_PARCEL);

            if (flight!=null) {
                FlightExtractionTasks.executeTask(
                        action,
                        null,
                        context,
                        -2,
                        null,
                        null,
                        flight,
                        initial,
                        -2,
                        -2);
            }
        }

    }
}