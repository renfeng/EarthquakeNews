package app.earthquakenews.android.example.com.earthquakenews.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by islam on 28-Dec-14.
 */
public class EarthquakeContract  {

    public static final String CONTENT_AUTHORITY = "com.example.android.earthquake.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_EARTHQUAKE= "earthquake";

    public static final class EarthquakeEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EARTHQUAKE).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_EARTHQUAKE;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_EARTHQUAKE;

        public static final String TABLE_NAME = "earthquake";

        public static final String COLUMN_TITLE ="title";

        public static final String COLUMN_MAGNITUDE = "magnitude";

        public static final String COLUMN_LOCATION = "location";

        public static final String COLUMN_DEPTH = "depth";

        public static final String COLUMN_LATITUDE = "latitude";

        public static final String COLUMN_LONGITUDE = "longitude";

        public static final String COLUMN_DATE_TIME ="dateTime";

        public static Uri buildEarthquakeUri() {
            return CONTENT_URI;

        }
        public static Uri buildEarthquakeWithDateAndLocation(String date, String location) {
            return CONTENT_URI.buildUpon().appendPath(date).appendPath(location).build();
        }

        public static String getDateFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
        public static String getLocationFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

    }
}
