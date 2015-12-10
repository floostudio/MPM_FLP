package chat.floo.mpmflp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SONY_VAIO on 10-Aug-15.
 */
public class MyCustomAdapter extends BaseAdapter {

    JSONArray data;
    Context context;
    YouTubeThumbnailLoader thumbnailLoader;

    //public static  String[] dataVideoDummy = new String[]{"YGchlqCjj8A","g7lbj9cTXG8","Gimtj1MjiVE"};
    int type;
    public static int FOR_ARTICLE= 1;
    public static int FOR_VIDEO= 2;
    public static int FOR_FEATURE= 3;
    //MyImageLoader imgLoader;

    MyCustomAdapter(Context context,JSONArray data,int type){
        this.data = data;
        this.context = context.getApplicationContext();
        this.type = type;
        //imgLoader = new MyImageLoader(context);
    }

    @Override
    public int getCount() {
        return data.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return data.get(position);
        } catch (JSONException e) {
            Log.e("exception",e.getMessage());
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        int itemID=0;
        try {
            JSONObject object = (JSONObject) data.get(position);
            if(type==FOR_ARTICLE)
                return object.getInt("ARTICLE_ID");//if article
            else if(type==FOR_VIDEO)
                return object.getInt("VIDEO_ID"); //if video
            else if(type==FOR_FEATURE)
                return object.getInt("PRODFEAT_ID"); //if feature
        } catch (JSONException e) {
            Log.e("exception",e.getMessage());
        }
        return itemID;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView;
        if(type==FOR_ARTICLE)
            rowView = inflater.inflate(R.layout.list_row, parent, false);
        else if(type==FOR_FEATURE)
            rowView = inflater.inflate(R.layout.list_feature_row, parent, false);
        else
            rowView = inflater.inflate(R.layout.list_video_row, parent, false);
        TextView title = (TextView) rowView.findViewById(R.id.title);
        TextView uploadInfo = (TextView) rowView.findViewById(R.id.uploadinfo);
        ImageView imageView=null;
        YouTubeThumbnailView thumbnailView;
        if(type!=FOR_VIDEO)
            imageView = (ImageView) rowView.findViewById(R.id.list_image);
        else {

            thumbnailView = (YouTubeThumbnailView) rowView.findViewById(R.id.videoThumbnail);
            thumbnailView.initialize(VideoPlayerActivity.API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                    thumbnailLoader = youTubeThumbnailLoader;
                    try {
                        thumbnailLoader.setVideo(data.getJSONObject(position).getString("VIDURL"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

                }
            });
        }
        try {
            JSONObject object = (JSONObject) data.get(position);
            if(type==FOR_FEATURE) {
                title.setText(object.getString("PRODFEAT_TITLE"));
                uploadInfo.setText(object.getString("PRODFEAT_DESC"));
            }
            else {
                title.setText(object.getString("TITLE"));
                String author = object.getString("NAME");
                if(author.length()<=0||author==null||author.equalsIgnoreCase("null"))
                    author = object.getString("username");
                if(author.length()<=0||author==null||author.equalsIgnoreCase("null"))
                    author = "Unknown";

                uploadInfo.setText(object.getString("UPLOAD_DATE")+" | "+author);
            }
            if(type==FOR_ARTICLE) {
                //new MyImageDownloader(imageView, true, true).execute(DataManager.dirUrl + object.getString("IMG"));//if article
                //imgLoader.displayImage(DataManager.dirUrl + object.getString("IMG"),imageView);
                ImageLoader imgLoader = ImageLoader.getInstance();
                imgLoader.displayImage(DataManager.dirUrl+object.getString("IMG"),imageView);


            }
            else if(type==FOR_FEATURE) {
                //new MyImageDownloader(imageView, true, true).execute(DataManager.dirUrl + object.getString("PRODFEAT_IMG"));//if feature
                //imgLoader.displayImage(DataManager.dirUrl + object.getString("PRODFEAT_IMG"),imageView);
                ImageLoader imgLoader = ImageLoader.getInstance();
                imgLoader.displayImage(DataManager.dirUrl+object.getString("PRODFEAT_IMG"),imageView);

            }

        } catch (JSONException e) {
            Log.e("exception",e.getMessage());
        }

        return  rowView;


    }
}
