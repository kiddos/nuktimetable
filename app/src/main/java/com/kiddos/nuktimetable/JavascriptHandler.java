package com.kiddos.nuktimetable;

import android.util.Log;

/**
 * Created by joseph on 2015/8/31.
 */
public class JavascriptHandler {
	public static final String API = "api";
	public static final String CHECK_RESPONSE = "javascript:window.api." +
			"checkResponse('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');";
	public static final String PROCESS_HTML = "javascript:window.api." +
			"processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');";
	private OnLoginListener handler;
	private boolean success = false;

	public JavascriptHandler(OnLoginListener handler) {
		this.handler = handler;
	}

	@android.webkit.JavascriptInterface
	public void checkResponse(String response) {
		if (response.contains(LoginFragment.CONTENT_URL)) {
			success = true;
		} else {
			success = false;
		}
		Log.i("response", response);
	}

	@android.webkit.JavascriptInterface
	public void processHTML(String html) {
		String[] lines = html.split("\n");
		for (String line : lines) {
			Log.i("line", line);
		}

		handler.onLogin(html);
	}

	public boolean isPasswordCorrect() {
		return success;
	}
}
