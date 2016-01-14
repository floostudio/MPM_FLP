package chat.floo.mpmflp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListTraining.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListTraining#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListTraining extends Fragment implements AsyncResponse {

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     */
    // TODO: Rename and change types and number of parameters
    public static ListTraining newInstance() {
        ListTraining fragment = new ListTraining();
        return fragment;
    }

    public ListTraining() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }


    Context context;
    MyHTTPGet requester;
    JSONArray data;
    ListView quizList;
    SessionManager session;
    TrainingAdapter adapter;

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        session = new SessionManager(context);
        requester = new MyHTTPGet(context);
        requester.delegate = this;
        requester.execute(DataManager.restAPIurl + "/training/getAvailableTraining?user_id="+session.getuserid());

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        quizList = (ListView)rootView.findViewById(R.id.list);


        quizList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //goes to detail
                Intent intent = new Intent(context, MainActivity.class);
                intent.setAction("trainingContent");
                try {
                    intent.putExtra("trainingData", data.getJSONObject(position).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
        progressDialog = ProgressDialog.show(context, "", "Loading data..", true);

        return rootView;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
    }*/

    @Override
    public void processFinish(String output) {
        if(output!="")
        {
            try {
                data = new JSONArray(output);
                adapter = new TrainingAdapter(context,data);
                quizList.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context,"No data Available",Toast.LENGTH_LONG).show();
                try{
                    String read = new JSONObject(output).getString("success");
                    if(read.equals("0"))
                    {
                        new DoLogout(context).execute();
                    }
                }
                catch (JSONException ex){
                    ex.printStackTrace();

                }
            }
        }
        else{
            Toast.makeText(context,"No data Available",Toast.LENGTH_LONG).show();
        }
        progressDialog.dismiss();
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
