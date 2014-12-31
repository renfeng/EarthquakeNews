package app.earthquakenews.android.example.com.earthquakenews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements EarthquakeFragment.Callback{
    private boolean mTwoPane;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (findViewById(R.id.weather_detail_container) != null) {

            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailActivity.PlaceholderFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        Log.v(LOG_TAG, "in onStart");
        super.onStart();
// The activity is about to become visible.
    }
    @Override
    protected void onResume() {
        Log.v(LOG_TAG, "in onResume");
        super.onResume();
// The activity has become visible (it is now "resumed").
    }
    @Override
    protected void onPause() {
        Log.v(LOG_TAG, "in onPause");
        super.onPause();
// Another activity is taking focus (this activity is about to be "paused").
    }
    @Override
    protected void onStop() {
        Log.v(LOG_TAG, "in onStop");
        super.onStop();
// The activity is no longer visible (it is now "stopped")
    }
    @Override
    protected void onDestroy() {
        Log.v(LOG_TAG, "in onDestroy");
        super.onDestroy();
// The activity is about to be destroyed.
    }

    @Override
    public void onItemSelected(String dateTime, String location) {
        if (mTwoPane) {

            Bundle args = new Bundle();
            args.putString(DetailActivity.DATE_TIME, dateTime);
            args.putString(DetailActivity.LOCATION,location);
            DetailActivity.PlaceholderFragment fragment = new DetailActivity.PlaceholderFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment)
                    .commit();

            Log.e("Kraht","FALSE");
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailActivity.DATE_TIME, dateTime)
                    .putExtra(DetailActivity.LOCATION,location);
            startActivity(intent);
            Log.e("Kraht","TRUE");
        }

    }
}
