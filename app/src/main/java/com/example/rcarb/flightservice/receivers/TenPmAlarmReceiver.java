package com.example.rcarb.flightservice.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.rcarb.flightservice.utilities.IntentActions;

/**
 * Created by rcarb on 3/15/2018.
 */

public class TenPmAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent setElevenPm = new Intent(IntentActions.ACTION_START_ELEVEN_PM_PARSE);
        context.sendBroadcast(setElevenPm);

    }
}
