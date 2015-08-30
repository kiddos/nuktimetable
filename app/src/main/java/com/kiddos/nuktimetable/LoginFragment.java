package com.kiddos.nuktimetable;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.*;
import android.util.*;
import android.view.*;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;

public class LoginFragment extends Fragment implements View.OnClickListener {
	public static final String CONTENT_URL = "http://elearning.nuk.edu.tw/m_student/m_stu_index.php";
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
				String username = this.username.getText().toString();
				String password = this.password.getText().toString();

				// logging in
				final WebView webView = new WebView(getActivity());
				final JavascriptHandler handler = new JavascriptHandler(this.handler);
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
}
