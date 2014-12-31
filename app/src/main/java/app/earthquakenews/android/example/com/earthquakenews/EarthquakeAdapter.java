package app.earthquakenews.android.example.com.earthquakenews;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class EarthquakeAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView locationView;
        public final TextView dateTimeView;
        public final TextView magnitudeView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.imageMagnitude);
            locationView = (TextView) view.findViewById(R.id.list_item_esrthquake_location);
            dateTimeView = (TextView) view.findViewById(R.id.list_item_earthquake_dateTime);
            magnitudeView = (TextView) view.findViewById(R.id.list_item_earthquake_magnitude);

        }
    }
    public static int getIconResourceForMagnitude(double magnitude) {
        if(magnitude>0 && magnitude<=3.99){
            return R.drawable.green;
        }
        if(magnitude>=4 && magnitude<=6.99){
            return R.drawable.yellow;
        }
        if(magnitude>=7){
            return R.drawable.red;
        }
        return -1;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public EarthquakeAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        int layoutId = R.layout.list_item_earthquake;
        View view = LayoutInflater.from(context).inflate(layoutId, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.iconView.setImageResource(getIconResourceForMagnitude(
                cursor.getInt(EarthquakeFragment.COL_MAGNITUDE)));

        String location = cursor.getString(EarthquakeFragment.COL_LOCATION);

        viewHolder.locationView.setText(location);

        String dateTime = cursor.getString(EarthquakeFragment.COL_DATE_TIME);

        viewHolder.dateTimeView.setText(dateTime);

        String magnitude = cursor.getString(EarthquakeFragment.COL_MAGNITUDE);
        viewHolder.magnitudeView.setText(magnitude);
    }
}
