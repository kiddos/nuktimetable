package com.kiddos.nuktimetable;

import android.app.*;
import android.content.Context;
import android.os.*;
import android.view.*;
import android.widget.*;

public class MainFragment extends Fragment {
	public static final String KEY_CONTENT = "content";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		GridView schedule = (GridView) rootView.findViewById(R.id.gvSchedule);
		schedule.setAdapter(new ScheduleAdpater(getActivity()));

//		TextView test = (TextView) rootView.findViewById(R.id.test);
//		Bundle arg = getArguments();
//		String webContent = arg.getString(KEY_CONTENT);
//		test.setText(webContent);
		return rootView;
	}

	private class ScheduleAdpater extends BaseAdapter {
		private Context context;
		private int[] layoutId = {
			R.layout.block_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item,
			R.layout.block_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item,
			R.layout.block_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item,
			R.layout.block_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item,
			R.layout.block_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item,
			R.layout.block_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item,
			R.layout.block_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item,
			R.layout.block_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item,
			R.layout.block_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item,
			R.layout.block_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item,
			R.layout.block_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item,
			R.layout.block_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item,
			R.layout.block_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item,
			R.layout.block_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item,
			R.layout.block_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item,
			R.layout.block_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item, R.layout.class_item
		};

		public ScheduleAdpater(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return 6 * 15;
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
			if (convertView == null || convertView.getId() != layoutId[position]) {
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(layoutId[position], parent, false);
			}
			return convertView;
		}
	}
}
