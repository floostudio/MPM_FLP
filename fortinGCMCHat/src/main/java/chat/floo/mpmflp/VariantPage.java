package chat.floo.mpmflp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VariantPage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VariantPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VariantPage extends Fragment implements AsyncResponse{
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
     * @return A new instance of fragment VariantPage.
     */
    // TODO: Rename and change types and number of parameters
    public static VariantPage newInstance(String param1) {
        VariantPage fragment = new VariantPage();
        Bundle args = new Bundle();
        args.putString(PRODUCT_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public VariantPage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productID = getArguments().getString(PRODUCT_ID);
        }
    }
    ImageView product;
    LinearLayout imageWrapper;
    TextView variantName;
    Context context;
    JSONArray data;
    MyHTTPGet requester;
    int productImageIndex=0;
    //MyImageLoader imgLoader ;
    ImageLoader imgLoader;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        View rootView = inflater.inflate(R.layout.fragment_variant_page, container, false);
        imageWrapper = (LinearLayout) rootView.findViewById(R.id.imageWrapper);
        product = (ImageView)rootView.findViewById(R.id.variantImage);
        variantName = (TextView)rootView.findViewById(R.id.variantName);
        requester = new MyHTTPGet(context);
        requester.delegate = this;
        requester.execute(DataManager.restAPIurl+"/catalogue/getVariant_by?id="+productID);
        //imgLoader = new MyImageLoader(context);
        imgLoader = ImageLoader.getInstance();
        product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ImagePopup.class);
                try {
                    intent.putExtra(ImagePopup.KEY_IMAGE_PATH,data.getJSONObject(productImageIndex).getString("PRODVAR_IMG"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
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
    void addImageVariants(int paramID,String imagePath)
    {
        final ImageView variantImage = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        variantImage.setId(paramID);
        variantImage.setLayoutParams(params);
        variantImage.setAdjustViewBounds(true);
        variantImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    productImageIndex = v.getId();
                    JSONObject row = data.getJSONObject(productImageIndex);
                    //new MyImageDownloader(product,true,false).execute(DataManager.dirUrl+row.getString("PRODVAR_IMG"));

                    imgLoader.displayImage(DataManager.dirUrl + row.getString("PRODVAR_IMG"), product);
                    variantName.setText(row.getString("PRODVAR_NAME"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
        imgLoader.displayImage(DataManager.dirUrl+imagePath, variantImage);

        //new MyImageDownloader(variantImage,true,true).execute(DataManager.dirUrl+imagePath);
        imageWrapper.addView(variantImage);
    }

    @Override
    public void processFinish(String output) {
        if(output!="")
        {
            try {
                data = new JSONArray(output);
                //new MyImageDownloader(product,true,true).execute(DataManager.dirUrl + data.getJSONObject(productImageIndex).getString("PRODVAR_IMG"));
                imgLoader.displayImage(DataManager.dirUrl + data.getJSONObject(productImageIndex).getString("PRODVAR_IMG"),product);
                variantName.setText( data.getJSONObject(productImageIndex).getString("PRODVAR_NAME"));
                int dataSize = data.length();
                for(int i=0;i<dataSize;i++)
                {
                    addImageVariants(i,data.getJSONObject(i).getString("PRODVAR_IMG"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
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
