package com.kiddos.nuktimetable;

import android.app.*;
import android.content.*;
import android.net.*;
import android.net.wifi.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class LoginFragment extends Fragment implements View.OnClickListener {
	public static final String PREFERENCE = "com.kiddos.nuktimetable.prefs";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";
	private EditText username, password;
	private TextView errorMsg;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_login, container, false);
		username = (EditText) rootView.findViewById(R.id.etUserName);
		password = (EditText) rootView.findViewById(R.id.etPassword);
		errorMsg = (TextView) rootView.findViewById(R.id.tvErrorMsg);
		final Button login = (Button) rootView.findViewById(R.id.btnLogin);
		final Button clear = (Button) rootView.findViewById(R.id.btnClear);

		// button event
		login.setOnClickListener(this);
		clear.setOnClickListener(this);

		// set initial login username/password if existed
		final SharedPreferences prefs = getActivity().
				getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
		final String un = prefs.getString(KEY_USERNAME, "");
		final String pw = prefs.getString(KEY_PASSWORD, "");
		username.setText(un);
		password.setText(pw);

		return rootView;
	}

	private boolean isConnectingOrConnected() {
		final WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
		final ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().
				getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo mobileInfo = connectivityManager.getActiveNetworkInfo();
		if (wifiManager != null && wifiManager.isWifiEnabled()) {
			return true;
		} else if (mobileInfo != null && mobileInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnLogin:
				if (!isConnectingOrConnected()) {
					Toast.makeText(getActivity(), getResources().
							getString(R.string.network_issue), Toast.LENGTH_SHORT).show();
					return;
				}

				if (username.getText().toString().equals("")) {
					Toast.makeText(getActivity(), getResources().getString(R.string.username_missing),
							Toast.LENGTH_SHORT).show();
					errorMsg.setText(getResources().getString(R.string.username_missing));
					break;
				}
				if (password.getText().toString().equals("")) {
					Toast.makeText(getActivity(), getResources().getString(R.string.password_missing),
							Toast.LENGTH_SHORT).show();
					errorMsg.setText(getResources().getString(R.string.password_missing));
					break;
				}
				final String username = this.username.getText().toString();
				final String password = this.password.getText().toString();

				final SharedPreferences prefs = getActivity().
						getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
				prefs.edit().putString(KEY_USERNAME, username).
						putString(KEY_PASSWORD, password).apply();

				new RetrieveTask(getActivity(), (OnLoginListener)getActivity(), errorMsg).
						execute(username, password);
				break;
			case R.id.btnClear:
				this.username.setText("");
				this.password.setText("");
				break;
		}
	}
}
