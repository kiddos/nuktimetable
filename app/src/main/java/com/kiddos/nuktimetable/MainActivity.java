package com.kiddos.nuktimetable;

import android.app.*;
import android.content.*;
import android.content.SharedPreferences;
import android.os.*;
import android.util.Log;
import android.view.*;

public class MainActivity extends Activity implements OnLoginListener {
	public static final String PREFERENCE = "main_prefs";
	public static final String KEY_DATA = "data";
	public static final String KEY_LOGIN_SUCCESS = "login";
	private Fragment loginFragment;
	private Fragment mainFragment;
	private Handler handler;
	private FragmentManager fragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		handler = new Handler();

		if (savedInstanceState != null) {
			Log.i("onCreate", "data exist");
			setDisplayActionBar(true);
			return;
		}
		setDisplayActionBar(false);

		loginFragment = new LoginFragment();
		mainFragment = new MainFragment();

		fragmentManager = this.getFragmentManager();
		final SharedPreferences prefs = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
		boolean lastSuccess = prefs.getBoolean(KEY_LOGIN_SUCCESS, false);
		if (lastSuccess) {
			setDisplayActionBar(true);
			String data = prefs.getString(KEY_DATA, "");
			Bundle arg = new Bundle();
			arg.putString(MainFragment.KEY_CONTENT, data);

			mainFragment.setArguments(arg);
			fragmentManager.beginTransaction().replace(R.id.container, mainFragment).commit();
		} else {
			setDisplayActionBar(false);
			fragmentManager.beginTransaction().replace(R.id.container, loginFragment).commit();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

//		final SharedPreferences prefs = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
//		boolean lastSuccess = prefs.getBoolean(KEY_LOGIN_SUCCESS, false);
//		if (lastSuccess) {
//			setDisplayActionBar(true);
//			String data = prefs.getString(KEY_DATA, "");
//			Bundle arg = new Bundle();
//			arg.putString(MainFragment.KEY_CONTENT, data);
//
//			mainFragment.setArguments(arg);
//			fragmentManager.beginTransaction().replace(R.id.container, mainFragment).commit();
//		} else {
//			setDisplayActionBar(false);
//			fragmentManager.beginTransaction().replace(R.id.container, loginFragment).commit();
//		}
	}

	private void setDisplayActionBar(final boolean display) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				ActionBar actionBar = getActionBar();
				if (actionBar != null) {
					if (display) actionBar.show();
					else actionBar.hide();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_logout) {
			setDisplayActionBar(false);
			if (loginFragment == null) {
				loginFragment = new LoginFragment();
			}
			getFragmentManager().beginTransaction().
					replace(R.id.container, loginFragment).commit();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onLogin(String webContent) {
		// store the data in preference
		final SharedPreferences prefs = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
		prefs.edit().putString(KEY_DATA, webContent).putBoolean(KEY_LOGIN_SUCCESS, true).apply();

		// display the action bar
		setDisplayActionBar(true);

		// change to main fragment
		Bundle arg = new Bundle();
		arg.putString(MainFragment.KEY_CONTENT, webContent);
		if (mainFragment == null) {
			mainFragment = new MainFragment();
		}
		mainFragment.setArguments(arg);
		// protect from crashing
		// if the user decide to hide the ui
		// and the retrieve task return
		try {
			getFragmentManager().beginTransaction().
					replace(R.id.container, mainFragment).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
}
