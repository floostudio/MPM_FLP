package chat.floo.mpmflp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArticlePage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArticlePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArticlePage extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARTICLE_DATA = "article";
    // TODO: Rename and change types of parameters
    private String data;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param data Parameter 1.
     * @return A new instance of fragment ArticlePage.
     */
    // TODO: Rename and change types and number of parameters
    public static ArticlePage newInstance(String data) {
        ArticlePage fragment = new ArticlePage();
        Bundle args = new Bundle();
        args.putString(ARTICLE_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    public ArticlePage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            data = getArguments().getString(ARTICLE_DATA);
        }
    }

    Context context;

    TextView uploadInfo,title,content;
    ImageView imageView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        context = getActivity();
        View rootView = inflater.inflate(R.layout.activity_article_reader, container, false);

        uploadInfo = (TextView) rootView.findViewById(R.id.uploadinfo);
        title = (TextView) rootView.findViewById(R.id.title);
        content = (TextView) rootView.findViewById(R.id.content);

        imageView = (ImageView)rootView.findViewById(R.id.articleImage);
        if(data!=null)
        {
            try {
                JSONObject jsonObject = new JSONObject(data);
                String author = jsonObject.getString("NAME");
                if(author.length()<=0||author==null||author.equalsIgnoreCase("null"))
                    author = jsonObject.getString("username");
                if(author.length()<=0||author==null||author.equalsIgnoreCase("null"))
                    author = "Unknown";
                uploadInfo.setText(jsonObject.getString("UPLOAD_DATE")+" | "+author);
                title.setText(jsonObject.getString("TITLE"));
                content.setText(jsonObject.getString("CONTENT"));
                //new MyImageDownloader(imageView,true,true).execute(DataManager.dirUrl+jsonObject.getString("IMG"));
                ImageLoader imgLoader = ImageLoader.getInstance();
                imgLoader.displayImage(DataManager.dirUrl + jsonObject.getString("IMG"), imageView);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
