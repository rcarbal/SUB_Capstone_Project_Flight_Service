package com.example.rcarb.flightservice.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.rcarb.flightservice.utilities.IntentActions;

/**
 * Created by rcarb on 2/26/2018.
 */

public class ProcessAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String name = intent.getStringExtra(IntentActions.INTENT_SEND_STRING_FLIGHT);
        long column = intent.getLongExtra(IntentActions.INTENT_SEND_FLIGHT_COLUMN_ID, -2);
        String status = intent.getStringExtra(IntentActions.INTENT_SEND_FLIGHT_STATUS);
        int time = intent.getIntExtra(IntentActions.INTENT_SEND_SECOND_INT, -2);
        int scheduleTime = intent.getIntExtra(IntentActions.INTENT_SEND_THRID_INT, -2);

        Intent sendFOrUpdate = new Intent(IntentActions.ACTION_GET_STATUS_FLIGHT);
        sendFOrUpdate.putExtra(IntentActions.INTENT_SEND_STRING,name);
        sendFOrUpdate.putExtra(IntentActions.INTENT_SEND_FLIGHT_COLUMN_ID, column);
        sendFOrUpdate.putExtra(IntentActions.INTENT_SEND_FLIGHT_STATUS, status);
        sendFOrUpdate.putExtra(IntentActions.INTENT_SEND_SECOND_INT, time);
        sendFOrUpdate.putExtra(IntentActions.INTENT_SEND_THRID_INT, scheduleTime);
        context.sendBroadcast(sendFOrUpdate);
    }
}
