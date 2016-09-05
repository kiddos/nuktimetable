package com.kiddos.nuktimetable;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.Log;
import android.view.*;

public class MainActivity extends Activity implements OnLoginListener {
	public static final String PREFERENCE = "main_prefs";
	public static final String KEY_E_LEARNING_DATA = "data";
	public static final String KEY_COURSE_SELECTION_DATA = "latest_data";
  public static final String KEY_PREVIEW_DATA = "latest_data";
	public static final String KEY_LOGIN_SUCCESS = "login";
	public static final String KEY_DISPLAY_TYPE = "display_type";
	public static final String KEY_FIRST_TIME_USING = "first_time";
	private Fragment loginFragment, mainFragment;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final SharedPreferences prefs = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
		final boolean firstTime = prefs.getBoolean(KEY_FIRST_TIME_USING, true);
		if (firstTime) {
			startActivity(new Intent(this, HelpActivity.class));
			prefs.edit().putBoolean(KEY_FIRST_TIME_USING, false).apply();
		}

		handler = new Handler();

		if (savedInstanceState != null) {
			// get the login success status
			// to decide rather to show actionbar
			boolean lastSuccess = prefs.getBoolean(KEY_LOGIN_SUCCESS, false);
			if (lastSuccess) setDisplayActionBar(true);
			else setDisplayActionBar(false);
			return;
		}

		loginFragment = new LoginFragment();
		mainFragment = new MainFragment();

		final FragmentManager fragmentManager = this.getFragmentManager();
		final boolean lastSuccess = prefs.getBoolean(KEY_LOGIN_SUCCESS, false);
		if (lastSuccess) {
			setDisplayActionBar(true);

      int displayType = prefs.getInt(KEY_DISPLAY_TYPE, 0);
			String data;
			if (RetrieveTask.Type.getType(displayType) ==
          RetrieveTask.Type.COURSE_SELECTION) {
				data = prefs.getString(KEY_COURSE_SELECTION_DATA, "");
        Log.i("MainActivity", "retrieve course selection data");
			} else if (RetrieveTask.Type.getType(displayType) ==
                 RetrieveTask.Type.PREVIEW) {
				data = prefs.getString(KEY_PREVIEW_DATA, "");
        Log.i("MainActivity", "retrieve preview data");
			} else {
        data = prefs.getString(KEY_E_LEARNING_DATA, "");
        Log.i("MainActivity", "retrieve e-learing data");
      }

			Bundle arg = new Bundle();
			arg.putString(MainFragment.KEY_CONTENT, data);

			mainFragment.setArguments(arg);
			fragmentManager.beginTransaction().replace(R.id.container, mainFragment).commit();
		} else {
			setDisplayActionBar(false);
			fragmentManager.beginTransaction().replace(R.id.container, loginFragment).commit();
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
//		final SharedPreferences prefs = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
//		if (prefs.getBoolean(KEY_SHOULD_DISPLAY_LATEST, false)) {
//			menu.findItem(R.id.action_show_latest).setIcon(R.drawable.ic_star_white_24dp);
//		} else {
//			menu.findItem(R.id.action_show_latest).setIcon(R.drawable.ic_star_border_white_24dp);
//		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
    final SharedPreferences prefs = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
		if (id == R.id.action_logout) {
			// set login to false
			// allow user to login again and for the next start up
			prefs.edit().putBoolean(KEY_LOGIN_SUCCESS, false).apply();

			setDisplayActionBar(false);
			if (loginFragment == null) {
				loginFragment = new LoginFragment();
			}
			getFragmentManager().beginTransaction().
					replace(R.id.container, loginFragment).commit();
			return true;
    } else if (id == R.id.action_help) {
			startActivity(new Intent(this, HelpActivity.class));
		} else if (id == R.id.action_info) {
			try {
				final AlertDialog.Builder builder =
            new AlertDialog.Builder(MainActivity.this);
				builder.setTitle(getResources().getString(R.string.info));
				builder.setMessage(getResources().getString(R.string.info_message));
				builder.setPositiveButton(getResources().getString(R.string.ok), null);
				builder.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (id == R.id.action_about) {
			try {
				final AlertDialog.Builder builder =
            new AlertDialog.Builder(MainActivity.this);
				builder.setTitle(getResources().getString(R.string.about));
				builder.setMessage(getResources().getString(R.string.about_message));
				builder.setPositiveButton(getResources().getString(R.string.ok), null);
				builder.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onLogin(String webContent) {
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
			// store the data in preference
			final SharedPreferences prefs =
          getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
			prefs.edit().putString(KEY_E_LEARNING_DATA, webContent).
          putBoolean(KEY_LOGIN_SUCCESS, true).apply();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			final SharedPreferences prefs =
          getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
			prefs.edit().putString(KEY_E_LEARNING_DATA, webContent).
          putBoolean(KEY_LOGIN_SUCCESS, false).apply();
			setDisplayActionBar(false);
		}
	}
}