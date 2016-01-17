package chat.floo.mpmflp;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListTrainingHistory.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListTrainingHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListTrainingHistory extends Fragment implements AsyncResponse {


    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListTrainingHistory.
     */
    // TODO: Rename and change types and number of parameters
    public static ListTrainingHistory newInstance() {
        ListTrainingHistory fragment = new ListTrainingHistory();

        return fragment;
    }

    public ListTrainingHistory() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    Context context;
    ListView listView;
    MyHTTPPost postman;
    SessionManager session;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        context = getActivity();
        listView = (ListView)rootView.findViewById(R.id.list);
        session = new SessionManager(context);
        List<NameValuePair> postParameter = new ArrayList<>();
        postParameter.add(new BasicNameValuePair("user_id",session.getuserid()));
        postParameter.add(new BasicNameValuePair("device_id",session.getdeviceid()));
        postman = new MyHTTPPost(context,DataManager.restAPIurl+"user/getTrainingHist",postParameter);
        postman.delegate = this;
        postman.execute();

        progressDialog = ProgressDialog.show(context, "", "Loading data..", true);
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
                JSONObject result = new JSONObject(output);
                JSONArray data = result.getJSONObject("userdata").getJSONArray("Data");
                TrainingHistoryAdapter adapter = new TrainingHistoryAdapter(context,data);
                listView.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context,"No Data",Toast.LENGTH_LONG).show();
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
