package chat.floo.mpmflp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AnswerQuestionnaireActivity extends ActionBarActivity implements AsyncResponse {

    JSONArray data;
    ListView optionList;
    Button submit;
    TextView question,questionOrder,timer;
    EditText note;
    List<String> optionString;
    ArrayList<String> optionTemp;
    int counter=0;
    int userChoice=0;
    int lastClicked=-1;
    int totalQuestion;
    ArrayAdapter adapter;
    MyHTTPGet requester;
    String questionnaireID;
    //public static int FOR_QUIZ =1;
    //public static int FOR_PRE =2;
    //public static int FOR_POST =3;
    public static int FOR_QUESTIONER =4;
    int typeAnswer;
    ProgressDialog progressDialog;
    List <String[]>questionaireAns;
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView titleBar = (TextView)toolbar.findViewById(R.id.toolbar_title);
        session = new SessionManager(this);
        if(getIntent().getExtras()!=null)
        {
            questionnaireID = getIntent().getStringExtra("quizID");
            typeAnswer = getIntent().getIntExtra("quizType", 1);
        }

        optionTemp = new ArrayList<>();
        optionTemp.add("A");
        optionTemp.add("B");
        optionTemp.add("C");
        optionTemp.add("D");
        optionTemp.add("E");

        requester = new MyHTTPGet(this);
        requester.delegate = this;
        if(typeAnswer==FOR_QUESTIONER) {
            titleBar.setText("Training");
            requester.execute(DataManager.restAPIurl + "training/getTrainingQuestionaire_by?tr_id=" + questionnaireID+"&user_id="+session.getuserid());
            questionaireAns= new ArrayList<>();
//            totalQuestion = data.length();
        }

        optionList = (ListView)findViewById(R.id.option);
        question = (TextView)findViewById(R.id.question);
        questionOrder = (TextView)findViewById(R.id.questionCounter);
        timer = (TextView)findViewById(R.id.time);
        submit = (Button)findViewById(R.id.submit);
        timer.setVisibility(View.GONE);
        note= (EditText) findViewById(R.id.note);
        note.setVisibility(View.INVISIBLE);

        optionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                userChoice = position;

                if (lastClicked != -1)
                    optionList.getChildAt(lastClicked).setBackgroundColor(Color.TRANSPARENT);
                view.setBackgroundColor(getResources().getColor(R.color.cpb_green));
                lastClicked = position;
                submit.setEnabled(true);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String answerString = optionString.get(userChoice);
                answerString = answerString.substring(3,optionString.get(userChoice).length());
                try {
                    questionaireAns.add(new String[]{
                            data.getJSONObject(counter).getString("TR_Q_ID"),
                            optionTemp.get(userChoice ),
                            answerString});
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //
                counter++;
                if (counter < totalQuestion && counter < data.length())
                    loadQuestion(counter);
                else {
                    goToResult();
                }

            }
        });
        progressDialog = ProgressDialog.show(this, "", "Loading data..", true);

    }

    @Override
    public void onBackPressed() {
        if (typeAnswer!=FOR_QUESTIONER) {
            new AlertDialog.Builder(this)
                    .setTitle("Terminate Quiz?")
                    .setMessage("Are you sure you want to end?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes,
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    goToResult();
                                }
                            }).create().show();
        }
    }

    void goToResult()
    {

        SendQuestionnaireResult sendResult = new SendQuestionnaireResult(this,questionnaireID,questionaireAns);
        sendResult.execute(DataManager.restAPIurl+"training/submitQuestionaire");
        new AlertDialog.Builder(this)
                .setTitle("Thank you")
                .setMessage("Thank you for participating")
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                                //finish();
                                Intent intent = new Intent(AnswerQuestionnaireActivity.this, MainActivity.class);
                                intent.setAction("listTraining");
                                finish();
                                startActivity(intent);
                            }
                        }).create().show();
    }

    @Override
    public void processFinish(String output) {
        if(output!="")
        {
            try {
                data =  new JSONArray(output);
                loadQuestion(counter);
                totalQuestion = data.length();

                List<NameValuePair>postParameter = new ArrayList<>();
                postParameter.add(new BasicNameValuePair("user_id",session.getuserid()));
                postParameter.add(new BasicNameValuePair("score", Integer.toString(0)));
                postParameter.add(new BasicNameValuePair("training_id", questionnaireID));
                postParameter.add(new BasicNameValuePair("device_id",session.getdeviceid()));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        progressDialog.dismiss();
    }

    void loadQuestion(int index)
    {
        userChoice = -1;
        submit.setEnabled(false);
        try {
            optionString = new ArrayList<>();
            optionString.add("A. " + data.getJSONObject(index).getString("OPT_A"));
            optionString.add("B. "+data.getJSONObject(index).getString("OPT_B"));
            optionString.add("C. "+data.getJSONObject(index).getString("OPT_C"));
            optionString.add("D. "+data.getJSONObject(index).getString("OPT_D"));
            optionString.add("E. "+data.getJSONObject(index).getString("OPT_E"));


            adapter = new ArrayAdapter(AnswerQuestionnaireActivity.this, android.R.layout.simple_list_item_1, optionString);
            optionList.setAdapter(adapter);

            question.setText(data.getJSONObject(index).getString("QUESTION"));
            questionOrder.setText("Question No. " + (counter + 1) + " of " + totalQuestion);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

}
