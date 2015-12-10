package chat.floo.mpmflp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import chat.floo.mpmflp.views.ProportionalImageView;

/**
 * Created by SONY_VAIO on 14-Aug-15.
 */
public class MyExpandableAdapter extends BaseExpandableListAdapter {

    Context context;
    JSONArray data;

    public MyExpandableAdapter(Context context,JSONArray data)
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
            return data.getJSONObject(groupPosition).getJSONArray("PRODUCT").length();
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        try {
            return data.getJSONObject(groupPosition).getJSONObject("CATEGORY");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        try {
            return data.getJSONObject(groupPosition).getJSONArray("PRODUCT").getJSONObject(childPosition);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getGroupId(int groupPosition) {
        try {
            return Long.parseLong(data.getJSONObject(groupPosition).getJSONObject("CATEGORY").getString("PRODCAT_ID"));
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        try {
            return Long.parseLong(data.getJSONObject(groupPosition)
                    .getJSONArray("PRODUCT")
                    .getJSONObject(childPosition)
                    .getString("PROD_ID"));
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
            convertView = inflater.inflate(R.layout.group_item,null);
        }

        try {
            JSONObject jsonObject = data.getJSONObject(groupPosition).getJSONObject("CATEGORY");
            ImageView iconCategory = (ImageView)convertView.findViewById(R.id.categoryIcon);
            TextView categoryName = (TextView)convertView.findViewById(R.id.categoryName);

            categoryName.setText(jsonObject.getString("TITLE"));

            ImageLoader imgLoader = ImageLoader.getInstance();
            imgLoader.displayImage(DataManager.dirUrl+jsonObject.getString("IMG"),iconCategory);

            //new MyImageDownloader(iconCategory,true,false).execute(DataManager.dirUrl+jsonObject.getString("IMG"));
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
            convertView = inflater.inflate(R.layout.child_item,null);
        }

        try {
            JSONObject productCat = data.getJSONObject(groupPosition).getJSONArray("PRODUCT").getJSONObject(childPosition);
            ProportionalImageView productImage = (ProportionalImageView) convertView.findViewById(R.id.productCategory);
            //new MyImageDownloader(productImage,true,true).execute(DataManager.dirUrl+productCat.getString("PROD_IMG"));
            //MyImageLoader imgLoader = new MyImageLoader(context);
            //imgLoader.displayImage(DataManager.dirUrl+productCat.getString("PROD_IMG"),productImage);
            TextView productName = (TextView)convertView.findViewById(R.id.productName);
            productName.setText(productCat.getString("PROD_NAME"));
            ImageLoader imgLoader = ImageLoader.getInstance();
            imgLoader.displayImage(DataManager.dirUrl+productCat.getString("PROD_IMG"),productImage);

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
