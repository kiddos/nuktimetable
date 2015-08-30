package com.kiddos.nuktimetable;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity implements OnLoginListener {
	private Fragment settingFragment;
	private Fragment mainFragment;
	private boolean hideMenu = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		FragmentManager manager = this.getFragmentManager();
		settingFragment = new LoginFragment();
		mainFragment = new MainFragment();
		manager.beginTransaction().add(R.id.container, settingFragment).commit();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);

		if (hideMenu) {
			for (int i = 0 ; i < menu.size() ; i ++) {
				menu.getItem(i).setVisible(false);
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onLogin(String webContent) {
		Bundle arg = new Bundle();
		arg.putString(MainFragment.KEY_CONTENT, webContent);
		mainFragment.setArguments(arg);

		getFragmentManager().beginTransaction().replace(R.id.container, mainFragment).commit();

		// display the menu
		hideMenu = false;
		invalidateOptionsMenu();
	}
}
