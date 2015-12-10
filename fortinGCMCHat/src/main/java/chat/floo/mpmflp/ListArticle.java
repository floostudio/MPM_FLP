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
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListArticle.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListArticle#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListArticle extends Fragment implements AsyncResponse {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @paramparam1 Parameter 1.
     * @paramaram2 Parameter 2.
     * @return A new instance of fragment ListArticle.
     */
    // TODO: Rename and change types and number of parameters
    public static ListArticle newInstance() {
        ListArticle fragment = new ListArticle();
        return fragment;
    }

    public ListArticle() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    Context context;
    MyCustomAdapter adapter;
    ListView listView;
    JSONArray data;
    MyHTTPGet requester;
    ImageButton addArticle;
    SessionManager session;

    ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list_article, container, false);
        context = getActivity();
        requester = new MyHTTPGet(context);
        session = new SessionManager(context);
        requester.delegate = this;

        listView = (ListView) rootView.findViewById(R.id.list);
        //new GetData().execute();
        requester.execute(DataManager.restAPIurl+"/article/getValid");
        addArticle = (ImageButton)rootView.findViewById(R.id.addarticle);

        addArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostArticle.class);
                startActivity(intent);

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject jsonObject = data.getJSONObject(position);
                    Intent intent = new Intent(context,MainActivity.class);
                    intent.setAction("readArticle");
                    intent.putExtra("dataArticle",jsonObject.toString());
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                adapter = new MyCustomAdapter(context, data,MyCustomAdapter.FOR_ARTICLE);
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
