package com.kiddos.nuktimetable;

import android.support.annotation.NonNull;

import java.io.*;
import java.util.*;

public class Course implements Serializable, Comparable<Course> {
	private String courseId;
	private String courseName;
	private int courseYear;
	private String semester;
	private String blocks;
	private String classroom;
	private int viewCount;
	private int color;

	public Course(String courseId, String courseName, int courseYear,
				  String semester, String blocks, String classroom, int viewCount, int color) {
		this.courseId = courseId;
		this.courseName = courseName;
		this.courseYear = courseYear;
		this.semester = semester;
		this.blocks = blocks;
		this.classroom = classroom;
		this.viewCount = viewCount;
		this.color = color;
	}

	private int firstWordCharacter(String str) {
		for (int i = 0 ; i < str.length() ; i ++) {
			if ((str.charAt(i) < 48 || str.charAt(i) > 57) && str.charAt(i) != 45) {
				return i;
			}
		}
		return 0;
	}

	public String getCourseId() {
		return courseId;
	}

	public String getCourseName() {
		return courseName.substring(firstWordCharacter(courseName));
	}

	public int getCourseYear() {
		return courseYear;
	}

	public String getSemester() {
		return semester;
	}

	public String getClassroom() {
		return classroom;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getWeekDay() {
		int startBracket = blocks.indexOf('(');
		int endBracket = blocks.indexOf(')');
		String weekDay = blocks.substring(startBracket+1, endBracket);

		try {
			return Integer.parseInt(weekDay);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public String[] getTimeBlocks() {
		try {
			int endBracket = blocks.indexOf(')');
			String tb = blocks.substring(endBracket + 1);
			if (!tb.startsWith("_")) tb = "_" + tb;
			String[] data = tb.split("_");
			String[] timeBlocks = new String[data.length -1];
			System.arraycopy(data, 1, timeBlocks, 0, data.length - 1);
			return timeBlocks;
		} catch (IndexOutOfBoundsException e) {
			return new String[]{};
		}
	}

	public HashMap<Integer, String[]> getTimeBlockMap() {
		final HashMap<Integer, String[]> timeBlockMap = new HashMap<>();
		final String[] timeBlocks = blocks.split("\\(");
		for (String tb : timeBlocks) {
			if (!tb.equals("")) {
				final int weekday = Integer.parseInt(String.valueOf(tb.charAt(0)));
				final String[] timeblocks = tb.substring(tb.indexOf("_")+1).split("_");
				timeBlockMap.put(weekday, timeblocks);
			}
		}
		// testing
//		Set<Integer> wd = timeBlockMap.keySet();
//		for (int d : wd) {
//			System.out.println("week day: " + d);
//			System.out.println("time block: " + Arrays.toString(timeBlockMap.get(d)));
//		}
		return timeBlockMap;
	}

	@Override
	public String toString() {
		return "Course{" +
				"courseId='" + courseId + '\'' +
				", courseName='" + getCourseName() + '\'' +
				", courseYear=" + courseYear +
				", semester='" + semester + '\'' +
				", blocks='" + blocks + '\'' +
				", classroom='" + classroom + '\'' +
				", viewCount=" + viewCount +
				", color=" + color +
				", weekDay=" + getWeekDay() +
				", timeBlocks=" + Arrays.toString(getTimeBlocks()) +
				'}';
	}

	@Override
	public boolean equals(Object obj) {
		try {
			Course course = (Course) obj;
			return this.compareTo(course) == 0 &&
					this.courseId.equals(course.getCourseId());
		} catch (ClassCastException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public int compareTo(@NonNull Course course) {
		if (this.courseYear > course.getCourseYear()) {
			return -1;
		} else if (this.courseYear < course.getCourseYear()) {
			return 1;
		} else {
			if (this.semester.equals("上") && course.getSemester().equals("下")) {
				return 1;
			} else if (this.semester.equals("下") && course.getSemester().equals("上")) {
				return -1;
			} else {
				if (this.getWeekDay() < course.getWeekDay()) {
					return 1;
				} else if (this.getWeekDay() > course.getWeekDay()) {
					return -1;
				} else {
					return this.courseName.compareTo(course.getCourseName());
				}
			}
		}
	}
}
