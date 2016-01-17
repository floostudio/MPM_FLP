package chat.floo.mpmflp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by SONY_VAIO on 29-Aug-15.
 */
public class MyHTTPPost extends AsyncTask<Void,Void,String> {
    public AsyncResponse delegate=null;

    List<NameValuePair> postParameter;
    String url;
    SessionManager session;
    Context context;
    public MyHTTPPost(Context context,String url,List<NameValuePair> posParameter){
        this.postParameter = posParameter;
        this.url = url;
        this.context = context;
        session = new SessionManager(this.context);
    }

    @Override
    protected String doInBackground(Void... params) {
        //String url = params[0];
        HttpParams myParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(myParams, 5000);
        HttpConnectionParams.setSoTimeout(myParams, 5000);
        HttpClient client = new DefaultHttpClient(myParams);
        HttpPost post = new HttpPost(url);
        Log.e("url",url);

        //List<NameValuePair> posParameter;
        /*posParameter = new ArrayList<NameValuePair>();
        posParameter.add(new BasicNameValuePair("gcm_id", regid));
        posParameter.add(new BasicNameValuePair("user_id", MPMuserID));
        posParameter.add(new BasicNameValuePair("password", password));
        //paramas.add(new BasicNameValuePair("logintype", "email"));

        //paramas.add(new BasicNameValuePair("city", city));
        //paramas.add(new BasicNameValuePair("country", country));
        posParameter.add(new BasicNameValuePair("device_id", deviceid));*/
        UrlEncodedFormEntity ent;
        try {
            ent = new UrlEncodedFormEntity(postParameter, HTTP.UTF_8);
            post.setEntity(ent);
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String returnString = "";
        HttpResponse response;
        try {
            response = client.execute(post);
            HttpEntity resEntity = response.getEntity();
            returnString = EntityUtils.toString(resEntity);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("result", returnString);
        return returnString;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try{
            JSONObject object =new JSONObject(s);
            String success = object.getString("success");
            String message = object.getString("message");
            if(success.equals("0")&&message.equals("User Not Logged in."))
            {
                new DoLogout(context).execute();
            }
        }
        catch (JSONException ex){
            ex.printStackTrace();
        }
        if(delegate!=null)
            delegate.processFinish(s);
    }
}
