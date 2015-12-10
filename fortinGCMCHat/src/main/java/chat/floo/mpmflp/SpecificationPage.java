package chat.floo.mpmflp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SpecificationPage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SpecificationPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpecificationPage extends Fragment implements AsyncResponse {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String PRODUCT_ID = "productID";
    // TODO: Rename and change types of parameters
    private String productID;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SpecificationPage.
     */
    // TODO: Rename and change types and number of parameters
    public static SpecificationPage newInstance(String param1) {
        SpecificationPage fragment = new SpecificationPage();
        Bundle args = new Bundle();
        args.putString(PRODUCT_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public SpecificationPage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productID = getArguments().getString(PRODUCT_ID);
        }
    }
    ListView featureinfo;
    List<String[]> dataFeature;
    MyHTTPGet requester;
    SpecificationListAdapter adapter;
    Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_features, container, false);
        context = getActivity();
        featureinfo = (ListView)rootView.findViewById(R.id.listFeature);
        dataFeature = new ArrayList<>();
        requester = new MyHTTPGet(context);
        requester.execute(DataManager.restAPIurl+"catalogue/get_by?id="+productID);
        //adapter = new SpecificationListAdapter(context,dataFeature);
        requester.delegate = this;

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
                JSONArray data = new JSONArray(output);
                JSONObject jsonObject = data.getJSONObject(0);

                addDataFeature("Dimensi",jsonObject.getString("PROD_DIMENSI"));
                addDataFeature("Jarak antar Sumbu Roda", jsonObject.getString("PROD_JRK_SUMBU_RODA"));
                addDataFeature("Jarak ke Tanah", jsonObject.getString("PROD_JRK_KE_TANAH"));
                addDataFeature("Berat Kosong", jsonObject.getString("PROD_BRT_KSG"));
                addDataFeature("Tangki Bahan Bakar",jsonObject.getString("PROD_TANGKI_BB"));
                addDataFeature("Tipe Mesin",jsonObject.getString("PROD_TIPE_MESIN"));
                addDataFeature("Volume Langkah", jsonObject.getString("PROD_VOL_LANGKAH"));
                addDataFeature("Sistem Pendingin", jsonObject.getString("PROD_SIST_PENDINGIN"));
                addDataFeature("Sistem Suplai Bahan Bakar", jsonObject.getString("PROD_SIST_SUPL_BB"));
                addDataFeature("Diameter Langkah", jsonObject.getString("PROD_DIAMETER_LGKH"));
                addDataFeature("Tipe Transmisi", jsonObject.getString("PROD_TIPE_TRANSMISI"));
                addDataFeature("Kompresi", jsonObject.getString("PROD_KOMPRESI"));
                addDataFeature("Daya Maksimal", jsonObject.getString("PROD_DAYA_MAX"));
                addDataFeature("Torsi Maksimal", jsonObject.getString("PROD_TORSI_MAX"));
                addDataFeature("Operan Gigi", jsonObject.getString("PROD_OPER_GIGI"));
                addDataFeature("Tipe Starter", jsonObject.getString("PROD_TIPE_STARTER"));
                addDataFeature("Tipe Kopling", jsonObject.getString("PROD_TIPE_KOPLING"));
                addDataFeature("Minyak Pelumas", jsonObject.getString("PROD_KAP_MYK_PELUMAS"));
                addDataFeature("Tipe Rangka", jsonObject.getString("PROD_TIPE_RANGKA"));
                addDataFeature("Ukuran Ban Depan", jsonObject.getString("PROD_UK_BAN_DPN"));
                addDataFeature("Ukuran Ban Belakang", jsonObject.getString("PROD_UK_BAN_BLKG"));
                addDataFeature("Tipe Rem Depan", jsonObject.getString("PROD_TIPE_REM_DPN"));
                addDataFeature("Tipe Rema Belakang", jsonObject.getString("PROD_TIPE_REM_BLKG"));
                addDataFeature("Tipe Suspensi Depan", jsonObject.getString("PROD_TIPE_SUSP_DPN"));
                addDataFeature("Tipe Suspensi Belakang", jsonObject.getString("PROD_TIPE_SUSP_BLKG"));
                addDataFeature("Tipe Baterai", jsonObject.getString("PROD_TIPE_BATERAI"));
                addDataFeature("Sistem Pengapian",jsonObject.getString("PROD_SIST_PENGAPIAN"));
                addDataFeature("Tipe Aki", jsonObject.getString("PROD_TIPE_AKI"));
                addDataFeature("Tipe Busi", jsonObject.getString("PROD_TIPE_BUSI"));

                adapter = new SpecificationListAdapter(context,dataFeature);
                featureinfo.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    void addDataFeature(String columnName,String content){

        if(!content.equalsIgnoreCase("")){
            dataFeature.add(new String[]{columnName,content});
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
