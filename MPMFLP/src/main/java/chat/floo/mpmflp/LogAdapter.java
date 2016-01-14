package chat.floo.mpmflp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by SONY_VAIO on 13-Oct-15.
 */
public class LogAdapter extends BaseAdapter {

    JSONArray data;
    Context context;
    public LogAdapter(Context context,JSONArray data){
        this.data = data;
        this.context = context;
    }
    @Override
    public int getCount() {
        return data.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return data.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        try {
            return Integer.parseInt(data.getJSONObject(position).getString("LOG_ID"));
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;

        rowView = inflater.inflate(R.layout.log_row, parent, false);
        TextView message = (TextView)rowView.findViewById(R.id.message_text);
        TextView time = (TextView)rowView.findViewById(R.id.time);

        try {
            message.setText(data.getJSONObject(position).getString("LOG_MESSAGE"));
            String rawTime = data.getJSONObject(position).getString("LOG_DATE");
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            Date dateNow = cal.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date update = sdf.parse(rawTime);

            long different = dateNow.getTime()-update.getTime();
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;
            //Log.d("elapsedTime", "days: " + elapsedDays + " hours: " + elapsedHours + " minutes: " + elapsedMinutes + " seconds: " + elapsedSeconds);


            String timeMessage = "";
            if(elapsedDays==1)
                timeMessage = "yesterday";
            else if(elapsedDays>1&&elapsedDays<=6)
                timeMessage = elapsedDays+" days ago";
            else if(elapsedDays>6)
                timeMessage = "@"+rawTime;
            else if(elapsedDays<1){
                if(elapsedHours<=24&&elapsedHours>0)
                    timeMessage =  elapsedHours+" hours ago";
                else if(elapsedDays<=0){
                    if(elapsedMinutes<60&&elapsedMinutes>1)
                        timeMessage =  elapsedMinutes+" minutes ago";
                    else if(elapsedMinutes==1)
                        timeMessage = "a minute ago";
                    else if(elapsedMinutes<1){
                        timeMessage = "few moment ago";
                    }
                }
            }
            time.setText(timeMessage);


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return rowView;
    }
}
