package com.kiddos.nuktimetable;

import android.app.*;
import android.content.Context;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import java.io.*;
import java.net.*;

public class LoginFragment extends Fragment implements View.OnClickListener {
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

				new DownloadWebContent().execute(
						username.getText().toString(),
						password.getText().toString());
				break;
			case R.id.btnClear:
				username.setText("");
				password.setText("");
				break;
		}
	}

	private class DownloadWebContent extends AsyncTask<String, Void, String> {
		private static final String POST_URL = "http://stu.nuk.edu.tw/GEC/login_at2.asp";
//		private static final String POST_URL = "http://elearning.nuk.edu.tw/";
//		private static final String POST_URL = "http://elearning.nuk.edu.tw/m_student/m_stu_index.php";
		private static final String USERNAME = "stuid";
		private static final String PASSWORD = "stupw";
		private static final String TARGET = "seturl";
		private static final String TARGET_URL = "http://elearning.nuk.edu.tw/";
		private static final int READ_TIMEOUT = 6000;
		private static final int CONNECTION_TIMEOUT = 6000;
		private static final String METHOD = "POST";

		@Override
		protected String doInBackground(String... args) {
			StringBuilder content = new StringBuilder();
			try {
				String username = args[0];
				String password = args[1];
				URL url = new URL(POST_URL);

				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setReadTimeout(READ_TIMEOUT);
				connection.setConnectTimeout(CONNECTION_TIMEOUT);
				connection.setRequestMethod(METHOD);
				connection.setDoInput(true);
				connection.setDoOutput(true);

				Uri.Builder builder = new Uri.Builder().
						appendQueryParameter(USERNAME, username).
						appendQueryParameter(PASSWORD, password).
						appendQueryParameter(TARGET, TARGET_URL).
						appendQueryParameter("CHKID", "9587");
				String query = builder.build().getEncodedQuery();

				OutputStream out = connection.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
				writer.write(query);
				writer.flush();
				writer.close();
				out.close();

				connection.connect();

				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
					String line;
					while ((line = reader.readLine()) != null) {
						Log.i("line: ", line);
						content.append(line);
					}
				} else {
					Log.i("DownloadWebContent", "response code: " + connection.getResponseCode());
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ArrayIndexOutOfBoundsException e) {
				Log.i("DownloadWebContent", e.toString());
			}
			return content.toString();
		}

		@Override
		protected void onPostExecute(String content) {
			if (content.length() > 0) {
				Log.i("DownloadWebContent", "download success");
				handler.onLogin(content);
			} else {
				Log.i("DownloadWebContent", "download fail");
			}
		}
	}
}
