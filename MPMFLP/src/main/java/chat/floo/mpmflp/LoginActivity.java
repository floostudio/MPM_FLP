package chat.floo.mpmflp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class LoginActivity extends Activity implements OnClickListener {

	Dialog builder;
	EditText etemail, etpassword;
	Button btnlogin, btnReg, btnforgotpass;
	ProgressDialog pDialog;
	ConnectionDetector connection;
	String MPMuserID, password, city, userid, country, regid, deviceid, firstname,
			lastname, profilepic;
	String msg = "";
	double longitude = 0, latitude = 0;
	String PROJECT_NUMBER = DataManager.PROJECT_NUMBER;
	GPSTracker gps;
	String strLatitude, strLongitude;
	Geocoder geocoder;
	SessionManager session;
	GoogleCloudMessaging gcm;
	SharedPreferences prefs;
	private ProgressDialog progress;
	String resetemail = "";
	ImageView info1,info2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		session = new SessionManager(this);
		geocoder = new Geocoder(this, Locale.getDefault());
		gps = new GPSTracker(this);
		getRegId();

		getlocation();

		deviceid = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		connection = new ConnectionDetector(this);
		widgets();

	}

	public void getRegId() {

		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {

				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging
								.getInstance(getApplicationContext());
					}
					regid = gcm.register(PROJECT_NUMBER);
					msg = "Device registered, registration ID=" + regid;

				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();

					System.out.println("Error---" + ex.getMessage());
				}

				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				System.out.println("Registerid---" + regid);
			}
		}.execute(null, null, null);

	}

	public void widgets() {
		etemail = (EditText) findViewById(R.id.etemail);

		etpassword = (EditText) findViewById(R.id.etpassword);

		btnlogin = (Button) findViewById(R.id.btnlogin);
		btnReg = (Button) findViewById(R.id.btncancel);
		btnforgotpass = (Button) findViewById(R.id.btnforgotpass);

		info1=(ImageView)findViewById(R.id.info1);
		info2=(ImageView)findViewById(R.id.info2);

		info1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				etemail.setError("Please Fill with MPM ID");
				etemail.requestFocus();
			}
		});
		info2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				etpassword.setError("Password");
				etpassword.requestFocus();
			}
		});


		btnlogin.setOnClickListener(this);
		btnReg.setOnClickListener(this);
		btnforgotpass.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btnlogin) {
			register();
		} else if (v == btnReg) {
			Intent i = new Intent(this, RegisterActivity.class);
			finish();
			startActivity(i);
			overridePendingTransition(0, 0);
		} else if (v == btnforgotpass) {
			openmenu();
		}

	}

	private void openmenu() {

		builder = new Dialog(LoginActivity.this,
				android.R.style.Theme_DeviceDefault_DialogWhenLarge_NoActionBar);
		builder.setContentView(R.layout.restpass_layout);
		builder.setCanceledOnTouchOutside(true);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		builder.show();

		final EditText etemail = (EditText) builder.findViewById(R.id.etemail);

		TextView txtconfirm = (TextView) builder.findViewById(R.id.txtok);
		TextView txtcancel = (TextView) builder.findViewById(R.id.txtcancel);

		txtconfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				resetemail = etemail.getText().toString();

				if (resetemail.length() < 1) {
					Toast.makeText(LoginActivity.this, "Enter Email address!",
							Toast.LENGTH_LONG).show();
				} else if (!resetemail.contains("@")) {
					Toast.makeText(LoginActivity.this,
							"Enter valid Email Address", Toast.LENGTH_LONG)
							.show();
				} else {

					new resetpassword().execute();

				}
			}
		});

		txtcancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				builder.dismiss();

			}
		});

	}

	private class resetpassword extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(LoginActivity.this,
					"Requesting Password...", "Please wait....");
		}

		@Override
		protected String doInBackground(String... params) {

			APIManager.resetpassword(resetemail, userid);

			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {

			progress.cancel();
		
				
					Toast.makeText(
							getApplicationContext(),
							"We have sent Password on your registered Email Address.",
							Toast.LENGTH_LONG).show();
			
			
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	public void getlocation() {

		if (gps.canGetLocation()) {
			try {
				if (gps.canGetLocation()) {
					Log.d("Your Location", "latitude:" + gps.getLatitude()
							+ ", longitude: " + gps.getLongitude());
					latitude = gps.getLatitude();
					longitude = gps.getLongitude();
				}

				if (latitude == 0.0 && longitude == 0.0) {
					city = "";
					country = "";
				} else {
					List<Address> addresses;
					addresses = geocoder
							.getFromLocation(latitude, longitude, 1);

					System.out.println("address..." + addresses.toArray());

					city = addresses.get(0).getAddressLine(2);
					country = addresses.get(0).getCountryName();

				}

				System.out.println("city----" + city);
				System.out.println("country----" + country);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void register() {
		MPMuserID = etemail.getText().toString();

		password = etpassword.getText().toString();

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		if (MPMuserID.length() < 2) {
			etemail.setError("Please enter valid User ID");
			etemail.setFocusable(true);
		} else if (password.length() < 2) {
			etpassword.setError("Please enter valid password");
			etpassword.setFocusable(true);
		} else {
			if (connection.isConnectingToInternet()){
				new login().execute();
			}
			else{
				Toast.makeText(LoginActivity.this,
						"Please Connect to Internet...", Toast.LENGTH_LONG)
						.show();
			}

			/*if (connection.isConnectingToInternet()) {
				if (email.contains("@")) {
					if (email.contains(".")) {
						new login().execute();
					} else {
						etemail.setError("Please enter valid email");
						etemail.setFocusable(true);
					}
				} else {
					etemail.setError("Please enter valid email");
					etemail.setFocusable(true);
				}
			} else {
				Toast.makeText(LoginActivity.this,
						"Please Connect to Internet...", Toast.LENGTH_LONG)
						.show();
			}*/

		}

	}

	public class login extends AsyncTask<String, Void, String> {
		int response = 0;

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(LoginActivity.this, "Log In...",
					"Please wait....");

		}

		@Override
		protected String doInBackground(String... params) {

			response = registernotification();

			return "";

		}

		@Override
		protected void onPostExecute(String result) {

			progress.cancel();

			if (response==1) {
				DataManager.username = userid;
				session.setlogintype("email");
				session.createLoginSession(userid, firstname, lastname,
						deviceid);
				session.setProfilepic(profilepic);
				scheduleAlarm();
				Intent i = new Intent(LoginActivity.this,
						MainActivity.class);
				i.setAction("splash");
				finish();
				startActivity(i);
				overridePendingTransition(0, 0);

			}
			else if(response==2)
			{
				Intent i = new Intent(LoginActivity.this,
						ActivateUser.class);
				i.putExtra("MPMuserID",MPMuserID);
				finish();
				startActivity(i);
				overridePendingTransition(0, 0);
			}
			else
			{
				etpassword.setFocusable(true);
				etpassword.setError("UserName or Password invalid");
				etpassword.setFocusable(true);
			}

		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}

	public int registernotification() {

		//boolean isloggin = false;
		int authResult=0;

		//String url = DataManager.url + "login.php";
		String url = DataManager.restAPIurl+"/user/loginuser";
		HttpClient client = new DefaultHttpClient();
		HttpPost get = new HttpPost(url);

		List<NameValuePair> paramas = new ArrayList<NameValuePair>();
		paramas.add(new BasicNameValuePair("gcm_id", regid));
		paramas.add(new BasicNameValuePair("user_id", MPMuserID));
		paramas.add(new BasicNameValuePair("password", password));
		//paramas.add(new BasicNameValuePair("logintype", "email"));

		//paramas.add(new BasicNameValuePair("city", city));
		//paramas.add(new BasicNameValuePair("country", country));
		paramas.add(new BasicNameValuePair("device_id", deviceid));
		UrlEncodedFormEntity ent;
		try {
			ent = new UrlEncodedFormEntity(paramas, HTTP.UTF_8);
			get.setEntity(ent);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				System.out.println("returnString---" + returnString.toString());
				Log.e("message",returnString.toString());
				try {

					JSONObject obj = new JSONObject(returnString);

					String loginStatus = obj.getString("status");

					if (loginStatus.equals("1")) {
						//JSONArray array = obj.getJSONArray("users");
						JSONObject userDataObj = obj.getJSONObject("userdata");

						String status = "Available";
						userid = userDataObj.getString("Userid");
						firstname = userDataObj.getString("Username");
						status = userDataObj.getString("Status");
						profilepic = userDataObj.getString("Profile Pic");
						/*for (int i = 0; i < array.length(); i++)
						{

							userid = new String(
									array.getJSONObject(i).getString("userid")
											.getBytes("ISO-8859-1"), "UTF-8");

							firstname = new String(array.getJSONObject(i)
									.getString("firstname")
									.getBytes("ISO-8859-1"), "UTF-8");

							lastname = new String(array.getJSONObject(i)
									.getString("lastname")
									.getBytes("ISO-8859-1"), "UTF-8");

							status = new String(
									array.getJSONObject(i).getString("status")
											.getBytes("ISO-8859-1"), "UTF-8");
							
							profilepic = new String(array.getJSONObject(i)
									.getString("profilepic")
									.getBytes("ISO-8859-1"), "UTF-8");

						}*/
						authResult= 1;//login

						SharedPreferences.Editor se = prefs.edit();
						se.putString("status", status);
						se.commit();
					}
					else if(loginStatus.equals("2"))
					{
						authResult=2;
					}
					else {
						authResult = 0;
					}
				} catch (JSONException e) {
					Log.e("JSON Parser", "Error parsing data " + e.toString());
				}

			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			authResult = 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			authResult = 0;
		}

		return authResult;
	}

	public void connectionerror() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				LoginActivity.this);

		alertDialog.setTitle("Error!");

		alertDialog.setMessage("Connection Lost ! Try Again");

		alertDialog.setPositiveButton("Retry",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						new login().execute();

					}
				});

		alertDialog.show();
	}

	public void scheduleAlarm() {

		Long time = new GregorianCalendar().getTimeInMillis() + 1 * 60 * 1000;

		Intent intentAlarm = new Intent(this, AlarmReciever.class);

		// create the object
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intentAlarm, 0);

		try {
			alarmManager.cancel(pi);
		} catch (Exception e) {
			Log.e("Cancel",
					"AlarmManager update was not canceled. " + e.toString());
		}
		// set the alarm for particular time
		alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent
				.getBroadcast(this, 1, intentAlarm,
						PendingIntent.FLAG_UPDATE_CURRENT));

	}

}