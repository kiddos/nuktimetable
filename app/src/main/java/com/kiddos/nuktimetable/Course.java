package com.kiddos.nuktimetable;

import java.io.Serializable;

public class Course implements Serializable {
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

	public String getCourseId() {
		return courseId;
	}

	public String getCourseName() {
		return courseName;
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

	@Override
	public String toString() {
		return "Course{" +
				"courseId='" + courseId + '\'' +
				", courseName='" + courseName + '\'' +
				", courseYear=" + courseYear +
				", semester='" + semester + '\'' +
				", blocks='" + blocks + '\'' +
				", classroom='" + classroom + '\'' +
				", viewCount=" + viewCount +
				", color=" + color +
				'}';
	}
}
