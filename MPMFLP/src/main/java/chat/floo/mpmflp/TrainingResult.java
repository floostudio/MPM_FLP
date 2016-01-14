package chat.floo.mpmflp;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class TrainingResult extends ActionBarActivity {

    String trainingID;
    SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        session = new SessionManager(this);
        TextView titleBar = (TextView)toolbar.findViewById(R.id.toolbar_title);
        titleBar.setText("Training");

        TextView scoreText = (TextView)findViewById(R.id.score);
        TextView passingGrade = (TextView)findViewById(R.id.passingGrade);
        TextView resultMessage = (TextView)findViewById(R.id.resultMessage);
        final Button home = (Button)findViewById(R.id.home);
        Button requestAgain = (Button)findViewById(R.id.requestRev);
        int score=0,passinggrade=0;
        boolean isPretest=true;
        if(getIntent().getExtras()!=null){
            score = getIntent().getIntExtra("score",0);
            passinggrade = getIntent().getIntExtra("passingGrade",0);
            isPretest = getIntent().getBooleanExtra("isPretest", true);
            trainingID = getIntent().getStringExtra("trainingID");
        }
        scoreText.setText(Integer.toString(score));
        passingGrade.setText(Integer.toString(passinggrade));
        if(score<passinggrade){
            resultMessage.setText("Maaf, Anda Gagal");
            if(!isPretest)
                requestAgain.setVisibility(View.VISIBLE);
        }
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        requestAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyHTTPPost postman;

                List<NameValuePair> postParameter = new ArrayList<>();
                postParameter.add(new BasicNameValuePair("user_id",session.getuserid()));
                postParameter.add(new BasicNameValuePair("training_id", trainingID));

                postParameter.add(new BasicNameValuePair("device_id",session.getdeviceid()));

                postman = new MyHTTPPost(TrainingResult.this,DataManager.restAPIurl+"training/requestTraining",postParameter);
                postman.execute();
                onBackPressed();

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TrainingResult.this,MainActivity.class);
        intent.setAction("listTraining");
        finish();
        startActivity(intent);
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
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}
