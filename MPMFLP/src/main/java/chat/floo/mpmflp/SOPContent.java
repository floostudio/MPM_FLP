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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SOPContent.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SOPContent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SOPContent extends Fragment implements AsyncResponse {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String title;
    private String sopDetailID;
    private String desc;


    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SOPContent.
     */
    // TODO: Rename and change types and number of parameters
    public static SOPContent newInstance(String sopID, String sopTitle, String sopDesc) {
        SOPContent fragment = new SOPContent();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM2, sopID);
        args.putString(ARG_PARAM1, sopTitle);
        args.putString(ARG_PARAM3, sopDesc);

        fragment.setArguments(args);
        return fragment;
    }

    public SOPContent() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_PARAM1);
            sopDetailID = getArguments().getString(ARG_PARAM2);
            desc = getArguments().getString(ARG_PARAM3);

        }
    }

    Context context;
    MyHTTPGet requester;
    JSONArray data;
    SOPContentAdapter adapter;
    ListView listContent;
    ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sopcontent, container, false);
        TextView titleView = (TextView)rootView.findViewById(R.id.contentTitle);
        TextView descView = (TextView)rootView.findViewById(R.id.contentDesc);

        listContent = (ListView)rootView.findViewById(R.id.sopcontent);
        titleView.setText(title);
        descView.setText(desc);

        requester = new MyHTTPGet(context);
        requester.delegate = this;
        requester.execute(DataManager.restAPIurl+"/sop/getDetail_by?id="+sopDetailID);
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
        if(output!="")
        {
            try {
                data  = new JSONArray(output);
                adapter = new SOPContentAdapter(context,data);
                listContent.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        progressDialog.dismiss();

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
