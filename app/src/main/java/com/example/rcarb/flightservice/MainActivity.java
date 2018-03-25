package com.example.rcarb.flightservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
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
import com.example.rcarb.flightservice.loaders.GetFlightsTimeFrames;
import com.example.rcarb.flightservice.objects.FlightObject;
import com.example.rcarb.flightservice.objects.FlightSyncObject;
import com.example.rcarb.flightservice.receivers.ProcessSecondAlarmReceiver;
import com.example.rcarb.flightservice.service.AlarmService;
import com.example.rcarb.flightservice.service.FlightIntentService;
import com.example.rcarb.flightservice.service.RestartDayService;
import com.example.rcarb.flightservice.service.SecondAlarmService;
import com.example.rcarb.flightservice.service.AllDayAlarmService;
import com.example.rcarb.flightservice.service.SetupTenPmAlarmService;
import com.example.rcarb.flightservice.service.SetupTimeFrameService;
import com.example.rcarb.flightservice.utilities.DataCheckingUtils;
import com.example.rcarb.flightservice.utilities.FlightExtractionTasks;
import com.example.rcarb.flightservice.utilities.IntentActions;
import com.example.rcarb.flightservice.utilities.TimeManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    IntentFilter mDatabseReadyFilter;
    DatabaseReadyReceiver mReciever;


    private ArrayList<FlightObject> mArrayList;
    private ArrayList<FlightObject> mArrayListUpdate;

    private static final int EXTRACT_FLIGHTS_LOADER = 1;
    private static final int EXTRACT_FLIGHTS_STRING_LOADER = 2;
    private static final int EXTRACT_FLIGHT_TIME_FRAME = 3;

    private TextView mTaskTextView;
    private TextView mFlightNameTextView;
    private TextView mCountTextView;
    private TextView mUpdatingNeededTextView;
    private TextView mAlarmsRemainingTextView;
    private TextView mDatabaseFoundTextView;
    private TextView flightUpdated;
    private TextView alarmsForReUpdating;
    private TextView preUpdateStatus;
    private TextView statusAfterUpdate;
    private TextView systemMessage;
    private TextView allDayAlarms;
    private TextView preSecondAlarm;
    private TextView postSecondAlarm;
    private TextView timeStamp;

    private TextView minutes30;
    private TextView hour1;
    private TextView hour2;

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

    private int totalNumberOFFlights;

    private int test = 0;


    private String mCurrentParsedString;

    private boolean lastFlight = false;
    private boolean parsingConcatenate = false;
    private boolean allDayAlarmsSetup = false;
    private boolean isReset = false;

    private FirebaseDatabase mFireBaseDatabase;
    private DatabaseReference mDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mFireBaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFireBaseDatabase.getReference().child("flight");

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
        mDatabseReadyFilter.addAction(IntentActions.ACTION_START_NEW_PARSE_FOR_ALL_FLIGHTS);
        mDatabseReadyFilter.addAction(IntentActions.ACTION_FIRST_ALARM_COMPLETE);
        mDatabseReadyFilter.addAction(IntentActions.ACTION_INTENT_CANCEL_ALARM);
        mDatabseReadyFilter.addAction(IntentActions.ACTION_GET_PARSER);
        mDatabseReadyFilter.addAction(IntentActions.ACTION_START_ELEVEN_PM_PARSE);
        mDatabseReadyFilter.addAction(IntentActions.ACTION_START_RESET);

        mTaskTextView = findViewById(R.id.task);
        mFlightNameTextView = findViewById(R.id.flight_name);
        mCountTextView = findViewById(R.id.count);
        mUpdatingNeededTextView = findViewById(R.id.update_needed);
        mAlarmsRemainingTextView = findViewById(R.id.remaining_alarms);
        mDatabaseFoundTextView = findViewById(R.id.found_database);
        flightUpdated = findViewById(R.id.first_updated_flight);
        alarmsForReUpdating = findViewById(R.id.reupdate_alarms);
        preUpdateStatus = findViewById(R.id.update_pre_status);
        statusAfterUpdate = findViewById(R.id.status_after_update);
        systemMessage = findViewById(R.id.system_alarm);
        allDayAlarms = findViewById(R.id.tv_alarms);
        preSecondAlarm = findViewById(R.id.pre_second_alarm);
        postSecondAlarm = findViewById(R.id.post_second_alarm);
        minutes30 = findViewById(R.id.tv_15_minutes);
        hour1 = findViewById(R.id.tv_1_hour);
        hour2 = findViewById(R.id.tv_2_hours);
        timeStamp = findViewById(R.id.time_stamp);


        mFirstAlarms = 0;
        mSecondAlarm = 0;
        mAlldayAlarms = 0;
        mAllDayAlarmSet = 0;
        totalNumberOFFlights = 0;

    }

    public void startExtraction(View view) {
        //Check if database has been created
        if (!checkForCurrentDatabase()) {
            //If no database has been created, start flight info extraction.
            mArrayList = new ArrayList<>();
            count = 0;
            loaderCount = 12;
            arrayIndex = 0;
            updateArrayCount = 0;
            isReset = true;

        }

        setupTenPmAlarm();
        //setRestart();
        checkTimeFrame();

    }

    public void setRestart() {
        Intent restart = new Intent(MainActivity.this, RestartDayService.class);
        startService(restart);
    }

    public void setupAlarmsForDay() {
        //Setup up alarms for the rest of the day.ACTION_PROCESS_REMAINING_DAILY_ALARMS
        //service to do so.
        Calendar dateCalendar = Calendar.getInstance();
        mInitialHour = dateCalendar.get(Calendar.HOUR_OF_DAY);

        mAlldayAlarms = DataCheckingUtils.getNumberOfDailyAlarms(mInitialHour);
        Intent setupDaysAlarm = new Intent(MainActivity.this, AllDayAlarmService.class);
        setupDaysAlarm.putExtra(IntentActions.ACTION_SEND_ALL_DAY_ALARMS_INT, mAlldayAlarms);
        setupDaysAlarm.putExtra(IntentActions.ACTION_SEND_INITIAL_HOUR_INT, mInitialHour);
        startService(setupDaysAlarm);
    }

    public void setupTenPmAlarm() {
        Intent setupElevenPmAlarm = new Intent(MainActivity.this, SetupTenPmAlarmService.class);
        startService(setupElevenPmAlarm);
    }


    //Starts Exttraction for alarm.
    public void startAlarmExtractionExtraction() {
        //If no database has been created, start flight info extraction.
        mArrayList = new ArrayList<>();
        loaderCount = 12;
        arrayIndex = 0;
        updateArrayCount = 0;
        totalNumberOFFlights = 0;
        alarmCheckTimeFrame();
    }

    public void startConcatenateParse() {
        mArrayList = new ArrayList<>();
        loaderCount = 3;
        arrayIndex = 0;
        updateArrayCount = 0;
        parsingConcatenate = true;
        totalNumberOFFlights = 0;
        String parseString = "https://www.airport-la.com/lax/arrivals";
        startAlarmFlightExtraction(parseString);
    }

    public void restartDay() {
        mArrayList = new ArrayList<>();
        count = 0;
        loaderCount = 12;
        arrayIndex = 0;
        updateArrayCount = 0;

        mFirstAlarms = 0;
        mSecondAlarm = 0;
        mAlldayAlarms = 0;
        mAllDayAlarmSet = 0;

        mFirstAlarms = 0;
        mSecondAlarm = 0;
        mAlldayAlarms = 0;
        mAllDayAlarmSet = 0;

        totalNumberOFFlights = 0;

        lastFlight = false;

        setupAlarmsForDay();
        checkTimeFrame();

    }

    //Check if database exists
    public boolean checkForCurrentDatabase() {
        if (CheckForDatabase.checkDatabse(SQLiteFlightDBHelper.DATABASE_NAME)) {
            mDatabaseFoundTextView.setText("true");
            return true;
        }
        mDatabaseFoundTextView.setText("false");
        return false;
    }

    //Check time frame.
    public void checkTimeFrame() {
        mTaskTextView.setText("Extracting flights");
        int timeFrame = TimeManager.timeEtraction();
        pageCount = (timeFrame - 12) + 1;

        String parseString = FlightUriBuilder.buildUri(pageCount);
        startFlightExtraction(parseString);


    }

    //Check time frame.
    public void alarmCheckTimeFrame() {
        mTaskTextView.setText("Extracting flights");
        int timeFrame = TimeManager.timeEtraction();
        pageCount = (timeFrame - 12) + 1;

        String parseString = FlightUriBuilder.buildUri(pageCount);
        startAlarmFlightExtraction(parseString);


    }

    private void startFlightExtraction(String uri) {

        //Use loader
        mTaskTextView.setText(R.string.tv_extract_flights);

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

    private void startAlarmFlightExtraction(String uri) {
//        //Use Service


        //Use loader
        mTaskTextView.setText(R.string.tv_extract_flights);

        Bundle bundle = new Bundle();
        bundle.putString("parse", uri);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<FlightObject> getFlightLoader = loaderManager.getLoader(EXTRACT_FLIGHTS_LOADER);
        if (getFlightLoader == null) {
            loaderManager.initLoader(EXTRACT_FLIGHTS_LOADER, bundle, getAlarmFlightObject);
        } else {
            loaderManager.restartLoader(EXTRACT_FLIGHTS_LOADER, bundle, getAlarmFlightObject);
        }
    }

    public void parseRemainingFlights(int index, String parse) {
        if (index < 0) {
            String a = "";
        }
        mTaskTextView.setText(R.string.tv_extract_flights);
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

    public void parseAlarmRemainingFlights(int index, String parse) {
        if (index < 0) {
            String a = "";
        }
        mTaskTextView.setText(R.string.tv_extract_flights);
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        bundle.putString("parse", parse);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<FlightObject> getFlightLoader = loaderManager.getLoader(EXTRACT_FLIGHTS_STRING_LOADER);
        if (getFlightLoader == null) {
            loaderManager.initLoader(EXTRACT_FLIGHTS_STRING_LOADER, bundle, getAlarmFlightObjectString);
        } else {
            loaderManager.restartLoader(EXTRACT_FLIGHTS_STRING_LOADER, bundle, getAlarmFlightObjectString);
        }

    }

    public void getFlightTimeFrame() {
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Integer[]> getTimeFrame = loaderManager.getLoader(EXTRACT_FLIGHT_TIME_FRAME);
        if (getTimeFrame == null) {
            loaderManager.initLoader(EXTRACT_FLIGHT_TIME_FRAME, null, getFlightTimeFrames);
        } else {
            loaderManager.restartLoader(EXTRACT_FLIGHT_TIME_FRAME, null, getFlightTimeFrames);
        }
    }

    private void lowerFirstAlarm() {
        mFirstAlarms--;
        mAlarmsRemainingTextView.setText("" + mFirstAlarms);
    }

    private void lowerUpdateNeed() {
        updateArrayCount--;
        mUpdatingNeededTextView.setText("" + updateArrayCount);
    }

    public void startTimeParser(View view) {
        Intent intent = new Intent(MainActivity.this, SetupTimeFrameService.class);
        startService(intent);
    }


//  <----------------------------------Broadcast Receiver------------------------------------------>

    public class DatabaseReadyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {


            String action = intent.getAction();
            String label = intent.getStringExtra(IntentActions.INTENT_SEND_ALARM_PASS_LABEL);

            if (action.equals(IntentActions.FLIGHT_EXTRACTED_DONE)) {
                if (!parsingConcatenate) {
                    if (label.equals(IntentActions.INTENT_INITIAL)) {
                        setupAlarmsForDay();

                        ArrayList<FlightObject> arraySend = mArrayList;

                        Intent saveTodatabase = new Intent(MainActivity.this,
                                FlightIntentService.class);
                        saveTodatabase.setAction(FlightExtractionTasks.SAVE_FLIGHTS_TO_DATABASE);
                        saveTodatabase.putParcelableArrayListExtra(IntentActions.PUT_EXTRA_PARCEL_ARRAY,
                                arraySend);
                        saveTodatabase.putExtra(IntentActions.INTENT_SEND_ALARM_PASS_LABEL, IntentActions.INTENT_INITIAL);
                        mTaskTextView.setText(R.string.tv_task_saving_to_databse);
                        startService(saveTodatabase);
                    } else if (label.equals("")) {

                        ArrayList<FlightObject> arraySend = mArrayList;

                        Intent saveTodatabase = new Intent(MainActivity.this,
                                FlightIntentService.class);
                        saveTodatabase.setAction(FlightExtractionTasks.SAVE_FLIGHTS_TO_DATABASE);
                        saveTodatabase.putParcelableArrayListExtra(IntentActions.PUT_EXTRA_PARCEL_ARRAY,
                                arraySend);
                        saveTodatabase.putExtra(IntentActions.INTENT_SEND_ALARM_PASS_LABEL, "");
                        mTaskTextView.setText(R.string.tv_task_saving_to_databse);
                        startService(saveTodatabase);
                    }

                } else if (parsingConcatenate) {
                    ArrayList<FlightObject> arraySend = mArrayList;

                    Intent saveTodatabase = new Intent(MainActivity.this,
                            FlightIntentService.class);
                    saveTodatabase.setAction(FlightExtractionTasks.SAVE_FLIGHTS_TO_DATABASE);
                    saveTodatabase.putParcelableArrayListExtra(IntentActions.PUT_EXTRA_PARCEL_ARRAY,
                            arraySend);
                    saveTodatabase.putExtra(IntentActions.INTENT_SEND_ALARM_PASS_LABEL,
                            IntentActions.ACTION_INTENT_PARSE_CONCATINATE);
                    mTaskTextView.setText(R.string.tv_task_saving_to_databse);
                    startService(saveTodatabase);
                }
            }


            //The flight database was save sucecessfully.
            else if (action.equals(IntentActions.DATABASE_FLIGHT_INSERTED_SUCCESS)){
                if (label.equals(IntentActions.INTENT_INITIAL)) {
                    mTaskTextView.setText("");

                    //Extract flights that need updating.
                    Intent getFlightsForUpdate = new Intent(MainActivity.this,
                            FlightIntentService.class);
                    getFlightsForUpdate.setAction(FlightExtractionTasks.ACTION_GET_FLIGHTS_TO_UPDATE);
                    getFlightsForUpdate.putExtra(IntentActions.INTENT_SEND_ALARM_PASS_LABEL, IntentActions.INTENT_INITIAL);
                    mTaskTextView.setText("Getting flights for update");
                    startService(getFlightsForUpdate);
                    if (!allDayAlarmsSetup) {
//                        setupAlarmsForDay();
                    }
                }


            }
            //The database was not saved successfully.
            else if (action.equals(IntentActions.DATABASE_FLIGHT_INSERTED_FAILURE)) {
                if (parsingConcatenate) {
                    parsingConcatenate = false;
                }
                mTaskTextView.setText("");
                Toast.makeText(context, "FAILED", Toast.LENGTH_SHORT).show();

            }

            //Job service success received array list of flights to be updated
            else if (action.equals(IntentActions.FLIGHT_ARRAYLIST_FOR_UPDATE_EXTRACTED)) {


                mTaskTextView.setText("");
                ArrayList<FlightObject> arrayListParcelExtracted = intent.getParcelableArrayListExtra(
                        IntentActions.PUT_EXTRA_PARCEL_ARRAY);
                mArrayListUpdate = arrayListParcelExtracted;


                Intent alarmIntent = new Intent(MainActivity.this,
                        AlarmService.class);
                alarmIntent.putParcelableArrayListExtra(IntentActions.PUT_EXTRA_PARCEL_ARRAY,
                        arrayListParcelExtracted);
                alarmIntent.putExtra(IntentActions.INTENT_SEND_INT, arrayIndex);

                mTaskTextView.setText("Setting alarms");
                updateArrayCount = arrayListParcelExtracted.size();
                mUpdatingNeededTextView.setText("" + updateArrayCount);


                startService(alarmIntent);

            } else if (action.equals(IntentActions.ACTION_CONTINUE_SETTING_ALARMS))

            {
                lowerUpdateNeed();

                arrayIndex++;

                mTaskTextView.setText("");
                mFirstAlarms++;
                mAlarmsRemainingTextView.setText("" + mFirstAlarms);

                ArrayList<FlightObject> updatedArrayslIst = intent.getParcelableArrayListExtra(
                        IntentActions.PUT_EXTRA_PARCEL_ARRAY);
                mArrayListUpdate = new ArrayList<FlightObject>();
                mArrayListUpdate = updatedArrayslIst;

                if (arrayIndex == mArrayListUpdate.size()) {
                    Intent intentUpdateRequestCode = new Intent(MainActivity.this,
                            FlightIntentService.class);
                    intentUpdateRequestCode.setAction(FlightExtractionTasks.ACTION_UPDATE_ALARMS_REQUEST_CODE);
                    intentUpdateRequestCode.putParcelableArrayListExtra(IntentActions.ACTION_SEND_PARCEL_REQUEST_CODE,
                            mArrayListUpdate);
                    startService(intentUpdateRequestCode);
                } else {
                    int time = intent.getIntExtra(IntentActions.INTENT_SEND_SECOND_INT, -2);
                    Intent alarmIntent = new Intent(MainActivity.this,
                            AlarmService.class);
                    alarmIntent.putParcelableArrayListExtra(IntentActions.PUT_EXTRA_PARCEL_ARRAY,
                            mArrayListUpdate);
                    int size = mArrayListUpdate.size();

                    alarmIntent.putExtra(IntentActions.INTENT_SEND_INT, arrayIndex);
                    alarmIntent.putExtra(IntentActions.INTENT_SEND_SECOND_INT, time);
                    mTaskTextView.setText("Setting alarms");
                    startService(alarmIntent);
                }

            } else if (action.equals(IntentActions.ACTION_CURRENT_ARRAYLIST_REQUEST_CODES_COMPLETED))

            {


                systemMessage.setText("All request code batches competed.");


            } else if (action.equals(IntentActions.ACTION_GET_STATUS_FLIGHT)){


                mTaskTextView.setText("Get status of flight");
                long flightId = intent.getLongExtra(IntentActions.INTENT_SEND_FLIGHT_COLUMN_ID, -2);
                String flightName = intent.getStringExtra(IntentActions.INTENT_SEND_STRING);
                String status = intent.getStringExtra(IntentActions.INTENT_SEND_FLIGHT_STATUS);
                int time = intent.getIntExtra(IntentActions.INTENT_SEND_SECOND_INT, -2);
                int scheduleTime = intent.getIntExtra(IntentActions.INTENT_SEND_THRID_INT, -2);

                flightUpdated.setText("");
                preUpdateStatus.setText("");
                statusAfterUpdate.setText("");


                Intent getFlightStatus = new Intent(MainActivity.this, FlightIntentService.class);
                getFlightStatus.setAction(FlightExtractionTasks.ACTION_EXTRACT_SINGLE_FLIGHT_INFORMATION);
                getFlightStatus.putExtra(IntentActions.INTENT_SEND_FLIGHT_COLUMN_ID, flightId);
                getFlightStatus.putExtra(IntentActions.INTENT_SEND_STRING_FLIGHT, flightName);
                getFlightStatus.putExtra(IntentActions.INTENT_SEND_FLIGHT_STATUS, status);
                getFlightStatus.putExtra(IntentActions.INTENT_SEND_SECOND_INT, time);
                getFlightStatus.putExtra(IntentActions.INTENT_SEND_THRID_INT, scheduleTime);
                startService(getFlightStatus);
            } else if (action.equals(IntentActions.ACTION_FIRST_ALARM_COMPLETE))

            {
                lowerFirstAlarm();
            }

            //Start loader that will update the database.
            else if (action.equals(IntentActions.FLIGHT_UPDATE_DATABASE_STATUS)){
                FlightObject object = intent.getExtras().getParcelable(IntentActions.INTENT_SEND_PARCEL);
                String name = object.getFlightName();
                String pre = object.getFlightStatus();
                String post = object.getPostStatus();

                flightUpdated.setText(name);
                preUpdateStatus.setText(pre);
                statusAfterUpdate.setText(post);


                Intent updateStatus = new Intent(MainActivity.this, FlightIntentService.class);
                updateStatus.setAction(FlightExtractionTasks.ACTION_DATABASE_UPDATE_STATUS);
                updateStatus.putExtra(IntentActions.INTENT_SEND_PARCEL, object);
                startService(updateStatus);
            }
            //sets up second alarm
            else if (action.equals(IntentActions.ACTION_SETUP_SECOND_ALARM))

            {
                mTaskTextView.setText("Setting up second alarm");

                FlightObject parcelForSecondAlarm = intent.getParcelableExtra(
                        IntentActions.INTENT_SEND_PARCEL_FOR_SECOND_ALARM);
                Intent secondAlarmSetup = new Intent(MainActivity.this, SecondAlarmService.class);
                secondAlarmSetup.putExtra(IntentActions.INTENT_SEND_PARCEL_TO_SECOND_ALARM_SERVICE,
                        parcelForSecondAlarm);
                startService(secondAlarmSetup);
            }
            //Second alarm was successfully setup
            else if (action.equals(IntentActions.ACTION_SECOND_ALARM_SUCCESSUFULLY_SETUP))

            {
                mTaskTextView.setText("");
                mSecondAlarm++;
                alarmsForReUpdating.setText("" + mSecondAlarm);


            } else if (action.equals(IntentActions.ACTION_GET_SECOND_STATUS_FLIGHT))

            {
                mTaskTextView.setText("Getting second status");

                long flightId = intent.getLongExtra(IntentActions.INTENT_SEND_FLIGHT_COLUMN_ID, -2);
                String flightName = intent.getStringExtra(IntentActions.INTENT_SEND_STRING);
                String flightStatus = intent.getStringExtra(IntentActions.INTENT_SEND_FLIGHT_STATUS);
                int requestCode = intent.getIntExtra(IntentActions.INTENT_REQUEST_CODE, -2);

                preSecondAlarm.setText("" + flightStatus);


                Intent getSeconfFlightStatus = new Intent(MainActivity.this, FlightIntentService.class);
                getSeconfFlightStatus.setAction(FlightExtractionTasks.ACTION_EXTRACT_SINGLE_FLIGHT_INFO_FOR_SECOND_ALARM);
                getSeconfFlightStatus.putExtra(IntentActions.INTENT_SEND_FLIGHT_COLUMN_ID, flightId);
                getSeconfFlightStatus.putExtra(IntentActions.INTENT_SEND_STRING_FLIGHT, flightName);
                getSeconfFlightStatus.putExtra(IntentActions.INTENT_REQUEST_CODE, requestCode);
                startService(getSeconfFlightStatus);

            } else if (action.equals(IntentActions.ACTION_SECOND_ALARM_COMPLETE))

            {
                mTaskTextView.setText("");
                mSecondAlarm--;
                alarmsForReUpdating.setText("" + mSecondAlarm);

            } else if (action.equals(IntentActions.ACTION_PROCESS_REMAINING_DAILY_ALARMS))

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
            } else if (action.equals(IntentActions.ACTION_NOT_INITIAL_DATABASE_SAVE_SUCCESSFUL)) {
                if (parsingConcatenate) {
                    parsingConcatenate = false;
                }
                int newFlightNumber = intent.getIntExtra(IntentActions.INTENT_SENDING_NEW_FLIGHTS_DATABASE, -2);
                if (newFlightNumber > 0) {
                    for (int i = 0; i < newFlightNumber; i++) {
                        count = count + 1;
                        mCountTextView.setText("" + count);
                    }
                    Intent getNewFlights = new Intent(MainActivity.this, FlightIntentService.class);
                    getNewFlights.setAction(FlightExtractionTasks.ACTION_EXTRACT_FLIGHTS_NO_ALARM);
                    getNewFlights.putExtra(IntentActions.INTENT_SEND_ALARM_PASS_LABEL, "");
                    mTaskTextView.setText("Getting flights for update");
                    startService(getNewFlights);

                }

            } else if (action.equals(IntentActions.ACTION_INTENT_CANCEL_ALARM)) {
                int requestCode = intent.getIntExtra(IntentActions.INTENT_REQUEST_CODE, -2);
                String flightName = intent.getStringExtra(IntentActions.INTENT_SEND_STRING_FLIGHT);
                Intent cancelAlarm = new Intent(MainActivity.this, ProcessSecondAlarmReceiver.class);
                PendingIntent alarm = (PendingIntent.getBroadcast(
                        MainActivity.this,
                        requestCode,
                        cancelAlarm,
                        PendingIntent.FLAG_UPDATE_CURRENT));
                AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (alarmMgr != null) {
                    alarmMgr.cancel(alarm);
                    mTaskTextView.setText("");
                    mSecondAlarm--;
                    alarmsForReUpdating.setText("" + mSecondAlarm);
                    Toast.makeText(MainActivity.this, "Alarm Canceled: " + flightName, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Alarm not canceled: " + flightName, Toast.LENGTH_SHORT).show();
                }

            } else if (action.equals(IntentActions.ACTION_START_NEW_PARSE_FOR_ALL_FLIGHTS))

            {

                startAlarmExtractionExtraction();

            } else if (action.equals(IntentActions.ACTION_GET_PARSER))

            {

                getFlightTimeFrame();
            } else if (action.equals(IntentActions.ACTION_START_ELEVEN_PM_PARSE))

            {
                isReset = false;
                startConcatenateParse();
            } else if (action.equals(IntentActions.ACTION_START_RESET))

            {
                restartDay();
            } else if (action.equals(IntentActions.INTENT_SEND_TOAST))

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
                        mTaskTextView.setText("");

                        loaderCount--;
                        if (loaderCount > 0) {
                            pageCount++;
                            String parseString = FlightUriBuilder.buildUri(pageCount);
                            if (parseString.equals("https://www.airport-la.com/lax/arrivals?t=1")) {

                            }
                            startFlightExtraction(parseString);
                        }
                    } else {
                        mFlightNameTextView.setText(data.getFlightName());
                        mArrayList.add(data);
                        count++;
                        mCountTextView.setText("" + count);
                        mCurrentParsedString = data.getParsedString();
                        if (data.getIsLastFlight()) {
                            lastFlight = true;
                            mTaskTextView.setText("");

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
                        totalNumberOFFlights = count;
                        //Setup broadcast here
                        Intent broadcastIntent = new Intent(IntentActions.FLIGHT_EXTRACTED_DONE);
                        broadcastIntent.putExtra(IntentActions.INTENT_SEND_ALARM_PASS_LABEL, IntentActions.INTENT_INITIAL);
                        sendBroadcast(broadcastIntent);
                        Toast.makeText(MainActivity.this, "Extraction Complete", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onLoaderReset(Loader<FlightObject> loader) {

                }
            };
    private final LoaderManager.LoaderCallbacks<FlightObject> getAlarmFlightObject =
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
                        mTaskTextView.setText("");

                        loaderCount--;
                        if (loaderCount > 0) {
                            pageCount++;
                            String parseString = FlightUriBuilder.buildUri(pageCount);
                            startAlarmFlightExtraction(parseString);
                        }
                    } else {
                        mFlightNameTextView.setText(data.getFlightName());
                        mArrayList.add(data);
                        mCurrentParsedString = data.getParsedString();
                        if (data.getIsLastFlight()) {
                            lastFlight = true;
                            mTaskTextView.setText("");

                            loaderCount--;
                            if (loaderCount > 0) {
                                pageCount++;
                                String parseString = FlightUriBuilder.buildUri(pageCount);
                                startAlarmFlightExtraction(parseString);
                            }
                            String a = "";
                        } else if (!data.getIsLastFlight()) {
                            int index = data.getNextFlightIndex();
                            String parse = data.getParsedString();
                            parseAlarmRemainingFlights(index, parse);
                        }
                    }
                    if (loaderCount == 0) {
                        totalNumberOFFlights = count;
                        //Setup broadcast here
                        totalNumberOFFlights = count;
                        Intent broadcastIntent = new Intent(IntentActions.FLIGHT_EXTRACTED_DONE);
                        broadcastIntent.putExtra(IntentActions.INTENT_SEND_ALARM_PASS_LABEL, "");
                        if (parsingConcatenate) {
                            parsingConcatenate = false;
                        }

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
                    mFlightNameTextView.setText(data.getFlightName());
                    mArrayList.add(data);
                    count++;
                    mCountTextView.setText("" + count);
                    mCurrentParsedString = data.getParsedString();
                    if (data.getIsLastFlight()) {
                        lastFlight = true;
                        mTaskTextView.setText("");

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
                        totalNumberOFFlights = count;
                        //Setup broadcast here
                        Intent broadcastIntent = new Intent(IntentActions.FLIGHT_EXTRACTED_DONE);
                        broadcastIntent.putExtra(IntentActions.INTENT_SEND_ALARM_PASS_LABEL, IntentActions.INTENT_INITIAL);
                        sendBroadcast(broadcastIntent);
                    }
                }

                @Override
                public void onLoaderReset(Loader<FlightObject> loader) {

                }
            };

    private final LoaderManager.LoaderCallbacks<FlightObject> getAlarmFlightObjectString =
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
                    mFlightNameTextView.setText(data.getFlightName());
                    mArrayList.add(data);
                    mCurrentParsedString = data.getParsedString();
                    if (data.getIsLastFlight()) {
                        lastFlight = true;
                        mTaskTextView.setText("");
                        if (parsingConcatenate) {
                            loaderCount--;
                            if (loaderCount == 2) {

                                String parseString = "https://www.airport-la.com/lax/arrivals?t=1";
                                startAlarmFlightExtraction(parseString);
                            } else if (loaderCount == 1) {
                                String parseString = "https://www.airport-la.com/lax/arrivals?t=2";
                                startAlarmFlightExtraction(parseString);
                            }
                        } else if (!parsingConcatenate) {

                            loaderCount--;
                            if (loaderCount > 0) {
                                pageCount++;
                                String parseString = FlightUriBuilder.buildUri(pageCount);
                                startAlarmFlightExtraction(parseString);
                            }
                        }
                        //Next page
                    } else if (!data.getIsLastFlight()) {
                        parseAlarmRemainingFlights(data.getNextFlightIndex(),
                                data.getParsedString());
                    }
                    if (loaderCount == 0) {
                        totalNumberOFFlights = count;
                        //Setup broadcast here
                        Intent broadcastIntent = new Intent(IntentActions.FLIGHT_EXTRACTED_DONE);
                        broadcastIntent.putExtra(IntentActions.INTENT_SEND_ALARM_PASS_LABEL, "");
                        if (parsingConcatenate) {
                            parsingConcatenate = true;
                        }
                        sendBroadcast(broadcastIntent);

                    }
                }

                @Override
                public void onLoaderReset(Loader<FlightObject> loader) {

                }
            };

    private final LoaderManager.LoaderCallbacks<ArrayList<Integer>> getFlightTimeFrames =
            new LoaderManager.LoaderCallbacks<ArrayList<Integer>>() {
                @NonNull
                @Override
                public Loader<ArrayList<Integer>> onCreateLoader(int id, @Nullable Bundle args) {
                    return new GetFlightsTimeFrames(MainActivity.this, isReset);
                }

                @Override
                public void onLoadFinished(@NonNull Loader<ArrayList<Integer>> loader, ArrayList<Integer> data) {
                    if (data != null) {
                        int length = data.size();
                        if (length == 1) {

                            minutes30.setText("" + data.get(0));
                        } else if (length == 2) {
                            minutes30.setText("" + data.get(0));
                            hour1.setText("" + data.get(1));
                        } else if (length == 3) {
                            minutes30.setText("" + data.get(0));
                            hour1.setText("" + data.get(1));
                            hour2.setText("" + data.get(2));
                        }
                        String currentDateTimeString = DateFormat.getTimeInstance(DateFormat.SHORT)
                                .format(new Date());

                        timeStamp.setText(currentDateTimeString);
                        if (data.size() == 0) {
                            data.add(0, 0);
                            data.add(1, 0);
                            data.add(2, 0);
                        }
                        if (data.size() == 1) {
                            data.add(1, 0);
                        }
                        if (data.size() == 2) {
                            data.add(1, 0);
                            data.add(2, 0);
                        }

                        FlightSyncObject sync = new FlightSyncObject(data.get(0),
                                data.get(1),
                                data.get(2),
                                currentDateTimeString,
                                String.valueOf(count));
                        mDatabaseReference.push().setValue(sync);

                    }
                }

                @Override
                public void onLoaderReset(@NonNull Loader<ArrayList<Integer>> loader) {

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

    public void testMethod(View view) {

    }

    public void cancelAlarm(View view) {
        Intent intent = new Intent(IntentActions.ACTION_INTENT_CANCEL_ALARM);
        sendBroadcast(intent);
    }
}
