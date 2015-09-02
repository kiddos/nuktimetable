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
			Collections.sort(courses);

			if (courses.size() > 0) {
				Course latest = courses.get(0);
				ArrayList<Course> latestCourses = new ArrayList<>();
				for (Course course : courses) {
					if (course.getCourseYear() == latest.getCourseYear() &&
							course.getSemester().equals(latest.getSemester()))
						latestCourses.add(course);
				}
				schedule.setAdapter(new ScheduleAdapter(getActivity(), latestCourses));

				// debug info
				for (Course c : latestCourses) {
					System.out.println(c.toString());
				}
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

	private class ScheduleAdapter extends BaseAdapter {
		private static final int NUM_COL = 6;
		private static final int NUM_ROW = 15;
		private static final int TOTAL_ITEMS = NUM_COL * NUM_ROW;
		private int[] layoutId = new int[TOTAL_ITEMS];
		private Context context;
		private Course[] courses = new Course[TOTAL_ITEMS];
		private int colorSeq = 0;

		public ScheduleAdapter(Context context, ArrayList<Course> latestCourses) {
			this.context = context;

			for (int i = 0 ; i < TOTAL_ITEMS ; i ++) {
				if (i % 6 == 0) {
					layoutId[i] = R.layout.block_item;
				} else {
					layoutId[i] = R.layout.class_item;
				}
			}

			for (Course course : latestCourses) {
				int weekDay = course.getWeekDay();
				int index = 0;
				int col = NUM_COL;
				course.setColor(getColorSeq());
				for (String block : course.getTimeBlocks()) {
					switch (block) {
						case "X":
						case "x":
							index = weekDay;
							break;
						case "1":
							index = col + weekDay;
							break;
						case "2":
							index = 2 * col + weekDay;
							break;
						case "3":
							index = 3 * col + weekDay;
							break;
						case "4":
							index = 4 * col + weekDay;
							break;
						case "Y":
						case "y":
							index = 5 * col + weekDay;
							break;
						case "5":
							index = 6 * col + weekDay;
							break;
						case "6":
							index = 7 * col + weekDay;
							break;
						case "7":
							index = 8 * col + weekDay;
							break;
						case "8":
							index = 9 * col + weekDay;
							break;
						case "9":
							index = 10 * col + weekDay;
							break;
						case "10":
							index = 11 * col + weekDay;
							break;
						case "11":
							index = 12 * col + weekDay;
							break;
						case "12":
							index = 13 * col + weekDay;
							break;
						case "13":
							index = 14 * col + weekDay;
							break;
					}
					courses[index] = course;
				}
			}

			for (int i = 0 ; i < courses.length ; i ++) {
				Course course = courses[i];
				if (i % 6 == 0) {
					System.out.println("time");
				}
				if (course != null)
					System.out.print(course.getCourseName());
				else
					System.out.print("null");
			}
		}

		private int getColorSeq() {
			int[] seq = {R.color.red,
					R.color.orange,
					R.color.yellow,
					R.color.green,
					R.color.blue,
					R.color.teal,
					R.color.purple
			};

			int color = seq[colorSeq];
			colorSeq ++;
			if (colorSeq >= seq.length) colorSeq = 0;
			return color;
		}

		private boolean isRowEmpty(int startingPosition) {
			int i = startingPosition;
			while (i >= 0) {
				if (i % NUM_COL == 0) break;
				i --;
			}
			int start = i;
			for (; i < start + NUM_COL - 1 ; i ++) {
				if (courses[i] != null) {
					return false;
				}
			}
			return true;
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
			boolean shouldUpdate = false;
			final Course course = courses[position];
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				if (layoutId[position] == R.layout.block_item && isRowEmpty(position)) {
					convertView = inflater.inflate(R.layout.empty_block_item, parent, false);
				} else if (layoutId[position] == R.layout.class_item && isRowEmpty(position)){
					convertView = inflater.inflate(R.layout.empty_class_item, parent, false);
				} else if (layoutId[position] == R.layout.class_item && !isRowEmpty(position) &&
						courses[position] == null){
					convertView = inflater.inflate(R.layout.empty_high_class_item, parent, false);
				} else {
					convertView = inflater.inflate(layoutId[position], parent, false);
				}
				shouldUpdate = true;
			} else {
				int id = convertView.getId();
				if (id != layoutId[position]) {
					LayoutInflater inflater = LayoutInflater.from(context);
					if (layoutId[position] == R.layout.block_item && isRowEmpty(position)) {
						convertView = inflater.inflate(R.layout.empty_block_item, parent, false);
					} else if (layoutId[position] == R.layout.class_item && isRowEmpty(position)){
						convertView = inflater.inflate(R.layout.empty_class_item, parent, false);
					} else if (layoutId[position] == R.layout.class_item && !isRowEmpty(position) &&
							courses[position] == null){
						convertView = inflater.inflate(R.layout.empty_high_class_item, parent, false);
					} else {
						convertView = inflater.inflate(layoutId[position], parent, false);
					}
					shouldUpdate = true;
				}
			}

			if (shouldUpdate) {
				if (layoutId[position] == R.layout.block_item) {
					int id = convertView.getId();
					if (id == R.layout.block_item){
						final TextView time1 = (TextView) convertView.findViewById(R.id.tvTime1);
						final TextView time2 = (TextView) convertView.findViewById(R.id.tvTime2);
						final TextView block = (TextView) convertView.findViewById(R.id.tvBlock);
						switch (position / 6) {
							case 0:
								time1.setText(getResources().getString(R.string.timex_1));
								time2.setText(getResources().getString(R.string.timex_2));
								block.setText(getResources().getString(R.string.blockx));
								break;
							case 1:
								time1.setText(getResources().getString(R.string.time1_1));
								time2.setText(getResources().getString(R.string.time1_2));
								block.setText(getResources().getString(R.string.block1));
								break;
							case 2:
								time1.setText(getResources().getString(R.string.time2_1));
								time2.setText(getResources().getString(R.string.time2_2));
								block.setText(getResources().getString(R.string.block2));
								break;
							case 3:
								time1.setText(getResources().getString(R.string.time3_1));
								time2.setText(getResources().getString(R.string.time3_2));
								block.setText(getResources().getString(R.string.block3));
								break;
							case 4:
								time1.setText(getResources().getString(R.string.time4_1));
								time2.setText(getResources().getString(R.string.time4_2));
								block.setText(getResources().getString(R.string.block4));
								break;
							case 5:
								time1.setText(getResources().getString(R.string.timey_1));
								time2.setText(getResources().getString(R.string.timey_2));
								block.setText(getResources().getString(R.string.blocky));
								break;
							case 6:
								time1.setText(getResources().getString(R.string.time5_1));
								time2.setText(getResources().getString(R.string.time5_2));
								block.setText(getResources().getString(R.string.block5));
								break;
							case 7:
								time1.setText(getResources().getString(R.string.time6_1));
								time2.setText(getResources().getString(R.string.time6_2));
								block.setText(getResources().getString(R.string.block6));
								break;
							case 8:
								time1.setText(getResources().getString(R.string.time7_1));
								time2.setText(getResources().getString(R.string.time7_2));
								block.setText(getResources().getString(R.string.block7));
								break;
							case 9:
								time1.setText(getResources().getString(R.string.time8_1));
								time2.setText(getResources().getString(R.string.time8_2));
								block.setText(getResources().getString(R.string.block8));
								break;
							case 10:
								time1.setText(getResources().getString(R.string.time9_1));
								time2.setText(getResources().getString(R.string.time9_2));
								block.setText(getResources().getString(R.string.block9));
								break;
							case 11:
								time1.setText(getResources().getString(R.string.time10_1));
								time2.setText(getResources().getString(R.string.time10_2));
								block.setText(getResources().getString(R.string.block10));
								break;
							case 12:
								time1.setText(getResources().getString(R.string.time11_1));
								time2.setText(getResources().getString(R.string.time11_2));
								block.setText(getResources().getString(R.string.block11));
								break;
							case 13:
								time1.setText(getResources().getString(R.string.time12_1));
								time2.setText(getResources().getString(R.string.time12_2));
								block.setText(getResources().getString(R.string.block12));
								break;
							case 14:
								time1.setText(getResources().getString(R.string.time13_1));
								time2.setText(getResources().getString(R.string.time13_2));
								block.setText(getResources().getString(R.string.block13));
								break;
						}
					} else if (id == R.layout.empty_block_item) {
						final TextView block = (TextView) convertView.findViewById(R.id.tvBlock);
						switch (position / 6) {
							case 0:
								block.setText(getResources().getString(R.string.blockx));
								break;
							case 1:
								block.setText(getResources().getString(R.string.block1));
								break;
							case 2:
								block.setText(getResources().getString(R.string.block2));
								break;
							case 3:
								block.setText(getResources().getString(R.string.block3));
								break;
							case 4:
								block.setText(getResources().getString(R.string.block4));
								break;
							case 5:
								block.setText(getResources().getString(R.string.blocky));
								break;
							case 6:
								block.setText(getResources().getString(R.string.block5));
								break;
							case 7:
								block.setText(getResources().getString(R.string.block6));
								break;
							case 8:
								block.setText(getResources().getString(R.string.block7));
								break;
							case 9:
								block.setText(getResources().getString(R.string.block8));
								break;
							case 10:
								block.setText(getResources().getString(R.string.block9));
								break;
							case 11:
								block.setText(getResources().getString(R.string.block10));
								break;
							case 12:
								block.setText(getResources().getString(R.string.block11));
								break;
							case 13:
								block.setText(getResources().getString(R.string.block12));
								break;
							case 14:
								block.setText(getResources().getString(R.string.block13));
								break;
						}
					}
				} else if (layoutId[position] == R.layout.class_item){
					if (course != null) {
						final TextView className = (TextView) convertView.findViewById(R.id.tvClassName);
						final TextView classroom = (TextView) convertView.findViewById(R.id.tvClassroom);
						final TextView instructor = (TextView) convertView.findViewById(R.id.tvInstructor);
						className.setText(course.getCourseName());
						classroom.setText(course.getClassroom());
						instructor.setText("");
					}
				}
			}
			return convertView;
		}
	}
}
