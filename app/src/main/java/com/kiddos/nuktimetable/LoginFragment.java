package com.kiddos.nuktimetable;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.*;
import android.util.*;
import android.view.*;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;

public class LoginFragment extends Fragment implements View.OnClickListener {
	public static final String CONTENT_URL = "http://elearning.nuk.edu.tw/m_student/m_stu_index.php";
	public static final String PREFERENCE = "prefs";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";
	private static final String POST_URL = "http://stu.nuk.edu.tw/GEC/login_at2.asp";
	private static final String USERNAME = "stuid";
	private static final String PASSWORD = "stupw";
	private static final String SETURL = "seturl";
	private static final String SETURL_VALUE = "http://elearning.nuk.edu.tw/";
	private static final String CHKID = "CHKID";
	private static final String CHKID_VALUE = "9587";
	private EditText username, password;
	private OnLoginListener handler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_login, container, false);
		username = (EditText) rootView.findViewById(R.id.etUserName);
		password = (EditText) rootView.findViewById(R.id.etPassword);
		final Button login = (Button) rootView.findViewById(R.id.btnLogin);
		final Button clear = (Button) rootView.findViewById(R.id.btnClear);

		login.setOnClickListener(this);
		clear.setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		handler = (OnLoginListener) context;
	}

	private boolean isConnectingorConnected() {
		WifiManager manager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
		if (manager.isWifiEnabled()) {

		}
		return false;
	}

	@Override
	@SuppressLint({"setJavaScriptEnabled", "addJavascriptInterface"})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnLogin:
				if (username.getText().toString().equals("")) {
					Toast.makeText(getActivity(), getResources().getString(R.string.username_missing),
							Toast.LENGTH_SHORT).show();
					break;
				}
				if (password.getText().toString().equals("")) {
					Toast.makeText(getActivity(), getResources().getString(R.string.password_missing),
							Toast.LENGTH_SHORT).show();
					break;
				}
				final String username = this.username.getText().toString();
				final String password = this.password.getText().toString();

				// logging in
				final WebView webView = new WebView(getActivity());
				final JavascriptHandler handler = new JavascriptHandler();
				webView.getSettings().setJavaScriptEnabled(true);

				Uri.Builder builder = new Uri.Builder().
						appendQueryParameter(USERNAME, username).
						appendQueryParameter(PASSWORD, password).
						appendQueryParameter(SETURL, SETURL_VALUE).
						appendQueryParameter(CHKID, CHKID_VALUE);
				String query = builder.build().getEncodedQuery();
				webView.addJavascriptInterface(handler, JavascriptHandler.API);

				webView.setWebViewClient(new WebViewClient() {
					@Override
					public void onPageFinished(WebView view, String url) {
						if (!url.equals(CONTENT_URL)) {
							Log.i("load content", "load content");
							webView.loadUrl(JavascriptHandler.CHECK_RESPONSE);
							webView.loadUrl(CONTENT_URL);
						} else {
							if (handler.isPasswordCorrect()) {
								Log.i("read content", "read content");
								webView.loadUrl(JavascriptHandler.PROCESS_HTML);

								// store the username and password as log in preference
								SharedPreferences prefs = getActivity().getSharedPreferences(
										PREFERENCE, Context.MODE_PRIVATE);
								prefs.edit().putString(KEY_USERNAME, username).
										putString(KEY_PASSWORD, password).apply();
							} else {
								Log.i("Failed", "password/username incorrect");
							}
						}
					}
				});

				webView.postUrl(POST_URL, query.getBytes());
				break;
			case R.id.btnClear:
				this.username.setText("");
				this.password.setText("");
				break;
		}
	}

	private class JavascriptHandler {
		public static final String API = "api";
		public static final String CHECK_RESPONSE = "javascript:window.api." +
				"checkResponse('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');";
		public static final String PROCESS_HTML = "javascript:window.api." +
				"processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');";
		private boolean success = false;

		public JavascriptHandler() {
		}

		@SuppressWarnings("unused")
		@android.webkit.JavascriptInterface
		public void checkResponse(String response) {
			Log.i("response", response);
			success = response.contains(LoginFragment.CONTENT_URL);
		}

		@SuppressWarnings("unused")
		@android.webkit.JavascriptInterface
		public void processHTML(String html) {
			String[] lines = html.split("\n");
			for (String line : lines) {
				Log.i("line", line);
			}

			if (handler != null)
				handler.onLogin(html);
			else {
				handler = (OnLoginListener) getActivity();
				handler.onLogin(html);
			}
		}

		public boolean isPasswordCorrect() {
			return success;
		}
	}
}
