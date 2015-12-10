package chat.floo.mpmflp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FeaturesPage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FeaturesPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeaturesPage extends Fragment implements AsyncResponse {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PRODUCT_ID = "productID";

    // TODO: Rename and change types of parameters
    private String productID;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FeaturesPage.
     */
    // TODO: Rename and change types and number of parameters
    public static FeaturesPage newInstance(String param1) {
        FeaturesPage fragment = new FeaturesPage();
        Bundle args = new Bundle();
        args.putString(PRODUCT_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public FeaturesPage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productID = getArguments().getString(PRODUCT_ID);
        }
    }
    Context context;
    MyCustomAdapter adapter;
    ListView listView;
    JSONArray data;
    MyHTTPGet requester;
    SessionManager session;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        context = getActivity();
        requester = new MyHTTPGet(context);
        requester.delegate = this;
        session = new SessionManager(context);

        listView = (ListView) rootView.findViewById(R.id.list);
        //new GetData().execute();
        requester.execute(DataManager.restAPIurl + "/catalogue/getFeature_by?id="+productID);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject jsonObject = data.getJSONObject(position);
                    Intent intent = new Intent(context,ImagePopup.class);
                    intent.putExtra(ImagePopup.KEY_IMAGE_PATH,jsonObject.getString("PRODFEAT_IMG"));
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }
    @Override
    public void processFinish(String output) {
        if(output!="")
        {
            try {
                data = new JSONArray(output);
                adapter = new MyCustomAdapter(context, data,MyCustomAdapter.FOR_FEATURE);
                listView.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
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
