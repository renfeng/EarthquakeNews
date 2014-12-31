package app.earthquakenews.android.example.com.earthquakenews.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by islam on 28-Dec-14.
 */
public class EarthquakeDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "earthquake.db";

    public EarthquakeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_EARTHQUAKE_TABLE = "CREATE TABLE " + EarthquakeContract.EarthquakeEntry.TABLE_NAME + " (" +

                EarthquakeContract.EarthquakeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                EarthquakeContract.EarthquakeEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                EarthquakeContract.EarthquakeEntry.COLUMN_MAGNITUDE + " TEXT NOT NULL, " +
                EarthquakeContract.EarthquakeEntry.COLUMN_LOCATION + " TEXT NOT NULL, " +
                EarthquakeContract.EarthquakeEntry.COLUMN_DEPTH + " TEXT NOT NULL," +
                EarthquakeContract.EarthquakeEntry.COLUMN_LATITUDE + " TEXT NOT NULL," +
                EarthquakeContract.EarthquakeEntry.COLUMN_LONGITUDE + " TEXT NOT NULL," +
                EarthquakeContract.EarthquakeEntry.COLUMN_DATE_TIME + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_EARTHQUAKE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EarthquakeContract.EarthquakeEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
