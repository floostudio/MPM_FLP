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

import org.json.JSONArray;
import org.json.JSONException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListArticle.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListArticle#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListVideo extends Fragment implements AsyncResponse{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    JSONArray data;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListArticle.
     */
    // TODO: Rename and change types and number of parameters
    public static ListVideo newInstance() {
        ListVideo fragment = new ListVideo();
        return fragment;
    }

    public ListVideo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    Context context;
    MyCustomAdapter adapter;
    ListView listView;
    MyHTTPGet requester;

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        context = getActivity();
        requester = new MyHTTPGet(context);
        requester.delegate = this;

        requester.execute(DataManager.restAPIurl+"video/getValid");

        listView = (ListView) rootView.findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(context, VideoPlayerActivity.class);
                try {
                    i.putExtra("videoKey",data.getJSONObject(position).getString("VIDURL"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(i);
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
                adapter = new MyCustomAdapter(context, data,MyCustomAdapter.FOR_VIDEO);
                listView.setAdapter(adapter);


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
