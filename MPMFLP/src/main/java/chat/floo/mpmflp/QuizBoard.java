package chat.floo.mpmflp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QuizBoard extends ActionBarActivity implements AsyncResponse{
//USER_NAME ,  SCORE
    String quizID;
    MyHTTPGet requester;
    JSONArray data;
    ListView scoreTable;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_highscore);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView titleBar = (TextView)toolbar.findViewById(R.id.toolbar_title);
        titleBar.setText("Quiz");
        scoreTable = (ListView)findViewById(R.id.scoreTable);
        if(getIntent().getExtras()!=null){
            quizID = getIntent().getStringExtra("quizID");
        }
        requester = new MyHTTPGet(this);
        requester.delegate = this;
        requester.execute(DataManager.restAPIurl+"quiz/getScore?quiz_id="+quizID+"&limit=10");
        progressDialog = ProgressDialog.show(this, "", "Loading Data..", true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,MainActivity.class);
        intent.setAction("listQuiz");
        finish();
        startActivity(intent);
    }

    @Override
    public void processFinish(String output) {
        if(output!=""){
            try {
                data = new JSONArray(output);

                List<String[]> dataScore = new ArrayList<>();

                for(int i = 0;i<data.length();i++){
                    dataScore.add(new String[]{data.getJSONObject(i).getString("USER_NAME"),data.getJSONObject(i).getString("SCORE")});
                }

                BoardAdapter adapter = new BoardAdapter(QuizBoard.this,dataScore);
                scoreTable.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();

        }

    }
}
