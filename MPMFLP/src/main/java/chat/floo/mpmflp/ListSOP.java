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
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListSOP.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListSOP#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListSOP extends Fragment implements AsyncResponse{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListSOP.
     */
    // TODO: Rename and change types and number of parameters
    public static ListSOP newInstance() {
        ListSOP fragment = new ListSOP();
        return fragment;
    }

    public ListSOP() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    ExpandableListView category;
    SOPExpandAdapter adapter;
    Context context;
    MyHTTPGet requester;
    private int lastExpandedPosition=-1;
    JSONArray data;
    ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list_category, container, false);
        category = (ExpandableListView)rootView.findViewById(R.id.category);
        requester = new MyHTTPGet(context);
        requester.delegate = this;
        requester.execute(DataManager.restAPIurl+"/sop/getList");

        category.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    category.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

        category.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                try {
                    String sopID = data.getJSONObject(groupPosition)
                            .getJSONArray("SOP")
                            .getJSONObject(childPosition)
                            .getString("SOP_ID");
                    String sopTitle = data.getJSONObject(groupPosition)
                            .getJSONArray("SOP")
                            .getJSONObject(childPosition)
                            .getString("SOP_NAME");
                    String sopDesc = data.getJSONObject(groupPosition)
                            .getJSONArray("SOP")
                            .getJSONObject(childPosition)
                            .getString("SOP_DESC");
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setAction("detailSOP");
                    intent.putExtra("sopID", sopID);
                    intent.putExtra("sopTitle", sopTitle);
                    intent.putExtra("sopDesc", sopDesc);

                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
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

    @Override
    public void processFinish(String output) {
        if(output!="")
        {
            try {
                data = new JSONArray(output);
                adapter = new SOPExpandAdapter(context,data);
                category.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
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
