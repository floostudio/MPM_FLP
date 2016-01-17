package chat.floo.mpmflp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReadModule.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReadModule#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReadModule extends Fragment implements AsyncResponse {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String trainingModID;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ReadModule.
     */
    // TODO: Rename and change types and number of parameters
    public static ReadModule newInstance(String trainingModID) {
        ReadModule fragment = new ReadModule();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, trainingModID);
        fragment.setArguments(args);
        return fragment;
    }

    public ReadModule() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            trainingModID = getArguments().getString(ARG_PARAM1);
        }
    }
    Context context;
    JSONArray data;
    ImageButton next,prev;
    TextView title,content;
    MyHTTPGet requester;
    int indexPage=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_read_module, container, false);
        title = (TextView)rootView.findViewById(R.id.title);
        content = (TextView)rootView.findViewById(R.id.content);
        next = (ImageButton)rootView.findViewById(R.id.next);
        prev = (ImageButton)rootView.findViewById(R.id.prev);

        requester = new MyHTTPGet(context);
        requester.delegate = this;
        requester.execute(DataManager.restAPIurl+"training/getModulesDetails_by?tr_mod_id="+trainingModID);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexPage--;
                if(indexPage<0&&data!=null)
                    indexPage = 0;
                loadModule();

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexPage++;
                if(indexPage==data.length()&&data!=null)
                    indexPage = data.length()-1;
                loadModule();

            }
        });

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
                data = new JSONArray(output);
                loadModule();
            } catch (JSONException e) {
                title.setText("*No Data");
                content.setText("*Empty");
                next.setVisibility(View.GONE);
                prev.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }

    void loadModule()
    {
        if(data.length()==0)
        {
            next.setVisibility(View.GONE);
            prev.setVisibility(View.GONE);
        }
        else{
            if(indexPage==0)
                prev.setVisibility(View.GONE);
            else
                prev.setVisibility(View.VISIBLE);
            if(indexPage==data.length()-1)
                next.setVisibility(View.GONE);
            else
                next.setVisibility(View.VISIBLE);

        }
        try {
            title.setText(data.getJSONObject(indexPage).getString("TR_MODDET_TITLE"));
            content.setText(data.getJSONObject(indexPage).getString("TR_MODDET_DESC"));
        } catch (JSONException e) {
            title.setText("*No Data");
            content.setText("*Empty");
            e.printStackTrace();
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
