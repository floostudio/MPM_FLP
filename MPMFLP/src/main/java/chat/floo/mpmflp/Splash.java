package chat.floo.mpmflp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;


public class Splash extends Activity implements AsyncResponse{


	private boolean mIsBackButtonPressed;
	private static final int SPLASH_DURATION = 4000; // 2 seconds
	SessionManager session;
	MyHTTPGet requester;
	String version;
	int versionCode;
	ProgressDialog progressDialog;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		session = new SessionManager(this);
		requester = new MyHTTPGet(this);
		requester.delegate=this;
		DataManager.username = session.getuserid();
		PackageInfo pInfo = null;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		version = pInfo.versionName;
		//versionCode = pInfo.versionCode;
		//Log.e("version","versionName: "+version+" versionCode: "+versionCode);
		
						
		Handler handler = new Handler();

		// run a thread after 2 seconds to start the home screen
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				if (!mIsBackButtonPressed) {

	
					if(session.isLoggedIn())
					{
						requester.execute(DataManager.restAPIurl + "version/get?limit=1");
						progressDialog = ProgressDialog.show(Splash.this, "", "Checking version..", true);

					}else
					{
						Intent i = new Intent(Splash.this, LoginActivity.class);
						finish();
						startActivity(i);
						overridePendingTransition(0, 0);
					}
				}
			}

		}, SPLASH_DURATION);
		
		
		
	}


	@Override
	public void processFinish(String output) {
		progressDialog.dismiss();
		if(output!=""){
			try {
				JSONArray data = new JSONArray(output);
				String serverVersion = data.getJSONObject(0).getString("VER_NAME");
				if(serverVersion.equals(version)){
					Intent i = new Intent(this, MainActivity.class);
					i.setAction("splash");
					finish();
					startActivity(i);
					overridePendingTransition(0, 0);
				}
				else{
					new AlertDialog.Builder(this)
							.setTitle("Your app is outdated")
							//.setMessage("Your app need to be update, Please contact Administrator")
							.setMessage(data.getJSONObject(0).getString("VER_NOTIF"))
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {

										public void onClick(DialogInterface arg0,
															int arg1) {
											finish();
										}
									}).create().show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}
}
