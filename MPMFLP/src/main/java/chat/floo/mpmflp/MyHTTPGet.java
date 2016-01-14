package chat.floo.mpmflp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by SONY_VAIO on 15-Aug-15.
 */
public class MyHTTPGet extends AsyncTask<String, Void, String> {

    public AsyncResponse delegate=null;
    SessionManager session;
    Context context;

    public MyHTTPGet(Context context){
        this.context = context;
        session = new SessionManager(context);
    }


    @Override
    protected String doInBackground(String... params) {
        String result = "";
        HttpParams myParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(myParams, 5000);
        HttpConnectionParams.setSoTimeout(myParams, 5000);
        HttpClient httpclient = new DefaultHttpClient(myParams);
        String url = params[0];
        if(!url.contains("user_id")) {
            if(url.contains("?"))
                url = url + "&user_id=" + session.getuserid();
            else
                url = url + "?user_id=" + session.getuserid();
        }
        if(!url.contains("device_id"))
            url=url+"&device_id="+session.getdeviceid();
        Log.e("url",url);

        try {
            HttpGet httpGet = new HttpGet(url);

            HttpResponse response = httpclient.execute(httpGet);
            result = EntityUtils.toString(response.getEntity());
            Log.d("tag", result);


        } catch (ClientProtocolException e) {
            e.printStackTrace();
            result = "";
        } catch (IOException e) {
            e.printStackTrace();
            result = "";
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try{
            JSONObject object =new JSONObject(result);
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
        delegate.processFinish(result);


    }
}
