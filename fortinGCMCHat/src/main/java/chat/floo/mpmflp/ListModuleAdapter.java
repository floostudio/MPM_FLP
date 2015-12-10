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
 * Created by SONY_VAIO on 05-Sep-15.
 */
public class ListModuleAdapter extends BaseAdapter{
    JSONArray data;
    Context context;

    public ListModuleAdapter(Context context,JSONArray data){
        this.context= context.getApplicationContext();
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
            return  null;
        }
    }

    @Override
    public long getItemId(int position) {
        try {
            return Integer.parseInt(data.getJSONObject(position).getString("TR_MOD_ID"));
        } catch (JSONException e) {
            e.printStackTrace();
            return  0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView;
        rowView = inflater.inflate(R.layout.child_item_sop, parent, false);
        TextView moduleTitle = (TextView)rowView.findViewById(R.id.categoryName);
        try {
            moduleTitle.setText(data.getJSONObject(position).getString("TR_MOD_NAME"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rowView;
    }
}
