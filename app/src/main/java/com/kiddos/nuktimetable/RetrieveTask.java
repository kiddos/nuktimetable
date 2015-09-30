package com.kiddos.nuktimetable;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.Log;
import android.widget.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;

public class RetrieveTask extends AsyncTask<String, Void, String> {
	public static final String CONTENT_URL = "http://elearning.nuk.edu.tw/m_student/m_stu_index.php";
	public static final String LOGIN_URL = "http://stu.nuk.edu.tw/GEC/login_at2.asp";
	public static final String USERNAME = "stuid";
	public static final String PASSWORD = "stupw";
	public static final String SETURL = "seturl";
	public static final String SETURL_VALUE = "http://elearning.nuk.edu.tw/";
	public static final String CHKID = "CHKID";
	public static final String CHKID_VALUE = "9587";
	public static final String POST_METHOD = "POST";
	public static final String RESULT_WRONG_CREDENTIALS = "wrong";
	public static final String RESULT_EXCEPTION_OCCUR = "exception";
	public static final int CONNECTION_TIMEOUT = 12000;
	public static final int READ_TIMEOUT = 6000;
	private static final String MODE_LOGIN = "mode_login";
	private static final String MODE_RELOAD = "mode_reload";
	private Context context;
	private ProgressDialog dialog;
	private TextView errorMsg;
	private String mode;
	private OnLoginListener handler;
	private GridView weekday, schedule;
	private MainFragment.ScheduleAdapter scheduleAdapter;
	private MainFragment.WeekdayAdapter weekdayAdapter;

	public RetrieveTask(Context context, final OnLoginListener handler, final TextView errorMsg) {
		this.context = context;
		this.handler = handler;
		this.errorMsg = errorMsg;

		this.mode = MODE_LOGIN;
	}

	public RetrieveTask(final Context context,
						final GridView weekday,
						final GridView schedule,
						final MainFragment.WeekdayAdapter weekdayAdapter,
						final MainFragment.ScheduleAdapter scheduleAdapter) {
		this.context = context;
		this.weekday = weekday;
		this.schedule = schedule;
		this.weekdayAdapter = weekdayAdapter;
		this.scheduleAdapter = scheduleAdapter;

		this.mode = MODE_RELOAD;
	}

	@Override
	protected void onPreExecute() {
		// start progress dialog
		// protect from crashing
		// if the user decide to hide the ui
		// and the retrieve task return
		try {
			dialog = new ProgressDialog(context);
			dialog.setTitle(context.getResources().getString(R.string.logging_in));
			dialog.setMessage(context.getResources().getString(R.string.verifying));
			dialog.show();
		} catch (Exception e) {
			Log.i("onPreExecute", "Main UI is hidden");
		}
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
				if (mode.equals(MODE_LOGIN)) {
					errorMsg.setText(context.getResources().getString(R.string.wrong_username_password));
					if (dialog != null) dialog.setMessage(context.getResources().getString(R.string.login_fail));
				} else if (mode.equals(MODE_RELOAD)) {
					if (dialog != null) dialog.setMessage(context.getResources().getString(R.string.reload_fail));
				}
				break;
			case RESULT_EXCEPTION_OCCUR:
				if (mode.equals(MODE_LOGIN)) {
					errorMsg.setText(context.getResources().getString(R.string.connection_timeout));
					if (dialog != null) dialog.setMessage(context.getResources().getString(R.string.fail));
				} else if (mode.equals(MODE_RELOAD)){
					if (dialog != null) dialog.setMessage(context.getResources().getString(R.string.connection_timeout));
				}
				break;
			default:
				if (mode.equals(MODE_LOGIN)) {
					if (handler != null) {
						handler.onLogin(content);
					} else {
						Log.i("onPostExecute", "handler null");
						try {
							final OnLoginListener handler = (OnLoginListener) context;
							handler.onLogin(content);
						} catch (ClassCastException e) {
							e.printStackTrace();
						}
					}
				} else if (mode.equals(MODE_RELOAD)) {
					// store the data in preference
					final SharedPreferences prefs = context.getSharedPreferences(
							MainActivity.PREFERENCE, Context.MODE_PRIVATE);
					prefs.edit().
							putString(MainActivity.KEY_DATA, content).
							putBoolean(MainActivity.KEY_LOGIN_SUCCESS, true).apply();

					if (scheduleAdapter != null) {
						final HTMLParser parser = new HTMLParser(content);
						final ArrayList<Course> courses = parser.getCourses();
						// sort according to year and semester
						Collections.sort(courses);

						if (courses.size() > 0) {
							// find the latest courses and set adapter
							final Course latest = courses.get(0);
							final ArrayList<Course> latestCourses = new ArrayList<>();
							for (Course course : courses) {
								if (course.getCourseYear() == latest.getCourseYear() &&
										course.getSemester().equals(latest.getSemester()))
									latestCourses.add(course);
							}

							// update adapter
							if (MainFragment.hasSaturdayCourse(latestCourses)) {
								weekday.setNumColumns(7);
								weekdayAdapter.setNumDays(7);
								weekdayAdapter.notifyDataSetChanged();
								schedule.setNumColumns(7);
								scheduleAdapter.setLatestCourses(latestCourses, 7);
								scheduleAdapter.notifyDataSetChanged();
							} else {
								weekday.setNumColumns(6);
								weekdayAdapter.setNumDays(6);
								weekdayAdapter.notifyDataSetChanged();
								schedule.setNumColumns(6);
								scheduleAdapter.setLatestCourses(latestCourses, 6);
								scheduleAdapter.notifyDataSetChanged();
							}

							// debug info
							for (Course c : latestCourses) {
								System.out.println(c.toString());
							}
						}
					}
				}
				break;
		}

		// protect from crashing
		// if the user decide to hide the ui
		// and the retrieve task return
		try {
			if (dialog != null) dialog.dismiss();
		} catch (Exception e) {
			Log.i("onPostExecute", "Main UI is hidden");
		}
	}
}
