package chat.floo.mpmflp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by SONY_VAIO on 16-Aug-15.
 */
public class SpecificationListAdapter extends BaseAdapter {

    Context context;
    List<String[]> dataFeature;

    public SpecificationListAdapter(Context context, List<String[]> dataFeature)
    {
        this.context = context;
        this.dataFeature = dataFeature;
    }
    @Override
    public int getCount() {
        return dataFeature.size();
    }

    @Override
    public Object getItem(int position) {
        return dataFeature.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.list_features, parent, false);
        TextView column = (TextView) rootView.findViewById(R.id.featureColumn);
        TextView info = (TextView) rootView.findViewById(R.id.featureInfo);
        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.linear1);
        String[] row = dataFeature.get(position);
        column.setText(row[0]);//column name
        info.setText(row[1]);//column info
        if(position%2==0)
        {
            layout.setBackgroundColor(Color.parseColor("#888888"));
            //column.setBackgroundColor(Color.parseColor("#B0B0B0"));
            //info.setBackgroundColor(Color.parseColor("#B0B0B0"));
        }
        else{
            layout.setBackgroundColor(Color.parseColor("#989898"));

            //column.setBackgroundColor(Color.parseColor("#D0D0D0"));
            //info.setBackgroundColor(Color.parseColor("#D0D0D0"));

        }


        return rootView;
    }
}
