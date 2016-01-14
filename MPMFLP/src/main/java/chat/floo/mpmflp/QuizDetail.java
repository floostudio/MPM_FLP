package chat.floo.mpmflp;

import android.content.Context;
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
public class QuizDetail extends Fragment implements AsyncResponse {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DATA = "quizData";

    // TODO: Rename and change types of parameters
    private JSONObject data;
    JSONArray listQuestion;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment QuizDetail.
     */
    // TODO: Rename and change types and number of parameters
    public static QuizDetail newInstance(String data) {
        QuizDetail fragment = new QuizDetail();
        Bundle args = new Bundle();
        args.putString(DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    public QuizDetail() {
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
        }
    }

    Context context;
    TextView questionCount,questionTime;
    Button highscore,startButton;
    MyHTTPGet requester;
    String quizID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        View rootView = inflater.inflate(R.layout.quiz_detail, container, false);
        questionCount = (TextView)rootView.findViewById(R.id.totalQuestion);
        questionTime = (TextView)rootView.findViewById(R.id.time);
        highscore = (Button)rootView.findViewById(R.id.highscore);
        startButton = (Button)rootView.findViewById(R.id.start);

        startButton.setEnabled(false);
        startButton.setText("Please wait..");
        requester = new MyHTTPGet(context);
        requester.delegate = this;
        try {
            quizID = data.getString("QUIZ_ID");
            requester.execute(DataManager.restAPIurl+"quiz/getQuizDetails_by?id="+quizID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        highscore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,QuizBoard.class);
                intent.putExtra("quizID",quizID);
                startActivity(intent);
            }
        });

        try {
            questionCount.setText(data.getString("TOTAL_QUESTION"));
            questionTime.setText(data.getString("TIMES"));
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //goes to answer page
                    Intent intent = new Intent(context,AnswerQuizActivity.class);
                    try {
                        intent.putExtra("quizID",data.getString("QUIZ_ID"));
                        intent.putExtra("quizMinute",Integer.parseInt(data.getString("TIMES")));
                        intent.putExtra("quizType", AnswerQuizActivity.FOR_QUIZ);
                        intent.putExtra("totalQuestion",Integer.parseInt(data.getString("TOTAL_QUESTION")));
                        intent.putExtra("questionData",listQuestion.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


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
