package chat.floo.mpmflp;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import chat.floo.mpmflp.utils.ImageUtil;
import chat.floo.mpmflp.views.RoundedImageView;

public class UserProfile extends ActionBarActivity {

	String name, city, country, gender, logintype;
	RoundedImageView img_icon;

	ArrayList<UserPojo> userlist = new ArrayList<UserPojo>();
	String myusername, deviceid, profileid;
	SessionManager session;
	TextView txtname, txtgender, txtphone, txtstatus, txtjabatan,txtdealernama,txtdealerkota,txtdealerarea,txtmembersince;
	String status = "Available";

	// private static final String AD_UNIT_ID = DataManager.bannerid;
	// private AdView adView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		// adView = new AdView(this);
		// adView.setAdSize(AdSize.BANNER);
		// adView.setAdUnitId(AD_UNIT_ID);
		// AdRequest adRequest = new AdRequest.Builder().build();
		// adView.loadAd(adRequest);
		// LinearLayout ll = (LinearLayout) findViewById(R.id.ad);
		// ll.addView(adView);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(
				getResources().getDrawable(R.drawable.back));
		// invalidateOptionsMenu();

		session = new SessionManager(this);
		profileid = DataManager.profileid;
		deviceid = session.getdeviceid();
		myusername = session.getuserid();
		widgets();

		new getuserprofile().execute();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.individual, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {

			Intent i = new Intent(this, MainActivity.class);
			i.setAction("splash");
			finish();
			startActivity(i);
			overridePendingTransition(0, 0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void widgets() {
		txtstatus = (TextView) findViewById(R.id.txtstatus);
		txtname = (TextView) findViewById(R.id.txtname);
		txtgender = (TextView) findViewById(R.id.txtgender);
		txtphone = (TextView) findViewById(R.id.txtphone);
		txtdealerarea = (TextView) findViewById(R.id.txtdealerarea);
		txtdealerkota = (TextView) findViewById(R.id.txtdealerkota);
		txtdealernama = (TextView) findViewById(R.id.txtdealernama);
		txtmembersince = (TextView) findViewById(R.id.txtmembersince);
		txtjabatan = (TextView) findViewById(R.id.txtjabatan);


		img_icon = (RoundedImageView) findViewById(R.id.imageView1);
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(this, MainActivity.class);
		i.setAction("splash");
		finish();
		startActivity(i);
		overridePendingTransition(0, 0);
	}

	public void displaydata() {
		String selectedid = userlist.get(0).getUserid();


		//status = userlist.get(0).getStatus();
		logintype = "email";
		name = userlist.get(0).getFirstname();
		gender = userlist.get(0).getGender().toUpperCase();


		//country = userlist.get(0).getCountry().toUpperCase();

		if (logintype.equals("google")) {

			String googleurl = userlist.get(0).getProfilepic();

			ImageUtil.displayImage(img_icon, googleurl, null);
		} else if (logintype.equals("email")) {
			String url = userlist.get(0).getProfilepic();

			String photourl = url;
			ImageUtil.displayImage(img_icon, photourl, null);
		} else {
			String userid = userlist.get(0).getUserid();
			
			String oppurl = DataManager.GetDirectURL(userid);
			
			ImageUtil.displayImage(img_icon, oppurl, null);
			
		}
		
		

		txtname.setText(name);
		txtgender.setText(gender);
		if (gender.equals("")) {
			txtgender.setText("");
		}
		txtstatus.setText("" + userlist.get(0).getStatus());
		txtphone.setText(userlist.get(0).getPhone());
		txtmembersince.setText(userlist.get(0).getMembersince());
		txtdealernama.setText(userlist.get(0).getDealerName());
		txtdealerkota.setText(userlist.get(0).getDealerKota());
		txtjabatan.setText(userlist.get(0).getJabatan());
		txtdealerarea.setText(userlist.get(0).getDealerArea());
	}

	public class getuserprofile extends AsyncTask<String, Void, String> {
		boolean response = false;
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(UserProfile.this,"","Please wait...",true);

		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.getuserprofile(UserProfile.this, myusername,
					deviceid, DataManager.profileid );

			return "";

		}

		@Override
		protected void onPostExecute(String result) {

			if (DataManager.status.equalsIgnoreCase("1")) {
				userlist = DataManager.alluserlist;
				displaydata();
			} else if (DataManager.status.equalsIgnoreCase("false")) {
				session.logoutUser();
			}
			progressDialog.dismiss();

		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}
}
