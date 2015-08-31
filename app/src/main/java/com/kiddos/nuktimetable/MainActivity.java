package com.kiddos.nuktimetable;

import android.app.*;
import android.os.*;
import android.view.*;

public class MainActivity extends Activity implements OnLoginListener {
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
		manager.beginTransaction().add(R.id.container, settingFragment).commit();
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
		// display the action bar
		setDisplayActionBar(true);
		// change to main fragment
		Bundle arg = new Bundle();
		arg.putString(MainFragment.KEY_CONTENT, webContent);
		mainFragment.setArguments(arg);
		getFragmentManager().beginTransaction().replace(R.id.container, mainFragment).commit();
	}
}
