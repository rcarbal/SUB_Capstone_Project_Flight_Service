package com.example.rcarb.flightservice;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;

import com.example.rcarb.flightservice.data.CheckForDatabase;
import com.example.rcarb.flightservice.data.FlightContract;
import com.example.rcarb.flightservice.data.SQLiteFlightDBHelper;
import com.example.rcarb.flightservice.loaders.GetFlightObjectLoader;
import com.example.rcarb.flightservice.loaders.GetFlightsFromParsedStringLoader;
import com.example.rcarb.flightservice.objects.FlightObject;
import com.example.rcarb.flightservice.service.FlightIntentService;
import com.example.rcarb.flightservice.utilities.FlightExtractionTasks;
import com.example.rcarb.flightservice.utilities.TimeManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<FlightObject> arrayList;

    private static final int EXTRACT_FLIGHTS_LOADER = 1;
    private static final int EXTRACT_FLIGHTS_STRING_LOADER = 2;

    private TextView taskText;
    private TextView flightNameText;
    private TextView countText;

    private int count;
    private int loaderCount;
    private int pageCount;

    private boolean lastFlight = false;
    private String mCurrentParsedString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskText = findViewById(R.id.task);
        flightNameText = findViewById(R.id.flight_name);
        countText = findViewById(R.id.count);

        //Check if database has been created
        if (!checkForCurrentDatabase()) {
            //If no database has been created, start flight info extraction.
            arrayList = new ArrayList<>();
            count = 0;
            loaderCount = 12;
            checkTimeFrame();
        }
    }

    //Check if database exists
    private boolean checkForCurrentDatabase() {
        return CheckForDatabase.checkDatabse(SQLiteFlightDBHelper.DATABASE_NAME);

    }

    //Check time frame.
    private void checkTimeFrame() {
        int timeFrame = TimeManager.timeEtraction();
        pageCount = (timeFrame - 12) + 1;

        String parseString = FlightUriBuilder.buildUri(pageCount);
        startFlightExtraction(parseString);


    }

    private void startFlightExtraction(String uri) {
        //Use Service
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
                                String a = "";
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
                        } else if (!data.getIsLastFlight()) {
                            int index = data.getNextFlightIndex();
                            String parse = data.getParsedString();
                            parseRemainingFlights(index, parse);
                        }
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
                            if (parseString.equals("https://www.airport-la.com/lax/arrivals?t=1")) {
                                String a = "";
                            }
                            startFlightExtraction(parseString);
                        }

                        //Next page
                    } else if (!data.getIsLastFlight()) {
                        parseRemainingFlights(data.getNextFlightIndex(),
                                data.getParsedString());
                    }
                }

                @Override
                public void onLoaderReset(Loader<FlightObject> loader) {

                }
            };
}
