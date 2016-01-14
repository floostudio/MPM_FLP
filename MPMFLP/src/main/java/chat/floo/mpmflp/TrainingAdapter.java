package chat.floo.mpmflp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by SONY_VAIO on 23-Aug-15.
 */
public class TrainingAdapter extends BaseAdapter {

    Context context;
    JSONArray data;

    public TrainingAdapter(Context context, JSONArray data)
    {
        this.context = context.getApplicationContext();
        this.data = data;
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView;
        rowView = inflater.inflate(R.layout.list_quiz, parent, false);
        TextView title = (TextView) rowView.findViewById(R.id.title);
        TextView info = (TextView) rowView.findViewById(R.id.quizInfo);
        TextView uploaded = (TextView)rowView.findViewById(R.id.description);

        try {
            title.setText(data.getJSONObject(position).getString("TR_NAME"));
            //uploaded.setText(data.getJSONObject(position).getString("UPLOAD_DATE"));
            uploaded.setVisibility(View.GONE);
            info.setText("Start: "+data.getJSONObject(position).getString("START_DATE")+
                    "\nEnd: "+data.getJSONObject(position).getString("END_DATE"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  rowView;

    }
}
