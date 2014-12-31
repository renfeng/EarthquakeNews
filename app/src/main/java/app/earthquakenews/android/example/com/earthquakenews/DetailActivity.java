package app.earthquakenews.android.example.com.earthquakenews;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import app.earthquakenews.android.example.com.earthquakenews.data.EarthquakeContract;


public class DetailActivity extends ActionBarActivity {
    public static final String DATE_TIME= "earthquake_date";
    public static final String LOCATION = "location";
    public static String latitudeMap;
    public static String longitudeMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            String dateTime = getIntent().getStringExtra(DATE_TIME);
            String location = getIntent().getStringExtra(LOCATION);
            Bundle arguments = new Bundle();
            arguments.putString(DetailActivity.DATE_TIME, dateTime);
            arguments.putString(DetailActivity.LOCATION,location);
            PlaceholderFragment fragment = new PlaceholderFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_map) {
            openPreferredLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void openPreferredLocationInMap() {



        String uri = "geo:0,0?q="+ latitudeMap + "," + longitudeMap;
        startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));

    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        private static final String LOG_TAG = PlaceholderFragment.class.getSimpleName();
        private static final String EARTHQUAKE_SHARE_HASHTAG = " #Earthquake";
        private String location;
        private String dateTime;
        private String mEarthquakeStr;

        private static final int DETAIL_LOADER = 0;
        private static final String[] EARTHQUAKE_COLUMNS = {
                EarthquakeContract.EarthquakeEntry.TABLE_NAME + "." + EarthquakeContract.EarthquakeEntry._ID,
                EarthquakeContract.EarthquakeEntry.COLUMN_TITLE,
                EarthquakeContract.EarthquakeEntry.COLUMN_MAGNITUDE,
                EarthquakeContract.EarthquakeEntry.COLUMN_LOCATION,
                EarthquakeContract.EarthquakeEntry.COLUMN_DATE_TIME,
                EarthquakeContract.EarthquakeEntry.COLUMN_DEPTH,
                EarthquakeContract.EarthquakeEntry.COLUMN_LATITUDE,
                EarthquakeContract.EarthquakeEntry.COLUMN_LONGITUDE

        };

        private TextView dTitleView;
        private TextView dMagnitudeView;
        private ImageView dImageIcon;
        private TextView dDepthView;
        private TextView dLatitudeView;
        private TextView dLongitudeView;

        public PlaceholderFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public void onResume() {
            Bundle arguments = getArguments();
            if (arguments != null && arguments.containsKey(DetailActivity.DATE_TIME) &&
                    arguments.containsKey(DetailActivity.LOCATION) ) {
                getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
            }
            super.onResume();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Bundle arguments = getArguments();
            if(arguments != null){
            dateTime =arguments.getString(DetailActivity.DATE_TIME);
                location = arguments.getString(DetailActivity.LOCATION);
            }


            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            dTitleView = (TextView) rootView.findViewById(R.id.detail_title);
            dMagnitudeView=(TextView)rootView.findViewById(R.id.detail_magnitude);
            dImageIcon = (ImageView) rootView.findViewById(R.id.detail_image);
            dDepthView = (TextView) rootView.findViewById(R.id.detail_depth);
            dLatitudeView=(TextView) rootView.findViewById(R.id.detail_latitude);
            dLongitudeView = (TextView) rootView.findViewById(R.id.detail_longitude);

            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.detail_fragment, menu);
// Retrieve the share menu item
            MenuItem menuItem = menu.findItem(R.id.action_share);
// Get the provider and hold onto it to set/change the share intent.
            ShareActionProvider mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
// Attach an intent to this ShareActionProvider. You can update this at any time,
// like when the user selects a new piece of data they might like to share.
            if (mShareActionProvider != null ) {
                mShareActionProvider.setShareIntent(createShareEarthquakeIntent());
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }
    }
        private Intent createShareEarthquakeIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    mEarthquakeStr + EARTHQUAKE_SHARE_HASHTAG);
            return shareIntent;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            if (savedInstanceState != null) {
                location = savedInstanceState.getString(LOCATION);
                dateTime = savedInstanceState.getString(DATE_TIME);
            }
            Bundle arguments = getArguments();
            if (arguments != null && arguments.containsKey(DetailActivity.DATE_TIME) && arguments.containsKey(DetailActivity.LOCATION)) {
                getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            }
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.v(LOG_TAG, "In onCreateLoader");



            String sortOrder = EarthquakeContract.EarthquakeEntry.COLUMN_DATE_TIME + " ASC";

            Uri EarthquakeUri = EarthquakeContract.EarthquakeEntry.buildEarthquakeWithDateAndLocation(
                    dateTime, location);
            Log.v(LOG_TAG, EarthquakeUri.toString());

            return new CursorLoader(
                    getActivity(),
                    EarthquakeUri,
                    EARTHQUAKE_COLUMNS,
                    null,
                    null,
                    sortOrder
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.v(LOG_TAG, "In onLoadFinished");
            if (!data.moveToFirst()) { return; }

            String magnitude =
                    data.getString(data.getColumnIndex(EarthquakeContract.EarthquakeEntry.COLUMN_MAGNITUDE));
            dMagnitudeView.setText("Magnitude : "+magnitude);
            dImageIcon.setImageResource(EarthquakeAdapter.getIconResourceForMagnitude(Double.valueOf(magnitude)));
            String title =
                    data.getString(data.getColumnIndex(EarthquakeContract.EarthquakeEntry.COLUMN_TITLE));
              dTitleView.setText(title);

            String dateTime =
                    data.getString(data.getColumnIndex(EarthquakeContract.EarthquakeEntry.COLUMN_DATE_TIME));
            String depth = data.getString(data.getColumnIndex(EarthquakeContract.EarthquakeEntry.COLUMN_DEPTH));
            dDepthView.setText("Depth: "+depth + " Km");

            String latitude = data.getString(data.getColumnIndex(EarthquakeContract.EarthquakeEntry.COLUMN_LATITUDE));
            dLatitudeView.setText("Latitude: " +latitude);
            latitudeMap = latitude;

            String longitude = data.getString(data.getColumnIndex(EarthquakeContract.EarthquakeEntry.COLUMN_LONGITUDE));
            dLongitudeView.setText("Longitude: " +longitude);
            longitudeMap = longitude;
            mEarthquakeStr = String.format("%s - %s - %s", magnitude, location, dateTime);
            Log.v(LOG_TAG, "Forecast String: " + mEarthquakeStr);

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}
