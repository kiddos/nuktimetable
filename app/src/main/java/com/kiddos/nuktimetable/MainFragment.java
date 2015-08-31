package com.kiddos.nuktimetable;

import android.app.*;
import android.content.Context;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class MainFragment extends Fragment {
	public static final String KEY_CONTENT = "content";
	private GridViewAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//		TextView test = (TextView) rootView.findViewById(R.id.test);
//		final GridView grid = (GridView) rootView.findViewById(R.id.gvSchedule);
//		adapter = new GridViewAdapter(getActivity());
//		grid.setAdapter(adapter);

//		Bundle arg = getArguments();
//		String webContent = arg.getString(KEY_CONTENT);
//		test.setText(webContent);
		return rootView;
	}

	private class GridViewAdapter extends BaseAdapter {
		private Context context;
		private int[] layouts = {
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
		public GridViewAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return layouts.length;
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
			Log.i("Position", position + "");
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(layouts[position], parent, false);
				convertView.setLayoutParams(new GridView.LayoutParams(85, 85));
			} else if (convertView.getId() != layouts[position]) {
				convertView = LayoutInflater.from(context).inflate(layouts[position], parent, false);
				convertView.setLayoutParams(new GridView.LayoutParams(85, 85));
			}
			switch (position % 6) {
				case 0:
					break;
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
					break;
			}
			return convertView;
		}
	}
}
