package com.kiddos.nuktimetable;

import android.app.*;
import android.content.*;
import android.net.*;
import android.net.wifi.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import java.io.*;
import java.net.*;

public class LoginFragment extends Fragment implements View.OnClickListener {
	public static final String CONTENT_URL = "http://elearning.nuk.edu.tw/m_student/m_stu_index.php";
	public static final String LOGIN_URL = "http://stu.nuk.edu.tw/GEC/login_at2.asp";
	public static final String PREFERENCE = "com.kiddos.nuktimetable.prefs";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";
	private static final String USERNAME = "stuid";
	private static final String PASSWORD = "stupw";
	private static final String SETURL = "seturl";
	private static final String SETURL_VALUE = "http://elearning.nuk.edu.tw/";
	private static final String CHKID = "CHKID";
	private static final String CHKID_VALUE = "9587";
	private static final String POST_METHOD = "POST";
	private static final String RESULT_WRONG_CREDENTIALS = "wrong";
	private static final String RESULT_EXCEPTION_OCCUR = "exception";
	private static final int CONNECTION_TIMEOUT = 6000;
	private static final int READ_TIMEOUT = 6000;
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

				new LoginTask().execute(username, password);
				break;
			case R.id.btnClear:
				this.username.setText("");
				this.password.setText("");
				break;
		}
	}

	private class LoginTask extends AsyncTask<String, Void, String> {
		private ProgressDialog dialog;
		@Override
		protected void onPreExecute() {
			// start progress dialog
			dialog = new ProgressDialog(getActivity());
			dialog.setTitle(getResources().getString(R.string.logging_in));
			dialog.setMessage(getResources().getString(R.string.verifying));
			dialog.show();
		}

		@Override
		protected String doInBackground(String... arg) {
			try {
				final String username = arg[0];
				final String password = arg[1];
				URL url = new URL(LOGIN_URL);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod(POST_METHOD);
				connection.setConnectTimeout(CONNECTION_TIMEOUT);
				connection.setReadTimeout(READ_TIMEOUT);
				connection.setRequestProperty("Connection", "keep-alive");
				connection.setRequestProperty("Accept-Charset", "utf-8");
				connection.setRequestProperty("Cookie", "");
				connection.setDoInput(true);
				connection.setDoOutput(true);

				Uri.Builder builder = new Uri.Builder().
						appendQueryParameter(USERNAME, username).
						appendQueryParameter(PASSWORD, password).
						appendQueryParameter(SETURL, SETURL_VALUE).
						appendQueryParameter(CHKID, CHKID_VALUE);

				final String query = builder.build().getEncodedQuery();
				OutputStream out = connection.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
				writer.write(query);
				writer.flush();
				writer.close();
				out.close();

				connection.connect();

				final StringBuilder response = new StringBuilder();
				InputStream in = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
					response.append(line);
				}
				if (!response.toString().contains(CONTENT_URL)) {
					Log.i("LoginTask", "Wrong Username/Password");
					return RESULT_WRONG_CREDENTIALS;
				}

				final String cookies = connection.getHeaderField("Set-Cookie");
				url = new URL(CONTENT_URL);
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(CONNECTION_TIMEOUT);
				connection.setReadTimeout(READ_TIMEOUT);
				connection.setRequestProperty("Connection", "keep-alive");
				connection.setRequestProperty("Accept-Charset", "utf-8");
				connection.setRequestProperty("Cookie", cookies);
				connection.setDoInput(true);
				connection.setDoOutput(true);

				final StringBuilder content = new StringBuilder();
				in = connection.getInputStream();
				reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					content.append(line);
				}
				connection.disconnect();
				return content.toString();
			} catch (IOException e) {
				e.printStackTrace();
				return RESULT_EXCEPTION_OCCUR;
			}
		}

		@Override
		protected void onPostExecute(String content) {
			switch (content) {
				case RESULT_WRONG_CREDENTIALS:
					errorMsg.setText(getResources().getString(R.string.wrong_username_password));
					dialog.setMessage(getResources().getString(R.string.login_fail));
					break;
				case RESULT_EXCEPTION_OCCUR:
					errorMsg.setText(getResources().getString(R.string.conntection_timeout));
					dialog.setMessage(getResources().getString(R.string.fail));
					break;
				default:
					try {
						OnLoginListener handler = (OnLoginListener) getActivity();
						handler.onLogin(content);
					} catch (ClassCastException e) {
						e.printStackTrace();
					}
					break;
			}
			dialog.dismiss();
		}
	}
}
