package chat.floo.mpmflp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by SONY_VAIO on 05-Sep-15.
 */
public class SendQuizResult extends AsyncTask<String,Void,String> {

    List<String[]> answer;
    String quizID;
    SessionManager session;
    int trueAnswer,falseAnswer,score;
    public SendQuizResult(Context context, String quizID, List<String[]> answer,int trueAnswer,int falseAnswer,int score){
        this.answer = answer;
        this.quizID = quizID;
        session = new SessionManager(context);
        this.trueAnswer = trueAnswer;
        this.falseAnswer = falseAnswer;
        this.score = score;
    }

    @Override
    protected String doInBackground(String... params) {
        String result="";
        HttpParams myParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(myParams, 10000);
        HttpConnectionParams.setSoTimeout(myParams, 10000);
        HttpClient httpclient = new DefaultHttpClient(myParams );
        //String url = "http://development.ayowes.com/pedobackend/public/api/activitieslog";
        String url = params[0];

        try {

            JSONArray dataArray = new JSONArray();
            for(String[] row:answer){
                JSONObject jsonObject = new JSONObject();
                try {
                    //jsonObject.put("TR_ID",trainingID);
                    jsonObject.put("QUIZ_Q_ID",row[0]);
                    jsonObject.put("OPT",row[1]);
                    jsonObject.put("OPT_TEXT",row[2]);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dataArray.put(jsonObject);
            }
            JSONObject dataToServer = new JSONObject();
            try {
                dataToServer.put("user_id",session.getuserid());
                dataToServer.put("device_id",session.getdeviceid());
                dataToServer.put("trueanswer",trueAnswer);
                dataToServer.put("falseanswer",falseAnswer);
                dataToServer.put("score",score);
                dataToServer.put("quiz_id",quizID);
                dataToServer.put("data",dataArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String json=dataToServer.toString();
            Log.e("questionnaire",json);

            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("Content-type", "application/json");

            StringEntity se = new StringEntity(json);
            //se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(se);

            HttpResponse response = httpclient.execute(httppost);
            result = EntityUtils.toString(response.getEntity());
            Log.d("pushdata", result);


        } catch (ClientProtocolException e) {
            Log.e("pushdata",e.getMessage());

        }
        catch (ConnectTimeoutException e){
            Log.e("pushdata", "timeout");

        }
        catch (IOException e) {
            Log.e("pushdata", "ioexception");

        }
        return result;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.e("sendResult",s);
    }
}
