package com.example.rcarb.flightservice.utilities;

/**
 * Created by rcarb on 2/21/2018.
 */

public class IntentActions {

    //Tasks
    public final static String FLIGHT_EXTRACTED_DONE ="flights-extracted-completed";

    public final static String PUT_EXTRA_PARCEL_ARRAY = "put-extra-parcel-array";

    public final static String DATABASE_FLIGHT_INSERTED_SUCCESS ="databse-insert-sucess";

    public final static String DATABASE_FLIGHT_INSERTED_FAILURE = "database-insert-failed";

    public final static String FLIGHT_ARRAYLIST_FOR_UPDATE_EXTRACTED ="extract-flight-array";

    public final static String ALARM_SET = "alarm-set";

    public final static String ALARM_FIRED_FLIGHT_UPDATED = "fired-alarm";

    public final static String FLIGHT_UPDATE_DATABASE_STATUS = "update-status";

    public final static String ACTION_GET_STATUS_FLIGHT = "get-flight-status";

    public final static String ACTION_CONTINUE_SETTING_ALARMS = "continue-alarms";

    public final static String ACTION_SETUP_SECOND_ALARM = "setup-second-alarm";

    public final static String ACTION_SECOND_ALARM_SUCCESSUFULLY_SETUP = "second-alarm-successful";

    public final static String ACTION_GET_SECOND_STATUS_FLIGHT = "get-second-status";

    public final static String ACTION_SECOND_ALARM_COMPLETE = "second-alarm-complete";

    public final static String ACTION_CURRENT_ARRAYLIST_REQUEST_CODES_COMPLETED = "request=codes=completed";

    public final static String ACTION_SEND_PARCEL_REQUEST_CODE = "parcel-request-code";

    public final static String ACTION_SEND_ALL_DAY_ALARMS_INT ="all-day-alarms";

    public final static String ACTION_SEND_INITIAL_HOUR_INT ="initial-hour-int";

    public final static String ACTION_PROCESS_REMAINING_DAILY_ALARMS = "process-remaining-daily-alarms";

    public final static String ACTION_NOT_INITIAL_DATABASE_SAVE_SUCCESSFUL = "save-not initial";

    public final static String ACTION_START_INITIAL_DATABASE_SAVE = "initial-datatabse-save";


    public final static String ACTION_RECEIVED_ALARM = "alarm-received";


    public final static String JOB_SERVICE_SCHEDULED_SUCCESS = "job-scheduled-success";

    //General variables
    public final static String INTENT_SEND_STRING = "send-string";
    public final static String INTENT_SEND_INT = "send-int";
    public final static String INTENT_SEND_SECOND_INT = "send-second-int";
    public final static String INTENT_SEND_LONG = "send-long";
    public final static String INTENT_SEND_TOAST = "send-toast";
    public final static String INTENT_SEND_PARCEL = "send-parcel";
    public final static String INTENT_SEND_TOAST_MESSAGE = "toast-message";
    public final static String INTENT_SEND_TOAST_SPECIAL = "toast-special";
    public final static String INTENT_SEND_PARCEL_FOR_SECOND_ALARM = "send-second-alarm-parcel";
    public final static String INTENT_SEND_PARCEL_TO_SECOND_ALARM_SERVICE = "send-second-alarm-to-service";


    //Flight Info
    public final static String INTENT_REQUEST_CODE = "request-code";
    public final static String INTENT_SEND_STRING_FLIGHT = "send-string-flight";
    public final static String INTENT_SEND_FLIGHT_COLUMN_ID = "column-id";
    public final static String INTENT_SEND_FLIGHT_STATUS = "send-flight-status";
}
