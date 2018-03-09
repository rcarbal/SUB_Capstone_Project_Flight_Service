package com.example.rcarb.flightservice;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rcarb.flightservice.data.CheckForDatabase;
import com.example.rcarb.flightservice.data.SQLiteFlightDBHelper;
import com.example.rcarb.flightservice.loaders.GetFlightObjectLoader;
import com.example.rcarb.flightservice.loaders.GetFlightsFromParsedStringLoader;
import com.example.rcarb.flightservice.objects.FlightObject;
import com.example.rcarb.flightservice.receivers.ProcessAlarmReceiver;
import com.example.rcarb.flightservice.service.AlarmService;
import com.example.rcarb.flightservice.service.FlightIntentService;
import com.example.rcarb.flightservice.service.SecondAlarmService;
import com.example.rcarb.flightservice.service.AllDayAlarmService;
import com.example.rcarb.flightservice.utilities.FlightExtractionTasks;
import com.example.rcarb.flightservice.utilities.IntentActions;
import com.example.rcarb.flightservice.utilities.TimeManager;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    IntentFilter mDatabseReadyFilter;
    DatabaseReadyReceiver mReciever;


    private ArrayList<FlightObject> arrayList;
    private ArrayList<FlightObject> arrayListUpdate;

    private static final int EXTRACT_FLIGHTS_LOADER = 1;
    private static final int EXTRACT_FLIGHTS_STRING_LOADER = 2;

    private TextView taskText;
    private TextView flightNameText;
    private TextView countText;
    private TextView updatingNeeded;
    private TextView alarmsRemaining;
    private TextView databseFound;
    private TextView flightUpdated;
    private TextView alarmsForReUpdating;
    private TextView statusToBeUpdated;
    private TextView statusAfterUpdate;
    private TextView systemMessage;
    private TextView allDayAlarms;

    private int mFirstAlarms;
    private int mSecondAlarm;
    private int updateArrayCount;
    private int count;
    private int loaderCount;
    private int pageCount;
    private int arrayIndex;
    private int mAlldayAlarms;
    private int mInitialHour;
    private int mAllDayAlarmSet;

    private boolean lastFlight = false;
    private String mCurrentParsedString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //ReminderUtilities.scheduleFlightUpdate(this);

        mDatabseReadyFilter = new IntentFilter();
        mReciever = new DatabaseReadyReceiver();

        mDatabseReadyFilter.addAction(IntentActions.FLIGHT_EXTRACTED_DONE);
        mDatabseReadyFilter.addAction(IntentActions.DATABASE_FLIGHT_INSERTED_FAILURE);
        mDatabseReadyFilter.addAction(IntentActions.DATABASE_FLIGHT_INSERTED_SUCCESS);
        mDatabseReadyFilter.addAction(IntentActions.FLIGHT_ARRAYLIST_FOR_UPDATE_EXTRACTED);
        mDatabseReadyFilter.addAction(IntentActions.ALARM_SET);
        mDatabseReadyFilter.addAction(IntentActions.ALARM_FIRED_FLIGHT_UPDATED);
        mDatabseReadyFilter.addAction(IntentActions.INTENT_SEND_TOAST);
        mDatabseReadyFilter.addAction(IntentActions.ACTION_GET_STATUS_FLIGHT);
        mDatabseReadyFilter.addAction(IntentActions.FLIGHT_UPDATE_DATABASE_STATUS);
        mDatabseReadyFilter.addAction(IntentActions.ACTION_CONTINUE_SETTING_ALARMS);
        mDatabseReadyFilter.addAction(IntentActions.ACTION_SETUP_SECOND_ALARM);
        mDatabseReadyFilter.addAction(IntentActions.ACTION_SECOND_ALARM_SUCCESSUFULLY_SETUP);
        mDatabseReadyFilter.addAction(IntentActions.INTENT_SEND_FLIGHT_STATUS);
        mDatabseReadyFilter.addAction(IntentActions.ACTION_GET_SECOND_STATUS_FLIGHT);
        mDatabseReadyFilter.addAction(IntentActions.ACTION_SECOND_ALARM_COMPLETE);
        mDatabseReadyFilter.addAction(IntentActions.ACTION_CURRENT_ARRAYLIST_REQUEST_CODES_COMPLETED);
        mDatabseReadyFilter.addAction(IntentActions.ACTION_PROCESS_REMAINING_DAILY_ALARMS);
        mDatabseReadyFilter.addAction(IntentActions.ACTION_NOT_INITIAL_DATABASE_SAVE_SUCCESSFUL);

        taskText = findViewById(R.id.task);
        flightNameText = findViewById(R.id.flight_name);
        countText = findViewById(R.id.count);
        updatingNeeded = findViewById(R.id.update_needed);
        alarmsRemaining = findViewById(R.id.remaining_alarms);
        databseFound = findViewById(R.id.found_database);
        flightUpdated = findViewById(R.id.first_updated_flight);
        alarmsForReUpdating = findViewById(R.id.reupdate_alarms);
        statusToBeUpdated = findViewById(R.id.update_status);
        statusAfterUpdate = findViewById(R.id.status_after_update);
        systemMessage = findViewById(R.id.system_alarm);
        allDayAlarms = findViewById(R.id.tv_alarms);


        mFirstAlarms = 0;
        mSecondAlarm = 0;
        mAlldayAlarms = 0;
        mAllDayAlarmSet = 0;
    }

    public void setupAlarmsForDay(@Nullable View view) {
        //Setup up alarms for the rest of the day.
        //service to do so.
        Calendar dateCalendar = Calendar.getInstance();
        mInitialHour = dateCalendar.get(Calendar.HOUR_OF_DAY);

        mAlldayAlarms = (24 - mInitialHour) / 2;
        Intent setupDaysAlarm = new Intent(MainActivity.this, AllDayAlarmService.class);
        setupDaysAlarm.putExtra(IntentActions.ACTION_SEND_ALL_DAY_ALARMS_INT, mAlldayAlarms);
        setupDaysAlarm.putExtra(IntentActions.ACTION_SEND_INITIAL_HOUR_INT, mInitialHour);
        startService(setupDaysAlarm);
    }

    public void startExtraction(View view) {
        //Check if database has been created
        if (!checkForCurrentDatabase()) {
            //If no database has been created, start flight info extraction.
            arrayList = new ArrayList<>();
            count = 0;
            loaderCount = 12;
            arrayIndex = 0;
            updateArrayCount = 0;
            checkTimeFrame();
        }
    }

    //Check if database exists
    public boolean checkForCurrentDatabase() {
        if (CheckForDatabase.checkDatabse(SQLiteFlightDBHelper.DATABASE_NAME)) {
            databseFound.setText("true");
            return true;
        }
        databseFound.setText("false");
        return false;
    }

    //prapare variables for when databse is not found
    public void prepareNewDatabseVariables(View view) {
        arrayList = new ArrayList<>();
        count = 0;
        loaderCount = 12;
        Toast.makeText(this, "New Arraylist, Count, loadercount",
                Toast.LENGTH_SHORT).show();
    }

    //Check time frame.
    public void checkTimeFrame() {
        taskText.setText("Extracting flights");
        int timeFrame = TimeManager.timeEtraction();
        pageCount = (timeFrame - 12) + 1;

        String parseString = FlightUriBuilder.buildUri(pageCount);
        startFlightExtraction(parseString);


    }

    //Button 4. Save flights to database
    public void saveflightsToDatabase(View view) {
        taskText.setText("Saving flights to database");
        Intent broadcastIntent = new Intent(IntentActions.FLIGHT_EXTRACTED_DONE);
        sendBroadcast(broadcastIntent);
    }

    //5.Get flights to update
    public void getFlightsToUpdate(View view) {
        taskText.setText("Getting flights for update");
        Intent databaseFailedIntent = new Intent(IntentActions.DATABASE_FLIGHT_INSERTED_SUCCESS);
        sendBroadcast(databaseFailedIntent);
    }

    //6. Set Alarms for each flight
    public void setAlarmsButton(View view) {
        taskText.setText("Set alarms");
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private boolean stopRunningService() {
        boolean stop = false;
        stop = stopService(new Intent(MainActivity.this, FlightIntentService.class));
        return stop;
    }

    private void startFlightExtraction(String uri) {
//        //Use Service
//        Intent startExtraction = new Intent(this, FlightIntentService.class);
//        startExtraction.setAction(FlightExtractionTasks.ACTION_EXTRACT_SINGLE_FLIGHT_INFORMATION);
//        startService(startExtraction);

        //Use loader
        taskText.setText(R.string.tv_extract_flights);

        Bundle bundle = new Bundle();
        bundle.putString("parse", uri);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<FlightObject> getFlightLoader = loaderManager.getLoader(EXTRACT_FLIGHTS_LOADER);
        if (getFlightLoader == null) {
            loaderManager.initLoader(EXTRACT_FLIGHTS_LOADER, bundle, getFlightObject);
        } else {
            loaderManager.restartLoader(EXTRACT_FLIGHTS_LOADER, bundle, getFlightObject);
        }
    }

    public void parseRemainingFlights(int index, String parse) {
        if (index < 0) {
            String a = "";
        }
        taskText.setText(R.string.tv_extract_flights);
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        bundle.putString("parse", parse);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<FlightObject> getFlightLoader = loaderManager.getLoader(EXTRACT_FLIGHTS_STRING_LOADER);
        if (getFlightLoader == null) {
            loaderManager.initLoader(EXTRACT_FLIGHTS_STRING_LOADER, bundle, getFlightObjectString);
        } else {
            loaderManager.restartLoader(EXTRACT_FLIGHTS_STRING_LOADER, bundle, getFlightObjectString);
        }

    }


//  <----------------------------------Broadcast Receiver------------------------------------------>

    public class DatabaseReadyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(IntentActions.FLIGHT_EXTRACTED_DONE)) {

                ArrayList<FlightObject> arraySend = arrayList;

                Intent saveTodatabase = new Intent(MainActivity.this,
                        FlightIntentService.class);
                saveTodatabase.setAction(FlightExtractionTasks.SAVE_FLIGHTS_TO_DATABASE);
                saveTodatabase.putParcelableArrayListExtra(IntentActions.PUT_EXTRA_PARCEL_ARRAY,
                        arraySend);
                saveTodatabase.putExtra(IntentActions.ACTION_START_INITIAL_DATABASE_SAVE, "initial");
                taskText.setText(R.string.tv_task_saving_to_databse);
                startService(saveTodatabase);
            }

            //The flight database was save sucecessfully.
            else if (action.equals(IntentActions.DATABASE_FLIGHT_INSERTED_SUCCESS)) {
                taskText.setText("");

                //Extract flights that need updating.
                Intent getFlightsForUpdate = new Intent(MainActivity.this,
                        FlightIntentService.class);
                getFlightsForUpdate.setAction(FlightExtractionTasks.ACTION_GET_FLIGHTS_TO_UPDATE);
                taskText.setText("Getting flights for update");
                startService(getFlightsForUpdate);


            }
            //The database was not saved successfully.
            else if (action.equals(IntentActions.DATABASE_FLIGHT_INSERTED_FAILURE)) {
                taskText.setText("");
                Toast.makeText(context, "FAILED", Toast.LENGTH_SHORT).show();
            }

            //Job service success received array list of flights to be updated
            else if (action.equals(IntentActions.FLIGHT_ARRAYLIST_FOR_UPDATE_EXTRACTED)) {
                taskText.setText("");
                ArrayList<FlightObject> arrayList = intent.getParcelableArrayListExtra(
                        IntentActions.PUT_EXTRA_PARCEL_ARRAY);
                arrayListUpdate = arrayList;
                int size = arrayListUpdate.size();

//                int time = 1138;


                Intent alarmIntent = new Intent(MainActivity.this,
                        AlarmService.class);
                alarmIntent.putParcelableArrayListExtra(IntentActions.PUT_EXTRA_PARCEL_ARRAY,
                        arrayList);
                alarmIntent.putExtra(IntentActions.INTENT_SEND_INT, arrayIndex);
//                alarmIntent.putExtra(IntentActions.INTENT_SEND_SECOND_INT, time);

                taskText.setText("Setting alarms");
                updateArrayCount = arrayList.size();
                updatingNeeded.setText("" + updateArrayCount);

                Toast.makeText(context, "Updated fligts extracted, ready for alarms",
                        Toast.LENGTH_SHORT).show();

                startService(alarmIntent);
            } else if (action.equals(IntentActions.ACTION_CONTINUE_SETTING_ALARMS)) {
                taskText.setText("");
                mFirstAlarms++;
                alarmsRemaining.setText("" + mFirstAlarms);

                ArrayList<FlightObject> updatedArrayslIst = intent.getParcelableArrayListExtra(
                        IntentActions.PUT_EXTRA_PARCEL_ARRAY);
                arrayListUpdate = new ArrayList<FlightObject>();
                arrayListUpdate = updatedArrayslIst;

                if (arrayIndex < arrayListUpdate.size()-1) {
                    arrayIndex++;
                    int time = intent.getIntExtra(IntentActions.INTENT_SEND_SECOND_INT, -2);
                    Intent alarmIntent = new Intent(MainActivity.this,
                            AlarmService.class);
                    alarmIntent.putParcelableArrayListExtra(IntentActions.PUT_EXTRA_PARCEL_ARRAY,
                            arrayListUpdate);
                    if (arrayIndex == arrayListUpdate.size()-2){
                        String a ="";
                    }

                    alarmIntent.putExtra(IntentActions.INTENT_SEND_INT, arrayIndex);
                    alarmIntent.putExtra(IntentActions.INTENT_SEND_SECOND_INT, time);
                    taskText.setText("Setting alarms");
                    startService(alarmIntent);


            }
            if (arrayIndex == arrayListUpdate.size()-1)
                Toast.makeText(context, "Done setting up alarms", Toast.LENGTH_SHORT).show();
            //Update all the flight's.
            Intent intentUpdateRequestCode = new Intent(MainActivity.this,
                    FlightIntentService.class);
            intentUpdateRequestCode.setAction(FlightExtractionTasks.ACTION_UPDATE_ALARMS_REQUEST_CODE);
            intentUpdateRequestCode.putParcelableArrayListExtra(IntentActions.ACTION_SEND_PARCEL_REQUEST_CODE,
                    arrayListUpdate);
            startService(intentUpdateRequestCode);


        }else if(action.equals(IntentActions.ACTION_CURRENT_ARRAYLIST_REQUEST_CODES_COMPLETED))

        {
            systemMessage.setText("All request code batches competed.");


        } else if(action.equals(IntentActions.ACTION_GET_STATUS_FLIGHT))

        {
            mFirstAlarms--;
            updateArrayCount--;
            updatingNeeded.setText("" + updateArrayCount);
            alarmsRemaining.setText("" + mFirstAlarms);

            taskText.setText("Get status of flight");
            long flightId = intent.getLongExtra(IntentActions.INTENT_SEND_FLIGHT_COLUMN_ID, -2);
            String flightName = intent.getStringExtra(IntentActions.INTENT_SEND_STRING);
            String status = intent.getStringExtra(IntentActions.INTENT_SEND_FLIGHT_STATUS);

            flightUpdated.setText("" + flightName);
            statusToBeUpdated.setText("" + status);
            statusAfterUpdate.setText("");


            Intent getFlightStatus = new Intent(MainActivity.this, FlightIntentService.class);
            getFlightStatus.setAction(FlightExtractionTasks.ACTION_EXTRACT_SINGLE_FLIGHT_INFORMATION);
            getFlightStatus.putExtra(IntentActions.INTENT_SEND_FLIGHT_COLUMN_ID, flightId);
            getFlightStatus.putExtra(IntentActions.INTENT_SEND_STRING_FLIGHT, flightName);
            startService(getFlightStatus);
        }

        //Start loader that will update the database.
            else if(action.equals(IntentActions.FLIGHT_UPDATE_DATABASE_STATUS))

        {
            FlightObject object = intent.getExtras().getParcelable(IntentActions.INTENT_SEND_PARCEL);
            String status = object.getFlightStatus();
            statusAfterUpdate.setText(status);

            Intent updateStatus = new Intent(MainActivity.this, FlightIntentService.class);
            updateStatus.setAction(FlightExtractionTasks.ACTION_DATABASE_UPDATE_STATUS);
            updateStatus.putExtra(IntentActions.INTENT_SEND_PARCEL, object);
            startService(updateStatus);
        }
        //sets up second alarm
            else if(action.equals(IntentActions.ACTION_SETUP_SECOND_ALARM)){
            taskText.setText("Setting up second alarm");

            FlightObject parcelForSecondAlarm = intent.getParcelableExtra(
                    IntentActions.INTENT_SEND_PARCEL_FOR_SECOND_ALARM);
            Intent secondAlarmSetup = new Intent(MainActivity.this, SecondAlarmService.class);
            secondAlarmSetup.putExtra(IntentActions.INTENT_SEND_PARCEL_TO_SECOND_ALARM_SERVICE,
                    parcelForSecondAlarm);
            startService(secondAlarmSetup);
        }
        //Second alarm was successfully setup
            else if(action.equals(IntentActions.ACTION_SECOND_ALARM_SUCCESSUFULLY_SETUP)){
            taskText.setText("");
            mSecondAlarm++;
            alarmsForReUpdating.setText("" + mSecondAlarm);
        } else if(action.equals(IntentActions.ACTION_GET_SECOND_STATUS_FLIGHT))

        {
            taskText.setText("Getting second status");

            long flightId = intent.getLongExtra(IntentActions.INTENT_SEND_FLIGHT_COLUMN_ID, -2);
            String flightName = intent.getStringExtra(IntentActions.INTENT_SEND_STRING);


            Intent getSeconfFlightStatus = new Intent(MainActivity.this, FlightIntentService.class);
            getSeconfFlightStatus.setAction(FlightExtractionTasks.ACTION_EXTRACT_SINGLE_FLIGHT_INFO_FOR_SECOND_ALARM);
            getSeconfFlightStatus.putExtra(IntentActions.INTENT_SEND_FLIGHT_COLUMN_ID, flightId);
            getSeconfFlightStatus.putExtra(IntentActions.INTENT_SEND_STRING_FLIGHT, flightName);
            startService(getSeconfFlightStatus);

        } else if(action.equals(IntentActions.ACTION_SECOND_ALARM_COMPLETE)) {
            taskText.setText("");
            mSecondAlarm--;
            alarmsForReUpdating.setText("" + mSecondAlarm);
        } else if(action.equals(IntentActions.ACTION_PROCESS_REMAINING_DAILY_ALARMS))

        {
            mAlldayAlarms = intent.getIntExtra(IntentActions.ACTION_SEND_ALL_DAY_ALARMS_INT, -2);
            int hour = intent.getIntExtra(IntentActions.ACTION_SEND_INITIAL_HOUR_INT, -2);
            if (mAlldayAlarms > 0) {

                mAllDayAlarmSet++;
                allDayAlarms.setText("" + mAllDayAlarmSet);
                mAlldayAlarms--;
            }
            if (mAlldayAlarms > 0 && hour > -1) {


                Intent continueIntent = new Intent(MainActivity.this, AllDayAlarmService.class);
                continueIntent.putExtra(IntentActions.ACTION_SEND_ALL_DAY_ALARMS_INT, mAlldayAlarms);
                continueIntent.putExtra(IntentActions.ACTION_SEND_INITIAL_HOUR_INT, hour);
                startService(continueIntent);

            }
            if (mAlldayAlarms == 0) {
                Toast.makeText(context, "Daily Alarms setup", Toast.LENGTH_SHORT).show();
                systemMessage.setText("Daily alarms setup");

            }
        }
            else if(action.equals(IntentActions.ACTION_NOT_INITIAL_DATABASE_SAVE_SUCCESSFUL))

        {
            Toast.makeText(context, "Re-parse complete", Toast.LENGTH_SHORT).show();
        }
            else if(action.equals(IntentActions.INTENT_SEND_TOAST))

        {
            String message = intent.getStringExtra(IntentActions.INTENT_SEND_TOAST_MESSAGE);
            if (message.equals("Second Alarm failed")) {
                systemMessage.setText("Second Alarm did not close status");
            }
            Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();
        }
    }

}

    //<----------------------------------LOADERS--------------------------------------------------->
    private final LoaderManager.LoaderCallbacks<FlightObject> getFlightObject =
            new LoaderManager.LoaderCallbacks<FlightObject>() {
                @Override
                public Loader<FlightObject> onCreateLoader(int id, Bundle args) {

                    String uri = args.getString("parse");
                    return new GetFlightObjectLoader(MainActivity.this, uri);
                }

                @Override
                public void onLoadFinished(Loader<FlightObject> loader, FlightObject data) {

                    if (data.getFlightName().equals("null")) {
                        lastFlight = true;
                        taskText.setText("");

                        loaderCount--;
                        if (loaderCount > 0) {
                            pageCount++;
                            String parseString = FlightUriBuilder.buildUri(pageCount);
                            if (parseString.equals("https://www.airport-la.com/lax/arrivals?t=1")) {

                            }
                            startFlightExtraction(parseString);
                        }
                    } else {
                        flightNameText.setText(data.getFlightName());
                        arrayList.add(data);
                        count++;
                        countText.setText("" + count);
                        mCurrentParsedString = data.getParsedString();
                        if (data.getIsLastFlight()) {
                            lastFlight = true;
                            taskText.setText("");

                            loaderCount--;
                            if (loaderCount > 0) {
                                pageCount++;
                                String parseString = FlightUriBuilder.buildUri(pageCount);
                                startFlightExtraction(parseString);
                            }
                            String a = "";
                        } else if (!data.getIsLastFlight()) {
                            int index = data.getNextFlightIndex();
                            String parse = data.getParsedString();
                            parseRemainingFlights(index, parse);
                        }
                    }
                    if (loaderCount == 0) {
                        //Setup broadcast here
                        Intent broadcastIntent = new Intent(IntentActions.FLIGHT_EXTRACTED_DONE);
                        sendBroadcast(broadcastIntent);
                        Toast.makeText(MainActivity.this, "Extraction Complete", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onLoaderReset(Loader<FlightObject> loader) {

                }
            };

    private final LoaderManager.LoaderCallbacks<FlightObject> getFlightObjectString =
            new LoaderManager.LoaderCallbacks<FlightObject>() {
                @Override
                public Loader<FlightObject> onCreateLoader(int id, Bundle args) {
                    int key = args.getInt("index");
                    String parse = args.getString("parse");
                    return new GetFlightsFromParsedStringLoader(MainActivity.this,
                            parse,
                            key);
                }

                @Override
                public void onLoadFinished(Loader<FlightObject> loader, FlightObject data) {
                    flightNameText.setText(data.getFlightName());
                    arrayList.add(data);
                    count++;
                    countText.setText("" + count);
                    mCurrentParsedString = data.getParsedString();
                    if (data.getIsLastFlight()) {
                        lastFlight = true;
                        taskText.setText("");

                        loaderCount--;
                        if (loaderCount > 0) {
                            pageCount++;
                            String parseString = FlightUriBuilder.buildUri(pageCount);
                            startFlightExtraction(parseString);
                        }
                        //Next page
                    } else if (!data.getIsLastFlight()) {
                        parseRemainingFlights(data.getNextFlightIndex(),
                                data.getParsedString());
                    }
                    if (loaderCount == 0) {
                        //Setup broadcast here
                        Intent broadcastIntent = new Intent(IntentActions.FLIGHT_EXTRACTED_DONE);
                        sendBroadcast(broadcastIntent);
                    }
                }

                @Override
                public void onLoaderReset(Loader<FlightObject> loader) {

                }
            };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReciever, mDatabseReadyFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReciever);
    }

    public void testMethod(ArrayList<FlightObject> arrayList) {


    }

    public void testAlarm(View view) {
        Intent intent = new Intent(this, ProcessAlarmReceiver.class);
        boolean alarm = (PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_NO_CREATE)) != null;
        if (alarm) {
            Toast.makeText(this, "ALARM FOUND", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "ALARM NIT FOUND", Toast.LENGTH_SHORT).show();
        }

    }

    public void cancelAlarm(View view) {
        Intent intent = new Intent(this, ProcessAlarmReceiver.class);
        PendingIntent alarm = (PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_NO_CREATE));
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmMgr != null) {
            alarmMgr.cancel(alarm);
            Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Alarm not canceled", Toast.LENGTH_SHORT).show();
        }
    }
}
