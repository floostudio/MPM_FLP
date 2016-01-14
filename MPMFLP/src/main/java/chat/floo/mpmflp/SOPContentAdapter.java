package chat.floo.mpmflp;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by SONY_VAIO on 23-Aug-15.
 */
public class SOPContentAdapter extends BaseAdapter {

    Context context;
    JSONArray data;

    public SOPContentAdapter(Context context,JSONArray data)
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
        rowView = inflater.inflate(R.layout.list_content_sop, parent, false);
        ImageView img = (ImageView) rowView.findViewById(R.id.sopImage);
        TextView title = (TextView) rowView.findViewById(R.id.title);
        TextView desc = (TextView)rowView.findViewById(R.id.description);

        try {
            title.setText(data.getJSONObject(position).getString("SOPDETAIL_TITLE"));
            String html = data.getJSONObject(position).getString("SOPDETAIL_DESC");
            String imgUrl = data.getJSONObject(position).getString("SOPDETAIL_IMG");
            if(imgUrl!=null&&!imgUrl.equalsIgnoreCase("")&&!imgUrl.equalsIgnoreCase("null")){
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(DataManager.dirUrl+imgUrl,img);
            }
            else{
                img.setVisibility(View.GONE);
            }

            html = html.replace("<span", "<font");
            html = html.replace("</span", "</font");

            html = html.replace("color:", "");
            html = html.replace(";\"", "\"");

            html = html.replace("style=\"", "color=\"");



            desc.setText(Html.fromHtml(html));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  rowView;

    }
}
