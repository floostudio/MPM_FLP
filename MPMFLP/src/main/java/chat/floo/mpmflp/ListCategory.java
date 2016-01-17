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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListCategory.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListCategory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListCategory extends Fragment implements AsyncResponse{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListCategory.
     */
    // TODO: Rename and change types and number of parameters
    public static ListCategory newInstance() {
        ListCategory fragment = new ListCategory();
        return fragment;
    }

    public ListCategory() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    Context context;
    ExpandableListView category;
    MyExpandableAdapter adapter;
    MyHTTPGet requester;
    JSONArray data;
    ProgressDialog progressDialog;
    private int lastExpandedPosition = -1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        requester = new MyHTTPGet(context);
        requester.delegate = this;
        requester.execute(DataManager.restAPIurl+"/catalogue/getAll");
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_list_category, container, false);
        category = (ExpandableListView)rootView.findViewById(R.id.category);

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
                    String productID = data.getJSONObject(groupPosition)
                            .getJSONArray("PRODUCT")
                            .getJSONObject(childPosition)
                            .getString("PROD_ID");
                    Intent intent = new Intent(context, ProductPage.class);
                    intent.putExtra("productID", productID);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
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
                adapter = new MyExpandableAdapter(context,data);
                category.setAdapter(adapter);
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
