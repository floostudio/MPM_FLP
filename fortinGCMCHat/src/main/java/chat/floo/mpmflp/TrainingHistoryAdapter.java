package chat.floo.mpmflp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by SONY_VAIO on 13-Oct-15.
 */
public class TrainingHistoryAdapter extends BaseAdapter {

    Context context;
    JSONArray data;
    TrainingHistoryAdapter(Context context,JSONArray data){
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;

        rowView = inflater.inflate(R.layout.training_history_row, parent, false);

        TextView trainingName,trainingDate,trainingStatus;

        trainingName = (TextView)rowView.findViewById(R.id.trainingName);
        trainingDate = (TextView)rowView.findViewById(R.id.trainingDate);
        trainingStatus = (TextView)rowView.findViewById(R.id.trainingStatus);

        try {
            trainingName.setText(data.getJSONObject(position).getString("NAMA"));
            trainingDate.setText(data.getJSONObject(position).getString("TGL_TRAINING"));

            String status = data.getJSONObject(position).getString("STATUS");
            if(status.equalsIgnoreCase("lulus"))
                trainingStatus.setTextColor(Color.GREEN);
            else
                trainingStatus.setTextColor(context.getResources().getColor(R.color.headercolor));
            trainingStatus.setText(status);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return  rowView;
    }
}
