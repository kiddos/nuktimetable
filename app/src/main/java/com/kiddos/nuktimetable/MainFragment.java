package com.kiddos.nuktimetable;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.os.*;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.*;
import android.widget.*;

//import java.io.*;
import java.util.*;

public class MainFragment extends Fragment {
	public static final String KEY_CONTENT = "content";
	private GridView weekday, schedule;
	private WeekdayAdapter weekdayAdapter;
	private ScheduleAdapter scheduleAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		weekday = (GridView) rootView.findViewById(R.id.gvWeekday);
		schedule = (GridView) rootView.findViewById(R.id.gvSchedule);

		try {
			// testing
//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					getResources().openRawResource(R.raw.test), "UTF-8"));
//			StringBuilder builder = new StringBuilder();
//			String line;
//			while ((line = reader.readLine()) != null) {
//				builder.append(line).append('\n');
//			}
//			reader.close();
//			final String content = builder.toString();

			final SharedPreferences prefs = getActivity().getSharedPreferences(
					MainActivity.PREFERENCE, Context.MODE_PRIVATE);
			final int displayType = prefs.getInt(
					MainActivity.KEY_DISPLAY_TYPE, 0);

			// retrieve old content
			final Bundle arg = getArguments();
			final String oldContent = arg.getString(KEY_CONTENT, "");
			final String content;

			final ArrayList<Course> courses;
			if (RetrieveTask.Type.getType(displayType) ==
          RetrieveTask.Type.COURSE_SELECTION) {
				Log.i("MainFragment", "retrieve course selection data");
				content = prefs.getString(MainActivity.KEY_COURSE_SELECTION_DATA, "");
				final LatestHTMLParser parser = new LatestHTMLParser(content);
				courses = parser.getCourses();
			} else if (RetrieveTask.Type.getType(displayType) ==
                 RetrieveTask.Type.PREVIEW) {
        Log.i("MainFragment", "retrieve preview data");
        content = prefs.getString(MainActivity.KEY_PREVIEW_DATA, "");
        final PreviewHTMLParser parser = new PreviewHTMLParser(content);
        courses = parser.getCourses();
      } else {
				Log.i("MainFragment", "should NOT display latest");
				content = prefs.getString(MainActivity.KEY_E_LEARNING_DATA, "");
				final HTMLParser parser = new HTMLParser(content);
				courses = parser.getCourses();
			}

			// correct the saved content
			if (!content.equals(oldContent)) {
				arg.putString(MainFragment.KEY_CONTENT, content);
			}

			// sort the course
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
				if (hasSaturdayCourse(latestCourses)) {
					schedule.setNumColumns(7);
					scheduleAdapter = new ScheduleAdapter(getActivity(), latestCourses, 7);
					weekday.setNumColumns(7);
					weekdayAdapter = new WeekdayAdapter(getActivity(), 7);
				} else {
					schedule.setNumColumns(6);
					scheduleAdapter = new ScheduleAdapter(getActivity(), latestCourses, 6);
					weekday.setNumColumns(6);
					weekdayAdapter = new WeekdayAdapter(getActivity(), 6);
				}
				schedule.setAdapter(scheduleAdapter);
				weekday.setAdapter(weekdayAdapter);

				// debug info
				for (Course c : latestCourses) {
					Log.i("course", c.toString());
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rootView;
	}

	public static boolean hasSaturdayCourse(ArrayList<Course> courses) {
		for (Course course : courses) {
			if (course.getWeekDay() == 6) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    final SharedPreferences prefs = getActivity().
        getSharedPreferences(LoginFragment.PREFERENCE, Context.MODE_PRIVATE);
    final SharedPreferences mainPrefs = getActivity().
        getSharedPreferences(MainActivity.PREFERENCE, Context.MODE_PRIVATE);

    // get the store username / password
    final String username = prefs.getString(LoginFragment.KEY_USERNAME, "");
    final String password = prefs.getString(LoginFragment.KEY_PASSWORD, "");
    int id = item.getItemId();
		if (id == R.id.action_reload) {
			Log.i("onOptionsItemSelected", "reload");
			// reload task
      int displayType = mainPrefs.getInt(MainActivity.KEY_DISPLAY_TYPE, 0);
      Log.i("MainFragment", "display type:" + displayType);
			new RetrieveTask(getActivity(), weekday, schedule, weekdayAdapter,
					scheduleAdapter, RetrieveTask.Type.getType(displayType)).
					execute(username, password);
    } else if (id == R.id.action_e_learning) {
      mainPrefs.edit().putInt(MainActivity.KEY_DISPLAY_TYPE,
          RetrieveTask.Type.E_LEARNING.getValue()).apply();
      Log.i("MainFragment", "value: " + RetrieveTask.Type.E_LEARNING.getValue());

      new RetrieveTask(getActivity(), weekday, schedule, weekdayAdapter,
          scheduleAdapter, RetrieveTask.Type.E_LEARNING).
          execute(username, password);
    } else if (id == R.id.action_course_selected) {
      mainPrefs.edit().putInt(MainActivity.KEY_DISPLAY_TYPE,
          RetrieveTask.Type.COURSE_SELECTION.getValue()).apply();
      Log.i("MainFragment", "value: " + RetrieveTask.Type.COURSE_SELECTION.getValue());

      new RetrieveTask(getActivity(), weekday, schedule, weekdayAdapter,
          scheduleAdapter, RetrieveTask.Type.COURSE_SELECTION).
          execute(username, password);
    } else if (id == R.id.action_preview) {
      mainPrefs.edit().putInt(MainActivity.KEY_DISPLAY_TYPE,
          RetrieveTask.Type.PREVIEW.getValue()).apply();
      Log.i("MainFragment", "value: " + RetrieveTask.Type.PREVIEW.getValue());

      new RetrieveTask(getActivity(), weekday, schedule, weekdayAdapter,
          scheduleAdapter, RetrieveTask.Type.PREVIEW).
          execute(username, password);
    }
		return super.onOptionsItemSelected(item);
	}

	public class WeekdayAdapter extends BaseAdapter {
		private int NUM_DAYS;
		private Context context;

		public WeekdayAdapter(Context context, final int numDays) {
			this.NUM_DAYS = numDays;
			this.context = context;
		}

		public void setNumDays(final int numDays) {
			this.NUM_DAYS = numDays;
		}

		@Override
		public int getCount() {
			return NUM_DAYS;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(R.layout.week_day_item, parent, false);
			}

			final TextView tvWeekdayItem = (TextView) convertView.findViewById(R.id.tvWeekdayItem);
			switch (position) {
				case 0:
					tvWeekdayItem.setText(getResources().getString(R.string.time_period));
					tvWeekdayItem.setGravity(Gravity.CENTER);
					break;
				case 1:
					tvWeekdayItem.setText(getResources().getString(R.string.monday_abbr));
					break;
				case 2:
					tvWeekdayItem.setText(getResources().getString(R.string.tuesday_abbr));
					break;
				case 3:
					tvWeekdayItem.setText(getResources().getString(R.string.wednesday_abbr));
					break;
				case 4:
					tvWeekdayItem.setText(getResources().getString(R.string.thursday_abbr));
					break;
				case 5:
					tvWeekdayItem.setText(getResources().getString(R.string.friday_abbr));
					break;
				case 6:
					tvWeekdayItem.setText(getResources().getString(R.string.saturday_abbr));
					break;
				case 7:
					tvWeekdayItem.setText(getResources().getString(R.string.sunday));
					break;
			}
			return convertView;
		}
	}

	public class ScheduleAdapter extends BaseAdapter {
		private static final int NUM_ROW = 15;
		private static final String EMPTY_TAG = "empty";
		private static final String NON_EMPTY_TAG = "non empty";
		private int NUM_COL;
		private int TOTAL_ITEMS;
		private int[] layoutId;
		private Context context;
		private ArrayList<Course> latestCourses;
		private Course[] courses;
		private int colorSeq = 0;

		public ScheduleAdapter(Context context, ArrayList<Course> latestCourses, final int numcol) {
			this.context = context;
			this.latestCourses = latestCourses;
			this.NUM_COL = numcol;
			this.TOTAL_ITEMS = NUM_COL * NUM_ROW;
			this.layoutId = new int[TOTAL_ITEMS];
			this.courses = new Course[TOTAL_ITEMS];

			setup();

			// debug usage
//			for (int i = 0 ; i < courses.length ; i ++) {
//				Course course = courses[i];
//				if (i % NUM_COL == 0) {
//					System.out.println("time");
//				}
//				if (course != null)
//					System.out.print(course.getCourseName());
//				else
//					System.out.print("null");
//			}
		}

		private void setup() {
			for (int i = 0; i < TOTAL_ITEMS; i++) {
				if (i % NUM_COL == 0) {
					layoutId[i] = R.layout.block_item;
				} else {
					layoutId[i] = R.layout.class_item;
				}
			}

			for (Course course : latestCourses) {
				final HashMap<Integer, String[]> timeBlockMap = course.getTimeBlockMap();
				final Set<Integer> weekDays = timeBlockMap.keySet();
				course.setColor(getColorSeq());
				for (int weekDay: weekDays) {
					final int col = NUM_COL;
					final String[] blocks = timeBlockMap.get(weekDay);
					int index = 0;

					for (String block : blocks) {
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
			}
		}

		private int getColorSeq() {
			final Context context = getActivity();
			int[] seq = {
					ContextCompat.getColor(context, R.color.purple),
					ContextCompat.getColor(context, R.color.yellow),
					ContextCompat.getColor(context, R.color.green),
					ContextCompat.getColor(context, R.color.red),
					ContextCompat.getColor(context, R.color.blue),
					ContextCompat.getColor(context, R.color.orange),
					ContextCompat.getColor(context, R.color.teal),
					ContextCompat.getColor(context, R.color.pink),
					ContextCompat.getColor(context, R.color.brown),
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
			for (; i < start + NUM_COL; i ++) {
				if (courses[i] != null) {
					return false;
				}
			}
			return true;
		}

		public void setLatestCourses(ArrayList<Course> latestCourses, final int numCol) {
			this.latestCourses = latestCourses;
			this.NUM_COL = numCol;
			this.TOTAL_ITEMS = this.NUM_COL * NUM_ROW;
			this.layoutId = new int[TOTAL_ITEMS];
			this.courses = new Course[TOTAL_ITEMS];

			setup();
		}

		@Override
		public int getCount() {
			return TOTAL_ITEMS;
		}

		@Override
		public Object getItem(int position) {
			return courses[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			boolean shouldUpdate = false;
			final Course course = courses[position];

			if (convertView == null) {
				// decide which layout to use
				LayoutInflater inflater = LayoutInflater.from(context);
				if (layoutId[position] == R.layout.block_item && isRowEmpty(position)) {
					convertView = inflater.inflate(R.layout.empty_block_item, parent, false);
					convertView.setTag(EMPTY_TAG);
				} else if (layoutId[position] == R.layout.class_item && isRowEmpty(position)){
					convertView = inflater.inflate(R.layout.empty_class_item, parent, false);
				} else if (layoutId[position] == R.layout.class_item && !isRowEmpty(position) &&
						courses[position] == null){
					convertView = inflater.inflate(R.layout.empty_high_class_item, parent, false);
				} else {
					convertView = inflater.inflate(layoutId[position], parent, false);
					convertView.setTag(NON_EMPTY_TAG);
				}
				shouldUpdate = true;
			} else {
				int id = convertView.getId();
				if (id != layoutId[position]) {
					LayoutInflater inflater = LayoutInflater.from(context);
					// decide which layout to use
					if (layoutId[position] == R.layout.block_item && isRowEmpty(position)) {
						convertView = inflater.inflate(R.layout.empty_block_item, parent, false);
						convertView.setTag(EMPTY_TAG);
					} else if (layoutId[position] == R.layout.class_item && isRowEmpty(position)){
						convertView = inflater.inflate(R.layout.empty_class_item, parent, false);
					} else if (layoutId[position] == R.layout.class_item && !isRowEmpty(position) &&
							courses[position] == null){
						convertView = inflater.inflate(R.layout.empty_high_class_item, parent, false);
					} else {
						convertView = inflater.inflate(layoutId[position], parent, false);
						convertView.setTag(NON_EMPTY_TAG);
					}
					shouldUpdate = true;
				}
			}

			if (shouldUpdate) {
				if (layoutId[position] == R.layout.block_item) {
					if (convertView.getTag().equals(NON_EMPTY_TAG)){
						// full block item
						final TextView time1 = (TextView) convertView.findViewById(R.id.tvTime1);
						final TextView time2 = (TextView) convertView.findViewById(R.id.tvTime2);
						final TextView block = (TextView) convertView.findViewById(R.id.tvBlock);
						switch (position / NUM_COL) {
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
					} else if (convertView.getTag().equals(EMPTY_TAG)) {
						// shorten block item
						final TextView block = (TextView) convertView.findViewById(R.id.tvBlock);
						switch (position / NUM_COL) {
							case 0: block.setText(getResources().getString(R.string.blockx)); break;
							case 1: block.setText(getResources().getString(R.string.block1)); break;
							case 2: block.setText(getResources().getString(R.string.block2)); break;
							case 3: block.setText(getResources().getString(R.string.block3)); break;
							case 4: block.setText(getResources().getString(R.string.block4)); break;
							case 5: block.setText(getResources().getString(R.string.blocky)); break;
							case 6: block.setText(getResources().getString(R.string.block5)); break;
							case 7: block.setText(getResources().getString(R.string.block6)); break;
							case 8: block.setText(getResources().getString(R.string.block7)); break;
							case 9: block.setText(getResources().getString(R.string.block8)); break;
							case 10: block.setText(getResources().getString(R.string.block9)); break;
							case 11: block.setText(getResources().getString(R.string.block10)); break;
							case 12: block.setText(getResources().getString(R.string.block11)); break;
							case 13: block.setText(getResources().getString(R.string.block12)); break;
							case 14: block.setText(getResources().getString(R.string.block13)); break;
						}
					}
				} else if (layoutId[position] == R.layout.class_item){
					if (course != null) {
						final TextView className = (TextView) convertView.findViewById(R.id.tvClassName);
						final TextView classroom = (TextView) convertView.findViewById(R.id.tvClassroom);
						final LinearLayout background = (LinearLayout) convertView.findViewById(R.id.background);
						// set all the views
						int orientation = getActivity().getResources().getConfiguration().orientation;
						if (orientation == Configuration.ORIENTATION_PORTRAIT) {
							if (className != null) className.setText(course.getAbbreCourseName());
						} else {
							if (className != null) className.setText(course.getCourseName());
						}
						if (classroom != null) classroom.setText(course.getClassroom());
						if (background != null) background.setBackgroundColor(course.getColor());
					}
				}
			}
			return convertView;
		}

	}
}
