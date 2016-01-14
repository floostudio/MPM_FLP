package chat.floo.mpmflp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuizDetail.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QuizDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrainingDetail extends Fragment implements AsyncResponse{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DATA = "trainingData";
    private static final String TYPE = "detailType";


    // TODO: Rename and change types of parameters
    private JSONObject data;
    JSONArray listQuestion;
    private boolean isPretest;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment QuizDetail.
     */
    // TODO: Rename and change types and number of parameters
    public static TrainingDetail newInstance(String data,boolean isPreTest) {
        TrainingDetail fragment = new TrainingDetail();
        Bundle args = new Bundle();
        args.putString(DATA, data);
        args.putBoolean(TYPE,isPreTest);
        fragment.setArguments(args);
        return fragment;
    }

    public TrainingDetail() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String temp = getArguments().getString(DATA);
            try {
                data = new JSONObject(temp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            isPretest = getArguments().getBoolean(TYPE);
        }
    }

    Context context;
    TextView questionCount,questionTime,passingGrade;
    Button startButton;
    MyHTTPGet requester;
    SessionManager session;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        View rootView = inflater.inflate(R.layout.training_detail, container, false);
        questionCount = (TextView)rootView.findViewById(R.id.totalQuestion);
        questionTime = (TextView)rootView.findViewById(R.id.time);
        passingGrade = (TextView)rootView.findViewById(R.id.passingGrade);
        session = new SessionManager(context);

        startButton = (Button)rootView.findViewById(R.id.start);
        try {
            if(isPretest){
                questionCount.setText(data.getString("PREQUESTION"));
                passingGrade.setText(data.getString("PREGRADE"));
                questionTime.setText(data.getString("PRETIMES"));
            }
            else{
                questionCount.setText(data.getString("POSTQUESTION"));
                passingGrade.setText(data.getString("POSTGRADE"));
                questionTime.setText(data.getString("POSTTIMES"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startButton.setEnabled(false);
        startButton.setText("Please wait..");
        requester = new MyHTTPGet(context);
        requester.delegate = this;
        try {
            String tr_id = data.getString("TR_ID");
            if(isPretest)
                requester.execute(DataManager.restAPIurl + "training/getTrainingPre_by?tr_id=" + tr_id+"&user_id="+session.getuserid());
            else
                requester.execute(DataManager.restAPIurl + "training/getTrainingPost_by?tr_id=" + tr_id+"&user_id="+session.getuserid());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(context,AnswerTrainingActivity.class);
                try {
                    intent.putExtra("quizID", data.getString("TR_ID"));
                    if(isPretest){
                        intent.putExtra("quizType", AnswerTrainingActivity.FOR_PRE);
                        intent.putExtra("quizMinute",Integer.parseInt(data.getString("PRETIMES")));
                        intent.putExtra("passingGrade",Integer.parseInt(data.getString("PREGRADE")));
                        intent.putExtra("totalQuestion",Integer.parseInt(data.getString("PREQUESTION")));
                        intent.putExtra("questionData",listQuestion.toString());

                    }
                    else{
                        intent.putExtra("quizType", AnswerTrainingActivity.FOR_POST);
                        intent.putExtra("quizMinute",Integer.parseInt(data.getString("POSTTIMES")));
                        intent.putExtra("passingGrade",Integer.parseInt(data.getString("POSTGRADE")));
                        intent.putExtra("totalQuestion",Integer.parseInt(data.getString("POSTQUESTION")));
                        intent.putExtra("questionData",listQuestion.toString());
                    }
                    new AlertDialog.Builder(context)
                            .setTitle("Pesan")
                            .setMessage("Test hanya dapat dilakukan sekali. Lanjut?")
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes,
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface arg0,
                                                            int arg1) {
                                            startActivity(intent);
                                        }
                                    }).create().show();


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
        if(output!=""){
            try {
                listQuestion = new JSONArray(output);
                if(listQuestion.length()>0){
                    startButton.setText("START");
                    startButton.setEnabled(true);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                startButton.setText("Not Available");
            }
        }

    }


    /*
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            try {
                mListener = (OnFragmentInteractionListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }

        @Override
        public void onDetach() {
            super.onDetach();
            mListener = null;
        }

        /**
         * This interface must be implemented by activities that contain this
         * fragment to allow an interaction in this fragment to be communicated
         * to the activity and potentially other fragments contained in that
         * activity.
         * <p/>
         * See the Android Training lesson <a href=
         * "http://developer.android.com/training/basics/fragments/communicating.html"
         * >Communicating with Other Fragments</a> for more information.
         */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
