package app.earthquakenews.android.example.com.earthquakenews;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;
import android.util.Log;

import app.earthquakenews.android.example.com.earthquakenews.data.EarthquakeContract;
import app.earthquakenews.android.example.com.earthquakenews.data.EarthquakeDbHelper;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class TestDb extends ApplicationTestCase<Application> {

    public static final String LOG_TAG = TestDb.class.getSimpleName();
    public TestDb() {
        super(Application.class);
    }

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(EarthquakeDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new EarthquakeDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {
        String testTitle ="Minor earthquake - Potosi, Bolivia on December 28, 2014" ;
        String testMagnitude = "3.6";
        String testLocation ="POTOSI, BOLIVIA" ;
        String testDepth = "176";
        String testLatitude ="-22.68" ;
        String testLongitude ="-67.81" ;
        String testDateTime = "2014-12-28T13:33:10+00:00";

        EarthquakeDbHelper dbHelper = new EarthquakeDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EarthquakeContract.EarthquakeEntry.COLUMN_TITLE, testTitle);
        values.put(EarthquakeContract.EarthquakeEntry.COLUMN_MAGNITUDE, testMagnitude);
        values.put(EarthquakeContract.EarthquakeEntry.COLUMN_LOCATION, testLocation);
        values.put(EarthquakeContract.EarthquakeEntry.COLUMN_DEPTH, testDepth);
        values.put(EarthquakeContract.EarthquakeEntry.COLUMN_LATITUDE,testLatitude);
        values.put(EarthquakeContract.EarthquakeEntry.COLUMN_LONGITUDE,testLongitude);
        values.put(EarthquakeContract.EarthquakeEntry.COLUMN_DATE_TIME,testDateTime);

        long EarthquakeRowId;
        EarthquakeRowId = db.insert(EarthquakeContract.EarthquakeEntry.TABLE_NAME, null, values);
// Verify we got a row back.
        assertTrue(EarthquakeRowId != -1);
        Log.d(LOG_TAG, "New row id: " + EarthquakeRowId);

        String [] columns ={
                EarthquakeContract.EarthquakeEntry.COLUMN_TITLE,
                EarthquakeContract.EarthquakeEntry.COLUMN_MAGNITUDE,
                EarthquakeContract.EarthquakeEntry.COLUMN_LOCATION,
                EarthquakeContract.EarthquakeEntry.COLUMN_DEPTH,
                EarthquakeContract.EarthquakeEntry.COLUMN_LATITUDE,
                EarthquakeContract.EarthquakeEntry.COLUMN_LONGITUDE,
                EarthquakeContract.EarthquakeEntry.COLUMN_DATE_TIME
        };

        Cursor cursor = db.query(
                EarthquakeContract.EarthquakeEntry.TABLE_NAME, // Table to Query
                columns,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        if (cursor.moveToFirst()) {

            int TitleIndex = cursor.getColumnIndex(EarthquakeContract.EarthquakeEntry.COLUMN_TITLE);
            String title = cursor.getString(TitleIndex);
            int MagIndex = cursor.getColumnIndex((EarthquakeContract.EarthquakeEntry.COLUMN_MAGNITUDE));
            String magnitude = cursor.getString(MagIndex);
            int LocIndex = cursor.getColumnIndex((EarthquakeContract.EarthquakeEntry.COLUMN_LOCATION));
            String location = cursor.getString(LocIndex);
            int DepIndex = cursor.getColumnIndex((EarthquakeContract.EarthquakeEntry.COLUMN_DEPTH));
            String depth = cursor.getString(DepIndex);
            int LatIndex = cursor.getColumnIndex((EarthquakeContract.EarthquakeEntry.COLUMN_LATITUDE));
            String latitude = cursor.getString(LatIndex);
            int LongIndex = cursor.getColumnIndex((EarthquakeContract.EarthquakeEntry.COLUMN_LONGITUDE));
            String longitude = cursor.getString(LongIndex);
            int DtIndex = cursor.getColumnIndex((EarthquakeContract.EarthquakeEntry.COLUMN_DATE_TIME));
            String dateTime = cursor.getString(DtIndex);

            assertEquals(testTitle, title);
            assertEquals(testMagnitude, magnitude);
            assertEquals(testLocation, location);
            assertEquals(testDepth, depth);
            assertEquals(testLatitude,latitude);
            assertEquals(testLongitude,longitude);
            assertEquals(testDateTime,dateTime);

        } else {

            fail("No values returned :(");
        }

        dbHelper.close();
    }
}