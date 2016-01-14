package chat.floo.mpmflp;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import chat.floo.mpmflp.utils.DrawerAdapter;
import chat.floo.mpmflp.utils.DrawerItem;
import chat.floo.mpmflp.utils.ImageUtil;
import chat.floo.mpmflp.views.RoundedImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends ActionBarActivity {

	private ListView mDrawerList;
	private List<DrawerItem> mDrawerItems;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private Handler mHandler;
	// Typeface normal, bold;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	SessionManager sesion;
	String name, profileurl, deviceid, logintype;
	int fragmentposition = 0;
	static String data,sopID,sopName,sopDesc,trainingID,trainingModID;
	boolean isPreTest;
	TextView toolbarTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// normal = Typeface.createFromAsset(getAssets(), "normal.ttf");
		// bold = Typeface.createFromAsset(getAssets(), "bold.ttf");
		sesion = new SessionManager(this);

		sesion.setAppState(true);

		if (getIntent().getAction().equals("splash")) {
			fragmentposition = 1;
		}
		if (getIntent().getAction().equals("message")) {
			fragmentposition = 9;
		}
		if (getIntent().getAction().equals("search")) {
			fragmentposition = 10;
		}
		if (getIntent().getAction().equals("newgroup")) {
			fragmentposition = 12;
		}
		if (getIntent().getAction().equals("settings")) {
			fragmentposition = 13;
		}



		if(getIntent().getAction().equals("listSOP"))
		{
			fragmentposition = 2;//SOP
		}
		if(getIntent().getAction().equals("listArticle"))
		{
			fragmentposition = 4;//Article
		}
		if(getIntent().getAction().equals("listCatalogue"))
		{
			fragmentposition = 5;//Catalogue
		}
		if(getIntent().getAction().equals("listQuiz"))
		{
			fragmentposition = 6;//Quiz
		}
		if(getIntent().getAction().equals("listTraining"))
		{
			fragmentposition = 7;//training
		}
		if(getIntent().getAction().equals("readArticle"))
		{
			fragmentposition = 70;//readarticle
			data = getIntent().getStringExtra("dataArticle");
		}
		if(getIntent().getAction().equals("detailSOP"))
		{
			fragmentposition = 71;//detailSOP
			sopID = getIntent().getStringExtra("sopID");
			sopName = getIntent().getStringExtra("sopTitle");
			sopDesc = getIntent().getStringExtra("sopDesc");
		}
		if(getIntent().getAction().equals("quizDetail"))
		{
			fragmentposition = 72;//detail
			data = getIntent().getStringExtra("quizDetail");
		}
		if(getIntent().getAction().equals("trainingContent"))
		{
			fragmentposition = 73;
			data = getIntent().getStringExtra("trainingData");
		}
		if(getIntent().getAction().equals("trainingTestDetail"))
		{
			fragmentposition = 74;
			data = getIntent().getStringExtra("trainingData");
			isPreTest = getIntent().getBooleanExtra("type", true);
		}
		if(getIntent().getAction().equals("listModule"))
		{
			fragmentposition = 75;
			trainingID = getIntent().getStringExtra("trainingID");
		}
		if(getIntent().getAction().equals("readModule"))
		{
			fragmentposition = 76;
			trainingModID = getIntent().getStringExtra("trainingModID");
		}
		name = sesion.firstname() + " " + sesion.lastname();

		deviceid = sesion.getdeviceid();
		logintype = sesion.getLogintype();

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mTitle = mDrawerTitle =toolbarTitle.getText().toString();

		mDrawerList = (ListView) findViewById(R.id.list_view);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		prepareNavigationDrawerItems();
		setAdapter();
		// mDrawerList.setAdapter(new DrawerAdapter(this, mDrawerItems));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
				R.string.drawer_open, R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		mHandler = new Handler();

		selectItem();
	}

	private void setAdapter() {

		String url = sesion.getuserid();

		View headerView = null;

		headerView = prepareHeaderView(R.layout.header_navigation_drawer_1,
				url, name);

		BaseAdapter adapter = new DrawerAdapter(this, mDrawerItems);

		mDrawerList.addHeaderView(headerView);// Add header before adapter (for
												// pre-KitKat)
		mDrawerList.setAdapter(adapter);
	}

	private View prepareHeaderView(int layoutRes, String url, String name) {
		View headerView = getLayoutInflater().inflate(layoutRes, mDrawerList,
				false);
		RoundedImageView iv = (RoundedImageView) headerView
				.findViewById(R.id.image);
		TextView tv = (TextView) headerView.findViewById(R.id.txtname);
		ImageLoader.getInstance().init(
				ImageLoaderConfiguration.createDefault(this));
		String logintype ="email";// sesion.getLogintype();
		profileurl = sesion.getProfilePic();

		if (profileurl != null) {
			if (logintype.equals("google")) {

				String googleurl = profileurl;

				ImageUtil.displayImage(iv, googleurl, null);

			} else if (logintype.equals("facebook")) {
				String userid = profileurl;

				String oppurl = DataManager.GetDirectURL(userid);

				ImageUtil.displayImage(iv, oppurl, null);

			} else if (logintype.equals("email")) {

				//String photourl = DataManager.url + profileurl;
				String photourl = profileurl;

				ImageUtil.displayImage(iv, photourl, null);

			}
		}

		tv.setText(name);

		headerView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this, UserProfile.class);
				DataManager.profileid = sesion.getuserid();
				startActivity(i);
				overridePendingTransition(0, 0);
			}
		});

		return headerView;
	}

	private void prepareNavigationDrawerItems() {
		mDrawerItems = new ArrayList<DrawerItem>();

		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_log,R.string.drawer_title_log));//admin log
		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_sop,R.string.drawer_title_sop)); // sop
		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_video,R.string.drawer_title_video)); // video
		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_article,R.string.drawer_title_article)); // article
		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_catalog,R.string.drawer_title_catalog)); // catalog
		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_quiz,R.string.drawer_title_quiz)); // quiz
		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_training,R.string.drawer_title_training)); // training
		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_history,R.string.drawer_title_history));//training history




		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_home,R.string.drawer_title_home)); // home
		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_search,R.string.drawer_title_search)); // search
		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_myfriends,R.string.drawer_title_myfriends)); // friends

		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_newgroup,R.string.drawer_title_newgroup)); // new group sementara dihilangkan
		//mDrawerItems.add(new DrawerItem(R.string.drawer_icon_blockeduser,R.string.drawer_title_blockeduser));
		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_setting,R.string.drawer_title_settings)); // settings
		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_logout,R.string.drawer_title_logout));//logout


	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		sesion.setAppState(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//MenuInflater inflater = getMenuInflater();

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			fragmentposition = position;
			//Log.e("click","position: "+position);
			selectItem();
		}
	}

	private void selectItem() {

		if (fragmentposition < 1) {
			return;
		}
		if(fragmentposition==14){
			new logoutuser().execute();
			return;
		}

		Fragment fragment = getFragmentByDrawerTag(fragmentposition);
		commitFragment(fragment);

		//String drawerTitle = getString(mDrawerItems.get(fragmentposition - 1).getTitle());
		if(fragmentposition<70)
		{
			mDrawerList.setItemChecked(fragmentposition, true);
			setTitle(mDrawerItems.get(fragmentposition - 1).getTitle());
			mDrawerLayout.closeDrawer(mDrawerList);
		}
		else
		{
			if(fragmentposition==70)
				setTitle("Artikel");
			else if(fragmentposition==71)
				setTitle("Panduan");
			else if(fragmentposition==72)
				setTitle("Quiz");
			else if(fragmentposition>=73&&fragmentposition<=76)
				setTitle("Training");

			mDrawerLayout.closeDrawer(mDrawerList);
		}

	}

	private Fragment getFragmentByDrawerTag(int position) {
		Fragment fragment = ListLog.newInstance();
		if (fragmentposition == 9) {
			fragment = MyMessageActivity.newInstance();
		} else if (fragmentposition == 10) {
			DataManager.action = "search";
			fragment = AllUsersActivity.newInstance();
		} else if (fragmentposition == 11) {
			DataManager.action = "friends";
			fragment = AllUsersActivity.newInstance();
		} else if (fragmentposition == 12) {
			fragment = AddGroup.newInstance();
		} else if (fragmentposition == 13) {

			fragment = SettingsActivity.newInstance();
		}
        //mpm menu
		else if (fragmentposition == 1) {// history training
			fragment = ListLog.newInstance();
		}
		else if (fragmentposition == 2) {//sop
			fragment = ListSOP.newInstance();//sementara
		}
        else if (fragmentposition == 3) {//video
            fragment = ListVideo.newInstance();
        }
        else if (fragmentposition == 4) {//article
			fragment = ListArticle.newInstance();
		}
		else if (fragmentposition == 5) {//catalog
			fragment = ListCategory.newInstance();
		}
		else if (fragmentposition == 6) {//quiz
			fragment = ListQuiz.newInstance();
		}
		else if (fragmentposition == 7) {//training
			fragment = ListTraining.newInstance();
		}
		else if (fragmentposition == 8) {// history training
			fragment = ListTrainingHistory.newInstance();
		}
		else if (fragmentposition == 70) {
			fragment = ArticlePage.newInstance(data);
		}
		else if (fragmentposition == 71) {
			fragment = SOPContent.newInstance(sopID,sopName,sopDesc);
		}
		else if (fragmentposition == 72) {
			fragment = QuizDetail.newInstance(data);
		}
		else if (fragmentposition == 73) {
			fragment = TrainingContent.newInstance(data);
		}
		else if (fragmentposition == 74) {
			fragment = TrainingDetail.newInstance(data,isPreTest);
		}
		else if (fragmentposition == 75) {
			fragment = ListModules.newInstance(trainingID);
		}
		else if (fragmentposition == 76) {
			fragment = ReadModule.newInstance(trainingModID);
		}
		else {
			fragment = ListLog.newInstance();
			new logoutuser().execute();
		}

		return fragment;
	}

	private class CommitFragmentRunnable implements Runnable {

		private Fragment fragment;

		public CommitFragmentRunnable(Fragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void run() {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();
		}
	}

	public void commitFragment(Fragment fragment) {
		mHandler.post(new CommitFragmentRunnable(fragment));
	}

	@Override
	public void setTitle(int titleId) {
		setTitle(getString(titleId));
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		if(mTitle.equals(getResources().getString(R.string.drawer_title_sop)))
			mTitle = mTitle.toString().split(" ")[0];
		if(mTitle.equals(getResources().getString(R.string.drawer_title_history)))
			mTitle = "Riwayat";
		//getSupportActionBar().setTitle(mTitle);
		toolbarTitle.setText(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onResume() {
		super.onResume();
		new getuserprofile().execute();
	}

	@Override
	public void onBackPressed() {

		if (fragmentposition == 1) {
			new AlertDialog.Builder(this)
					.setTitle("Really Exit?")
					.setMessage("Are you sure you want to exit?")
					.setNegativeButton(android.R.string.no, null)
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface arg0,
										int arg1) {
									Intent intent = new Intent(
											Intent.ACTION_MAIN);
									intent.addCategory(Intent.CATEGORY_HOME);
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									finish();
									startActivity(intent);
								}
							}).create().show();
		}
		else if(fragmentposition==70){
			fragmentposition = 4;
			selectItem();
		}
		else if(fragmentposition==71){
			fragmentposition = 2;
			selectItem();
		}
		else if(fragmentposition==72){
			fragmentposition = 6;
			selectItem();
		}
		else if(fragmentposition==73){
			fragmentposition=7;
			selectItem();
		}
		else if(fragmentposition==75){
			fragmentposition=73;
			selectItem();
		}
		else if(fragmentposition==74||fragmentposition==76){
			fragmentposition--;
			selectItem();
		}
		else {
			fragmentposition = 1;
			selectItem();
		}

	}

	public class getuserprofile extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.getuserprofile(getApplicationContext(),
					sesion.getuserid(), deviceid, sesion.getuserid());

			return "";

		}

		@Override
		protected void onPostExecute(String result) {

			if (response) {

				sesion.setProfilepic(DataManager.alluserlist.get(0)
						.getProfilepic());
			}

		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}

	private class logoutuser extends AsyncTask<String, Void, String> {

		ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(MainActivity.this, "", "Logging Out", true);

		}

		@Override
		protected String doInBackground(String... params) {

			APIManager.logout(sesion.getuserid(), deviceid);

			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {

			progressDialog.dismiss();

			if (DataManager.status.equals("Logout successful")) {

				sesion.logoutUser();

				Intent intent = new Intent(MainActivity.this,LoginActivity.class);
				finish();
				startActivity(intent);
				Toast.makeText(getApplicationContext(),
						"Logout Successfully..", Toast.LENGTH_LONG).show();

			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}
}