package chat.floo.mpmflp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by SONY_VAIO on 23-Aug-15.
 */
public class SOPExpandAdapter extends BaseExpandableListAdapter {

    JSONArray data;
    Context context;
    public SOPExpandAdapter(Context context,JSONArray data)
    {
        this.context = context.getApplicationContext();
        this.data = data;
    }
    @Override
    public int getGroupCount() {
        return data.length();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        try {
            return data.getJSONObject(groupPosition).getJSONArray("SOP").length();
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        try {
            return data.getJSONObject(groupPosition);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        try {
            return data.getJSONObject(groupPosition).getJSONArray("SOP").getJSONObject(childPosition);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getGroupId(int groupPosition) {
        try {
            return Long.parseLong(data.getJSONObject(groupPosition).
                    getJSONObject("CATEGORY").getString("SOPCAT_ID"));
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        try {
            return Long.parseLong(data.getJSONObject(groupPosition)
                    .getJSONArray("SOP")
                    .getJSONObject(childPosition)
                    .getString("SOP_ID"));
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_item_sop,null);
        }
        TextView categoryName = (TextView)convertView.findViewById(R.id.categoryName);
        try {
            categoryName.setText(data.getJSONObject(groupPosition).
                    getJSONObject("CATEGORY").getString("SOPCAT_NAME"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ImageView arrowIndicator = (ImageView)convertView.findViewById(R.id.groupIndicator);
        if(isExpanded)
            arrowIndicator.setImageResource(R.drawable.arrow);
        else
            arrowIndicator.setImageResource(R.drawable.arrow_down);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if(convertView==null)
        {
            convertView = inflater.inflate(R.layout.child_item_sop,null);
        }
        TextView subCatName = (TextView) convertView.findViewById(R.id.categoryName);
        try {
            subCatName.setText(data.getJSONObject(groupPosition)
                    .getJSONArray("SOP")
                    .getJSONObject(childPosition)
                    .getString("SOP_NAME"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
