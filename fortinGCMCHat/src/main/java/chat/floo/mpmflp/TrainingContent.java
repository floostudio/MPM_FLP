package chat.floo.mpmflp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TrainingContent.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TrainingContent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrainingContent extends Fragment implements AsyncResponse {
    private static final String DATA = "trainingData";

    // TODO: Rename and change types of parameters
    private JSONObject data;
    private String rawData;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment QuizDetail.
     */
    // TODO: Rename and change types and number of parameters
    public static TrainingContent newInstance(String data) {
        TrainingContent fragment = new TrainingContent();
        Bundle args = new Bundle();
        args.putString(DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    public TrainingContent() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rawData = getArguments().getString(DATA);
            try {
                data = new JSONObject(rawData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    Context context;
    RelativeLayout preTest,postTest,module,questioner;
    TextView title,moduleTitle;
    MyHTTPGet requester;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        View rootView = inflater.inflate(R.layout.training_content, container, false);
        preTest = (RelativeLayout)rootView.findViewById(R.id.preTest);
        postTest = (RelativeLayout)rootView.findViewById(R.id.postTest);
        module = (RelativeLayout)rootView.findViewById(R.id.moduleTraining);
        questioner = (RelativeLayout)rootView.findViewById(R.id.questioner);


        title = (TextView)rootView.findViewById(R.id.title);
        moduleTitle = (TextView)rootView.findViewById(R.id.moduleTitle);

        try {
            title.setText(data.getString("TR_NAME"));
            moduleTitle.setText("Modules");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        preTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goes to
                Intent intent = new Intent(context,MainActivity.class);
                intent.setAction("trainingTestDetail");
                intent.putExtra("trainingData",rawData);
                intent.putExtra("type",true);//it's pretest
                startActivity(intent);
            }
        });
        postTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goes to
                Intent intent = new Intent(context,MainActivity.class);
                intent.setAction("trainingTestDetail");
                intent.putExtra("trainingData",rawData);
                intent.putExtra("type",false);//it's posttest
                startActivity(intent);
            }
        });
        module.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,MainActivity.class);
                intent.setAction("listModule");
                try {
                    intent.putExtra("trainingID",data.getString("TR_ID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);

            }
        });
        questioner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    requester = new MyHTTPGet(context);
                    requester.delegate = TrainingContent.this;
                    requester.execute(DataManager.restAPIurl + "training/getTrainingQuestionaire_by?tr_id=" + data.getString("TR_ID"));
                    progressDialog = ProgressDialog.show(context, "", "Please wait..", true);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });




        //wait data


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void processFinish(String output) {
        progressDialog.dismiss();
        if(output!=""){
            try {
                JSONArray dataArray = new JSONArray(output);
                new AlertDialog.Builder(context)
                        .setTitle("Answer Questionnaire?")
                        .setMessage("The Questionnaire can be filled once, Continue?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        Intent intent = new Intent(context,AnswerQuestionnaireActivity.class);

                                        try {
                                            intent.putExtra("quizID",data.getString("TR_ID"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        intent.putExtra("quizType", AnswerQuestionnaireActivity.FOR_QUESTIONER);
                                        startActivity(intent);
                                    }
                                }).create().show();


            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "No Questionnaire Available", Toast.LENGTH_LONG).show();
            }
        }

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
