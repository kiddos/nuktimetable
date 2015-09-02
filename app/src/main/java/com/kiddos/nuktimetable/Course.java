package com.kiddos.nuktimetable;

import java.io.Serializable;
import java.util.Arrays;

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

	public String getBlocks() {
		return blocks;
	}

	public String getClassroom() {
		return classroom;
	}

	public int getViewCount() {
		return viewCount;
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
			String[] data = tb.split("_");
			String[] timeBlocks = new String[data.length -1];
			for (int i = 1 ; i < data.length ; i ++) {
				timeBlocks[i-1] = data[i];
			}
			return timeBlocks;
		} catch (IndexOutOfBoundsException e) {
			return new String[]{};
		}
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
	public int compareTo(Course course) {
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
				return this.courseName.compareTo(course.getCourseName());
			}
		}
	}
}
