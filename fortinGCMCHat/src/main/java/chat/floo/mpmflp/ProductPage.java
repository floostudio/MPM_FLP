package chat.floo.mpmflp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductPage extends ActionBarActivity implements AsyncResponse {

    FragmentTabHost tabHost;
    ImageView imageView,playTvc;
    MyHTTPGet requester;
    private int prevTabID = 0;
    JSONArray data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_product_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView titleBar = (TextView)toolbar.findViewById(R.id.toolbar_title);
        titleBar.setText("Katalog");
        String productID="1";
        if(getIntent().getExtras()!=null)
            productID = getIntent().getStringExtra("productID");
        requester = new MyHTTPGet(this);
        requester.delegate = this;

        Bundle bundle = new Bundle();
        bundle.putString(SpecificationPage.PRODUCT_ID, productID);
        requester.execute(DataManager.restAPIurl + "catalogue/get_by?id=" + productID);
        tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        imageView = (ImageView) findViewById(R.id.productImage);
        playTvc = (ImageView)findViewById(R.id.playtvc);
        playTvc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductPage.this, VideoPlayerActivity.class);
                String videoKey="";
                try {
                    videoKey = data.getJSONObject(0).getString("VIDURL");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(videoKey.length()>0) {
                    i.putExtra("videoKey", videoKey);
                    startActivity(i);
                }
                else{
                    Toast.makeText(ProductPage.this,"No video",Toast.LENGTH_LONG).show();
                }

            }
        });
        tabHost.setup(this, this.getSupportFragmentManager(), R.id.realtabcontent);

        tabHost.addTab(tabHost.newTabSpec("Main Feature").setIndicator("Main Feature"),
                FeaturesPage.class, bundle);
        tabHost.addTab(tabHost.newTabSpec("Color").setIndicator("Color"),
                VariantPage.class, bundle);
        tabHost.addTab(tabHost.newTabSpec("Specification").setIndicator("Specification"),
                SpecificationPage.class, bundle);
        for(int i =0;i<3;i++) {
            TextView text2 = (TextView) tabHost.getTabWidget().getChildTabViewAt(i).findViewById(android.R.id.title);
            tabHost.getTabWidget().getChildTabViewAt(i).setBackgroundColor(Color.TRANSPARENT);
            text2.setTextSize(10);
        }
        tabHost.getTabWidget().getChildTabViewAt(tabHost.getCurrentTab())
                .setBackgroundColor(getResources().getColor(R.color.headercolor));
        TextView text = (TextView) tabHost.getTabWidget().getChildTabViewAt(tabHost.getCurrentTab()).
                findViewById(android.R.id.title);
        text.setTextColor(Color.WHITE);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                tabHost.getTabWidget().getChildTabViewAt(prevTabID)
                        .setBackgroundColor(Color.TRANSPARENT);
                TextView text2 = (TextView) tabHost.getTabWidget().getChildTabViewAt(prevTabID).
                        findViewById(android.R.id.title);
                text2.setTextColor(Color.BLACK);

                tabHost.getTabWidget().getChildTabViewAt(tabHost.getCurrentTab())
                        .setBackgroundColor(getResources().getColor(R.color.headercolor));
                TextView text = (TextView) tabHost.getTabWidget().getChildTabViewAt(tabHost.getCurrentTab()).
                        findViewById(android.R.id.title);
                text.setTextColor(Color.WHITE);
                prevTabID = tabHost.getCurrentTab();
            }
        });

    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_product_page, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void processFinish(String output) {
        if(output!="")
        {
            try {
                data = new JSONArray(output);
                JSONObject jsonObject = data.getJSONObject(0);
                //new MyImageDownloader(imageView,true,false).execute(DataManager.dirUrl+jsonObject.getString("PROD_IMG"));
                //MyImageLoader imgLoader = new MyImageLoader(this);
                //imgLoader.displayImage(DataManager.dirUrl+jsonObject.getString("PROD_IMG"),imageView);
                ImageLoader imgLoader = ImageLoader.getInstance();
                imgLoader.displayImage(DataManager.dirUrl+jsonObject.getString("PROD_IMG"),imageView);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setAction("listCatalogue");
        startActivity(intent);
        finish();
    }
}
