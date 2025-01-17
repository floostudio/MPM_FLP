package chat.floo.mpmflp;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class AnswerTrainingActivity extends ActionBarActivity implements AsyncResponse {

    JSONArray data;
    ListView optionList;
    Button submit;
    TextView question,questionOrder,timer;
    EditText note;
    List<String> optionString;
    ArrayList<String> optionTemp;
    int counter=0;
    int rightAnswer=-1;
    int userChoice=0;
    int correctCount,falseCount;
    int totalQuestion;
    int questionToDisplay;
    ArrayAdapter adapter;
    MyHTTPGet requester;
    String trainingID;
    long minutesMillis;
    QuizTimer quizTimer;
    //public static int FOR_QUIZ =1;
    public static int FOR_PRE =2;
    public static int FOR_POST =3;
    //public static int FOR_QUESTIONER =4;
    int typeAnswer;
    int passingGrade;
    ProgressDialog progressDialog;
    List<Integer>listIndexQuestion;
    //List <String[]>questionaireAns;
    List <String[]>testAns;
    SessionManager session;
    View prevView;
    boolean loadFromServer = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView titleBar = (TextView)toolbar.findViewById(R.id.toolbar_title);
        session = new SessionManager(this);
        listIndexQuestion = new ArrayList<>();
        note= (EditText) findViewById(R.id.note);
        note.setVisibility(View.VISIBLE);
        if(getIntent().getExtras()!=null)
        {
            trainingID = getIntent().getStringExtra("quizID");
            typeAnswer = getIntent().getIntExtra("quizType", 1);
            if(typeAnswer==FOR_PRE||typeAnswer==FOR_POST)
            {
                minutesMillis = getIntent().getIntExtra("quizMinute",2)*1000;
                passingGrade = getIntent().getIntExtra("passingGrade",1);
                totalQuestion = getIntent().getIntExtra("totalQuestion",1);
            }
        }

        optionTemp = new ArrayList<>();
        optionTemp.add("A");
        optionTemp.add("B");
        optionTemp.add("C");
        optionTemp.add("D");
        optionTemp.add("E");

        requester = new MyHTTPGet(this);
        requester.delegate = this;
        prevView = null;


        titleBar.setText("Training");

        if(getIntent().getExtras()!=null){
            try {
                data = new JSONArray(getIntent().getStringExtra("questionData"));
            } catch (JSONException e) {
                e.printStackTrace();
                if(typeAnswer==FOR_PRE){

                    requester.execute(DataManager.restAPIurl+"training/getTrainingPre_by?tr_id="+trainingID+"&user_id="+session.getuserid());
                }
                else if(typeAnswer==FOR_POST) {
                    requester.execute(DataManager.restAPIurl + "training/getTrainingPost_by?tr_id=" + trainingID+"&user_id="+session.getuserid());
                }
                loadFromServer =true;
            }
        }

        testAns = new ArrayList<>();


        optionList = (ListView)findViewById(R.id.option);
        question = (TextView)findViewById(R.id.question);
        questionOrder = (TextView)findViewById(R.id.questionCounter);
        timer = (TextView)findViewById(R.id.time);
        submit = (Button)findViewById(R.id.submit);

        quizTimer = new QuizTimer(minutesMillis, 1000);
        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(minutesMillis),
                TimeUnit.MILLISECONDS.toMinutes(minutesMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(minutesMillis)),
                TimeUnit.MILLISECONDS.toSeconds(minutesMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(minutesMillis)));
        timer.setText(hms);
        optionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                userChoice = position;
                Log.e("answer", "r:" + rightAnswer + " u:" + userChoice);
                if(prevView!=null)
                    prevView.setBackgroundColor(Color.TRANSPARENT);
                prevView = view;
                view.setBackgroundColor(Color.GREEN);

                submit.setEnabled(true);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userChoice == rightAnswer)
                    correctCount++;
                else
                    falseCount++;
                String answerString="";
                if(userChoice>=0) {
                    answerString = optionString.get(userChoice);
                    answerString = answerString.substring(3, optionString.get(userChoice).length());
                }
                try {
                    String question = "";
                    if(typeAnswer==FOR_PRE)
                        question =data.getJSONObject(counter).getString("TR_PRE_ID");
                    else if(typeAnswer==FOR_POST)
                        question =data.getJSONObject(counter).getString("TR_POST_ID");

                    if(userChoice>=0) {
                        testAns.add(new String[]{
                                question,
                                optionTemp.get(userChoice),
                                answerString,
                                note.getText().toString()
                        });
                    }
                    else{
                        testAns.add(new String[]{
                                question,
                                "-",
                                "",
                                note.getText().toString()
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                counter++;
                if (counter < questionToDisplay)
                    loadQuestion(counter);
                else {
                    goToResult();
                }

            }
        });
        if(loadFromServer)
            progressDialog = ProgressDialog.show(this, "", "Loading data..", true);
        else{
            questionToDisplay = Math.min(totalQuestion,data.length());
            randomListQuestion();
            loadQuestion(counter);
            quizTimer.start();


            List<NameValuePair>postParameter = new ArrayList<>();
            postParameter.add(new BasicNameValuePair("user_id",session.getuserid()));
            postParameter.add(new BasicNameValuePair("score", Integer.toString(0)));
            postParameter.add(new BasicNameValuePair("training_id", trainingID));
            postParameter.add(new BasicNameValuePair("device_id",session.getdeviceid()));


            List<String[]> emptyList = new ArrayList<>();
            emptyList.add(new String[]{"","","",""});

            if(typeAnswer==FOR_PRE) {
                SendTrainingResult sendTrainingResult = new SendTrainingResult(this,trainingID,true,emptyList,correctCount,falseCount,0,passingGrade,"MENGAMBIL");
                sendTrainingResult.execute(DataManager.restAPIurl + "training/submitPreScore");
            }
            else if(typeAnswer==FOR_POST) {
                SendTrainingResult sendTrainingResult = new SendTrainingResult(this,trainingID,false,emptyList,correctCount,falseCount,0,passingGrade,"MENGAMBIL");
                sendTrainingResult.execute(DataManager.restAPIurl + "training/submitPostScore");
            }
        }

    }

    @Override
    public void onBackPressed() {
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

    void randomListQuestion(){
        Random random = new Random();
        int i=0;
        while(i<questionToDisplay){
            int numberQuestion=random.nextInt(questionToDisplay);
            if(listIndexQuestion.indexOf(numberQuestion)<0){
                listIndexQuestion.add(numberQuestion);
                i++;
            }
        }
    }


    void goToResult()
    {
        quizTimer.cancel();
        Intent intent = new Intent(AnswerTrainingActivity.this, TrainingResult.class);//chnage to trainingresult
        //int score = (int) Math.round((double) correctCount / (double) data.length()) * 10;
        int score = (int) Math.round(((double) correctCount / (double) questionToDisplay) * 100);
        intent.putExtra("score", score);
        intent.putExtra("passingGrade", passingGrade);
        intent.putExtra("trainingID", trainingID);

        if(typeAnswer==FOR_PRE)
            intent.putExtra("isPretest", true);
        else if(typeAnswer==FOR_POST)
            intent.putExtra("isPretest", false);

        String status = "GAGAL";
        if(score>=passingGrade)
            status = "LULUS";

        if(typeAnswer==FOR_PRE) {
            SendTrainingResult sendTrainingResult = new SendTrainingResult(this,trainingID,true,testAns,correctCount,falseCount,score,passingGrade,status);
            sendTrainingResult.execute(DataManager.restAPIurl + "training/submitPreScore");
        }
        else if(typeAnswer==FOR_POST) {
            SendTrainingResult sendTrainingResult = new SendTrainingResult(this,trainingID,false,testAns,correctCount,falseCount,score,passingGrade,status);
            sendTrainingResult.execute(DataManager.restAPIurl + "training/submitPostScore");
        }
        finish();
        startActivity(intent);
    }

    @Override
    public void processFinish(String output) {
        if(output!="")
        {
            try {
                data =  new JSONArray(output);
                questionToDisplay = Math.min(totalQuestion,data.length());
                randomListQuestion();
                loadQuestion(counter);
                quizTimer.start();


                List<NameValuePair>postParameter = new ArrayList<>();
                postParameter.add(new BasicNameValuePair("user_id",session.getuserid()));
                postParameter.add(new BasicNameValuePair("score", Integer.toString(0)));
                postParameter.add(new BasicNameValuePair("training_id", trainingID));
                postParameter.add(new BasicNameValuePair("device_id",session.getdeviceid()));


                List<String[]> emptyList = new ArrayList<>();
                emptyList.add(new String[]{"","","",""});

                if(typeAnswer==FOR_PRE) {
                    SendTrainingResult sendTrainingResult = new SendTrainingResult(this,trainingID,true,emptyList,correctCount,falseCount,0,passingGrade,"MENGAMBIL");
                    sendTrainingResult.execute(DataManager.restAPIurl + "training/submitPreScore");
                }
                else if(typeAnswer==FOR_POST) {
                    SendTrainingResult sendTrainingResult = new SendTrainingResult(this,trainingID,false,emptyList,correctCount,falseCount,0,passingGrade,"MENGAMBIL");
                    sendTrainingResult.execute(DataManager.restAPIurl + "training/submitPostScore");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                //disini
            }
        }
        progressDialog.dismiss();
    }

    void loadQuestion(int _index)
    {
        userChoice = -1;
        note.setText("");
        int index = listIndexQuestion.get(_index);
        submit.setEnabled(false);
        try {
            optionString = new ArrayList<>();
            optionString.add("A. " + data.getJSONObject(index).getString("OPT_A"));
            optionString.add("B. "+data.getJSONObject(index).getString("OPT_B"));
            optionString.add("C. "+data.getJSONObject(index).getString("OPT_C"));
            optionString.add("D. "+data.getJSONObject(index).getString("OPT_D"));
            optionString.add("E. "+data.getJSONObject(index).getString("OPT_E"));


            adapter = new ArrayAdapter(AnswerTrainingActivity.this, android.R.layout.simple_list_item_1, optionString);
            optionList.setAdapter(adapter);
            rightAnswer = optionTemp.indexOf(data.getJSONObject(index).getString("ANSWER"));

            question.setText(data.getJSONObject(index).getString("QUESTION"));
            questionOrder.setText("Question No. " + (counter + 1) + " of " + questionToDisplay);
            quizTimer.cancel();
            quizTimer.start();

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

    private class  QuizTimer extends CountDownTimer{

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public QuizTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);


        }

        @Override
        public void onTick(long millisUntilFinished) {
            String hms = String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
            timer.setText(hms);
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
        @Override
        public void onFinish() {
/*
            if (userChoice == rightAnswer)
                correctCount++;
            else
                falseCount++;
            counter++;
            if(counter<totalQuestion&&counter<data.length())
                loadQuestion(counter);
            else{
                goToResult();
            }
*/
            submit.callOnClick();
        }
    }

}
