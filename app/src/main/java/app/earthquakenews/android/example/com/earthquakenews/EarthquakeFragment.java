package app.earthquakenews.android.example.com.earthquakenews;


import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

import app.earthquakenews.android.example.com.earthquakenews.data.EarthquakeContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class EarthquakeFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    private EarthquakeAdapter earthquakeAdapter;
    private static final int EARTHQUAKE_LOADER = 0;
    private ListView listView;
    private int position = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private static final String[] EARTHQUAKE_COLUMNS = {

            EarthquakeContract.EarthquakeEntry.TABLE_NAME + "." + EarthquakeContract.EarthquakeEntry._ID,
            EarthquakeContract.EarthquakeEntry.COLUMN_TITLE,
            EarthquakeContract.EarthquakeEntry.COLUMN_MAGNITUDE,
            EarthquakeContract.EarthquakeEntry.COLUMN_LOCATION,
            EarthquakeContract.EarthquakeEntry.COLUMN_DEPTH,
            EarthquakeContract.EarthquakeEntry.COLUMN_LATITUDE,
            EarthquakeContract.EarthquakeEntry.COLUMN_LONGITUDE,
            EarthquakeContract.EarthquakeEntry.COLUMN_DATE_TIME
    };

    public static final int COL_EARTHQUAKE_ID = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_MAGNITUDE = 2;
    public static final int COL_LOCATION = 3;
    public static final int COL_DEPTH = 4;
    public static final int COL_LATITUDE = 5;
    public static final int COL_LONGITUDE = 6;
    public static final int COL_DATE_TIME = 7;

    public interface Callback {

        public void onItemSelected(String dateTime,String location);
    }


    public EarthquakeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_earthquake, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            UpdateEarthquakeInfo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        earthquakeAdapter = new EarthquakeAdapter(getActivity(),null,0);

        View rootView = inflater.inflate(R.layout.fragment_earthquake, container, false);

        listView = (ListView) rootView.findViewById(R.id.listview_earthquake);
        listView.setAdapter(earthquakeAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = earthquakeAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(i)) {
                    ((Callback) getActivity())
                            .onItemSelected(cursor.getString(COL_DATE_TIME), cursor.getString(COL_LOCATION));

                }
                position = i ;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {

            position = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(EARTHQUAKE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void UpdateEarthquakeInfo(){
        FetchEarthquakeTask earthquakeTask = new FetchEarthquakeTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String magnitude = prefs.getString(getString(R.string.pref_magnitude_key),
                getString(R.string.pref_magnitude_default));
        earthquakeTask.execute(magnitude);
    }

    @Override
    public void onStart() {
        super.onStart();
        UpdateEarthquakeInfo();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (position != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, position);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        getLoaderManager().restartLoader(EARTHQUAKE_LOADER, null, this);
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = EarthquakeContract.EarthquakeEntry.COLUMN_DATE_TIME + " DESC";
        Uri weatherForLocationUri = EarthquakeContract.EarthquakeEntry.buildEarthquakeUri(
              );
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                EARTHQUAKE_COLUMNS,
                null,
                null,
                sortOrder
        );

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


        if (position != ListView.INVALID_POSITION) {

            listView.smoothScrollToPosition(position);
        }
        earthquakeAdapter.swapCursor(data);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        earthquakeAdapter.swapCursor(null);
    }

    public class FetchEarthquakeTask extends AsyncTask<String, Void, String[]> {
        Context mContext;

        private boolean DEBUG = true;

        private final String LOG_TAG = FetchEarthquakeTask.class.getSimpleName();

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        private void getEarthquakeDataFromJson(String EarthQuakeJsonStr,double magnitudePref)
                throws JSONException {
// These are the names of the JSON objects that need to be extracted.
            final String E_TITLE = "title";
            final String E_MAGNITUDE = "magnitude";
            final String E_LOCATION = "location";
            final String E_DEPTH = "depth";
            final String E_LATITUDE = "latitude";
            final String E_LONGITUDE = "longitude";
            final String E_DATE_TIME = "date_time";

            JSONArray earthquakeJsonArray = new JSONArray(EarthQuakeJsonStr);
            JSONObject earthquakeJsonObject;
            ArrayList<String> resultArray = new ArrayList<>();
            String[] resultStrs = new String[earthquakeJsonArray.length()];

            Vector<ContentValues> cVVector = new Vector<ContentValues>();
            getActivity().getContentResolver().delete(EarthquakeContract.EarthquakeEntry.CONTENT_URI,null,null);
            for (int i = 0; i < earthquakeJsonArray.length(); i++) {
                earthquakeJsonObject = earthquakeJsonArray.getJSONObject(i);
                String title = earthquakeJsonObject.getString(E_TITLE);
                String magnitude = earthquakeJsonObject.getString(E_MAGNITUDE);
                String location = earthquakeJsonObject.getString(E_LOCATION);
                String depth = earthquakeJsonObject.getString(E_DEPTH);
                String latitude = earthquakeJsonObject.getString(E_LATITUDE);
                String longitude = earthquakeJsonObject.getString(E_LONGITUDE);
                String date_time = earthquakeJsonObject.getString(E_DATE_TIME);
                Log.e("Islam",String.valueOf(Double.valueOf(magnitude) >= Double.valueOf(magnitudePref)));

                if(Double.valueOf(magnitude) >= Double.valueOf(magnitudePref)) {


                    ContentValues EarthquakeValues = new ContentValues();
                    EarthquakeValues.put(EarthquakeContract.EarthquakeEntry.COLUMN_TITLE,title);
                    EarthquakeValues.put(EarthquakeContract.EarthquakeEntry.COLUMN_MAGNITUDE,magnitude);
                    EarthquakeValues.put(EarthquakeContract.EarthquakeEntry.COLUMN_LOCATION,location);
                    EarthquakeValues.put(EarthquakeContract.EarthquakeEntry.COLUMN_DEPTH,depth);
                    EarthquakeValues.put(EarthquakeContract.EarthquakeEntry.COLUMN_LATITUDE,latitude);
                    EarthquakeValues.put(EarthquakeContract.EarthquakeEntry.COLUMN_LONGITUDE,longitude);
                    EarthquakeValues.put(EarthquakeContract.EarthquakeEntry.COLUMN_DATE_TIME,date_time.substring(0,19));
                    resultArray.add( location + "-" + magnitude + "-" + date_time.substring(0,19));

                    cVVector.add(EarthquakeValues);
                }


            }

            resultStrs = resultArray.toArray(new String[resultArray.size()]);
            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Earthquake entry: " + s);
            }
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);



                int rowsInserted = getActivity().getContentResolver()
                        .bulkInsert(EarthquakeContract.EarthquakeEntry.CONTENT_URI, cvArray);
                Log.v(LOG_TAG, "inserted " + rowsInserted + " rows of weather data");

            }
        }

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String EarthQuakeJsonStr = null;

            try {

                final String EARTHQUAKE_BASE_URL =
                        "http://earthquake-report.com/feeds/recent-eq?json";


                URL url = new URL(EARTHQUAKE_BASE_URL);
                Log.e(LOG_TAG, "Built URI " + EARTHQUAKE_BASE_URL);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    Log.e("ISLAM", "Empty");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    Log.e("BESTO", "Empty");
                    return null;
                }
                EarthQuakeJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Earthquake string: " + EarthQuakeJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
// If the code didn't successfully get the weather data, there's no point in attemping
// to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                getEarthquakeDataFromJson(EarthQuakeJsonStr,Double.valueOf(params[0]));
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
// This will only happen if there was an error getting or parsing the forecast.
            return null;

        }



    }

}
