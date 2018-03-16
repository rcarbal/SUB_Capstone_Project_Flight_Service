package com.example.rcarb.flightservice.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.rcarb.flightservice.utilities.IntentActions;

/**
 * Created by rcarb on 3/14/2018.
 */

public class AlarmTimeFrameReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentParser = new Intent(IntentActions.ACTION_GET_PARSER);
        context.sendBroadcast(intentParser);
    }
}
