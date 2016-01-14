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
public class SendTrainingResult extends AsyncTask<String,Void,String> {

    List<String[]> answer;
    String trainingID;
    SessionManager session;
    int trueAnswer,falseAnswer,score,passingGrade;
    boolean pretest;
    String status;

    public SendTrainingResult(Context context, String trainingID,boolean isPreTest, List<String[]> answer, int trueAnswer, int falseAnswer, int score,int passingGrade,String status){
        this.answer = answer;
        this.trainingID = trainingID;
        session = new SessionManager(context);
        this.trueAnswer = trueAnswer;
        this.falseAnswer = falseAnswer;
        this.score = score;
        this.pretest = isPreTest;
        this.passingGrade = passingGrade;
        this.status = status.toUpperCase();
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
                    if(pretest)
                        jsonObject.put("TR_PRE_ID",row[0]);
                    else
                        jsonObject.put("TR_POST_ID",row[0]);
                    jsonObject.put("OPT",row[1]);
                    jsonObject.put("OPT_TEXT",row[2]);
                    jsonObject.put("OPT_NOTE",row[3]);


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
                dataToServer.put("training_id",trainingID);
                dataToServer.put("status",status);
                dataToServer.put("passinggrade",passingGrade);

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
