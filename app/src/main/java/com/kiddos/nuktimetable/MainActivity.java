package com.kiddos.nuktimetable;

import android.app.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.*;
import android.view.*;

public class MainActivity extends Activity implements OnLoginListener {
	public static final String PREFERENCE = "main_prefs";
	public static final String KEY_DATA = "data";
	public static final String KEY_LOGIN_SUCCESS = "login";
	private Fragment settingFragment;
	private Fragment mainFragment;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		handler = new Handler();
		setDisplayActionBar(false);

		FragmentManager manager = this.getFragmentManager();
		settingFragment = new LoginFragment();
		mainFragment = new MainFragment();

		SharedPreferences prefs = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
		boolean lastSuccess = prefs.getBoolean(KEY_LOGIN_SUCCESS, false);
		if (lastSuccess) {
			setDisplayActionBar(true);
			String data = prefs.getString(KEY_DATA, "");
			Bundle arg = new Bundle();
			arg.putString(MainFragment.KEY_CONTENT, data);

			mainFragment.setArguments(arg);
			manager.beginTransaction().add(R.id.container, mainFragment).commit();
		} else {
			manager.beginTransaction().add(R.id.container, settingFragment).commit();
		}
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
			getFragmentManager().beginTransaction().replace(R.id.container, settingFragment).commit();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onLogin(String webContent) {
		// store the data in preference
		SharedPreferences prefs = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
		prefs.edit().putString(KEY_DATA, webContent).putBoolean(KEY_LOGIN_SUCCESS, true).apply();

		// display the action bar
		setDisplayActionBar(true);

		// change to main fragment
		Bundle arg = new Bundle();
		arg.putString(MainFragment.KEY_CONTENT, webContent);
		mainFragment.setArguments(arg);
		getFragmentManager().beginTransaction().replace(R.id.container, mainFragment).commit();
	}
}
