package com.example.rcarb.flightservice.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.rcarb.flightservice.utilities.IntentActions;

/**
 * Created by rcarb on 3/8/2018.
 */

public class AllDayAlarmReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startNewFlightParse = new Intent(IntentActions.ACTION_START_NEW_PARSE_FOR_ALL_FLIGHTS);
        context.sendBroadcast(startNewFlightParse);
    }
}
