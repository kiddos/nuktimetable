package com.kiddos.nuktimetable;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.TextView;

public class MainFragment extends Fragment {
	public static final String KEY_CONTENT = "content";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		TextView test = (TextView) rootView.findViewById(R.id.test);

		Bundle arg = getArguments();
		String webContent = arg.getString(KEY_CONTENT);
		test.setText(webContent);
		return rootView;
	}
}
