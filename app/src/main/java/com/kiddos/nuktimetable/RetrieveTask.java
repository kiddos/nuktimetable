package com.kiddos.nuktimetable;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.*;
import java.net.*;

public class RetrieveTask extends AsyncTask<String, Void, String> {
	public static final String CONTENT_URL = "http://elearning.nuk.edu.tw/m_student/m_stu_index.php";
	public static final String LOGIN_URL = "http://stu.nuk.edu.tw/GEC/login_at2.asp";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";
	public static final String USERNAME = "stuid";
	public static final String PASSWORD = "stupw";
	public static final String SETURL = "seturl";
	public static final String SETURL_VALUE = "http://elearning.nuk.edu.tw/";
	public static final String CHKID = "CHKID";
	public static final String CHKID_VALUE = "9587";
	public static final String POST_METHOD = "POST";
	public static final String RESULT_WRONG_CREDENTIALS = "wrong";
	public static final String RESULT_EXCEPTION_OCCUR = "exception";
	public static final int CONNECTION_TIMEOUT = 6000;
	public static final int READ_TIMEOUT = 6000;
	private static final String MODE_LOGIN = "mode_login";
	private static final String MODE_RELOAD = "mode_reload";
	private Context context;
	private ProgressDialog dialog;
	private TextView errorMsg;
	private String mode;
	private OnLoginListener handler;

	public RetrieveTask(Context context, final OnLoginListener handler, final TextView errorMsg) {
		this.context = context;
		this.handler = handler;
		this.errorMsg = errorMsg;

		this.mode = MODE_LOGIN;
	}

	public RetrieveTask(Context context) {
		this.context = context;


		this.mode = MODE_RELOAD;
	}

	@Override
	protected void onPreExecute() {
		// start progress dialog
		dialog = new ProgressDialog(context);
		dialog.setTitle(context.getResources().getString(R.string.logging_in));
		dialog.setMessage(context.getResources().getString(R.string.verifying));
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
			connection.disconnect();

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
				errorMsg.setText(context.getResources().getString(R.string.wrong_username_password));
				dialog.setMessage(context.getResources().getString(R.string.login_fail));
				break;
			case RESULT_EXCEPTION_OCCUR:
				errorMsg.setText(context.getResources().getString(R.string.conntection_timeout));
				dialog.setMessage(context.getResources().getString(R.string.fail));
				break;
			default:
				if (handler != null) {
					handler.onLogin(content);
				} else {
					Log.i("onPostExecute", "hander null");
					try {
						OnLoginListener handler = (OnLoginListener) context;
						handler.onLogin(content);
					} catch (ClassCastException e) {
						e.printStackTrace();
					}
				}
				break;
		}
		dialog.dismiss();
	}
}
