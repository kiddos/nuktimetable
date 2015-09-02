package com.kiddos.nuktimetable;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;

import java.io.*;
import java.util.*;

public class MainFragment extends Fragment {
	public static final String KEY_CONTENT = "content";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		GridView schedule = (GridView) rootView.findViewById(R.id.gvSchedule);
		schedule.setAdapter(new ScheduleAdpater(getActivity()));

		try {
			InputStream in = getResources().openRawResource(R.raw.data);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line;
			StringBuilder content = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}
			HTMLParser parser = new HTMLParser(content.toString());
			ArrayList<Course> courses = parser.getCourses();
			for (Course c : courses) {
				System.out.println(c.toString());
			}
		} catch(IOException e) {
			e.printStackTrace();
		}

//		TextView test = (TextView) rootView.findViewById(R.id.test);
//		Bundle arg = getArguments();
//		String webContent = arg.getString(KEY_CONTENT);
//		test.setText(webContent);
		return rootView;
	}

	private class ScheduleAdpater extends BaseAdapter {
		private static final int TOTAL_ITEMS = 6 * 15;
		private Context context;
		private int[] layoutId = new int[TOTAL_ITEMS];

		public ScheduleAdpater(Context context) {
			this.context = context;

			for (int i = 0 ; i < TOTAL_ITEMS ; i ++) {
				if (i % 6 == 0) {
					layoutId[i] = R.layout.block_item;
				} else {
					layoutId[i] = R.layout.class_item;
				}
			}
		}

		@Override
		public int getCount() {
			return TOTAL_ITEMS;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(layoutId[position], parent, false);
			} else {
				int id = convertView.getId();
				if (id != layoutId[position]) {
					LayoutInflater inflater = LayoutInflater.from(context);
					convertView = inflater.inflate(layoutId[position], parent, false);
				}
			}
			return convertView;
		}
	}
}
