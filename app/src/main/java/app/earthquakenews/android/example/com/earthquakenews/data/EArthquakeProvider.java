package app.earthquakenews.android.example.com.earthquakenews.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by islam on 29-Dec-14.
 */
public class EArthquakeProvider extends ContentProvider {
    private static final int EARTHQUAKE = 100;
    private static final int EARTHQUAKE_DATE_LOCATION = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private EarthquakeDbHelper OpenHelper;


    private static final SQLiteQueryBuilder EarthquakeQueryBuilder ;

    static{
        EarthquakeQueryBuilder = new SQLiteQueryBuilder();
        EarthquakeQueryBuilder.setTables(EarthquakeContract.EarthquakeEntry.TABLE_NAME);
    }


    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = EarthquakeContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, EarthquakeContract.PATH_EARTHQUAKE, EARTHQUAKE);
        matcher.addURI(authority, EarthquakeContract.PATH_EARTHQUAKE + "/*/*", EARTHQUAKE_DATE_LOCATION);
        return matcher;
    }
    private static final String sDateAndLocationSelection =
            EarthquakeContract.EarthquakeEntry.TABLE_NAME +
                    "." + EarthquakeContract.EarthquakeEntry.COLUMN_DATE_TIME + " = ? AND " +
                    EarthquakeContract.EarthquakeEntry.COLUMN_LOCATION + " = ? ";

    private Cursor getEarthquakeByDateAndLocation(
            Uri uri, String[] projection, String sortOrder) {
        String date = EarthquakeContract.EarthquakeEntry.getDateFromUri(uri);
        String location = EarthquakeContract.EarthquakeEntry.getLocationFromUri(uri);

        return EarthquakeQueryBuilder.query(OpenHelper.getReadableDatabase(),
                projection,
                sDateAndLocationSelection,
                new String[]{date, location},
                null,
                null,
                sortOrder
        );
    }

    @Override
    public boolean onCreate() {
        OpenHelper = new EarthquakeDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            case EARTHQUAKE :
            retCursor = OpenHelper.getReadableDatabase().query(
                    EarthquakeContract.EarthquakeEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
                break;
            case EARTHQUAKE_DATE_LOCATION:
                retCursor = getEarthquakeByDateAndLocation(uri ,projection,selection);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri +OpenHelper.getReadableDatabase());
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case EARTHQUAKE:
                return EarthquakeContract.EarthquakeEntry.CONTENT_TYPE;
            case EARTHQUAKE_DATE_LOCATION :
                return EarthquakeContract.EarthquakeEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = OpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri=null;
        if(match == EARTHQUAKE){
            long _id = db.insert(EarthquakeContract.EarthquakeEntry.TABLE_NAME, null, contentValues);
            if ( _id > 0 )
                returnUri = EarthquakeContract.EarthquakeEntry.buildEarthquakeUri();
            else
                throw new android.database.SQLException("Failed to insert row into " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = OpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted=0;
        if (match== EARTHQUAKE) {

                rowsDeleted = db.delete(
                        EarthquakeContract.EarthquakeEntry.TABLE_NAME, selection, selectionArgs);


        }

        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = OpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated=0;
        if (match==EARTHQUAKE) {

                rowsUpdated = db.update(EarthquakeContract.EarthquakeEntry.TABLE_NAME, contentValues, selection,
                        selectionArgs);

        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = OpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EARTHQUAKE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(EarthquakeContract.EarthquakeEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
