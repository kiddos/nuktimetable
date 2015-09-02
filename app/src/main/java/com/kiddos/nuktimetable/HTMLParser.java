package com.kiddos.nuktimetable;


import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.util.ArrayList;

public class HTMLParser {
	private static final String TARGET_ID = "myTable";
	private static final String TARGET_TAG = "tbody";
	private static final int INDEX_COURSE_ID = 0;
	private static final int INDEX_COURSE_NAME = 1;
	private static final int INDEX_COURSE_YEAR = 2;
	private static final int INDEX_COURSE_SEMESTER = 3;
	private static final int INDEX_COURSE_BLOCK = 4;
	private static final int INDEX_COURSE_CLASSROOM = 5;
	private static final int INDEX_COURSE_VIEWCOUNT = 6;
	private ArrayList<Course> courses;

	public HTMLParser(String html) {
		courses = new ArrayList<>();
		Document doc = Jsoup.parse(html);

		Element table = doc.getElementById(TARGET_ID);
		for (Element e : table.children()) {
			if (e.tagName().equals(TARGET_TAG)) {
				processTableBody(e);
			}
		}
	}

	private void processTableBody(Element tbody) {
		Elements elements = tbody.children();
		for (Element tr : elements) {
			Course course = processRowItem(tr);
			courses.add(course);
		}
	}

	private Course processRowItem(Element row) {
		Elements elements = row.children();

		String courseId = "";
		String courseName = "";
		int courseYear = 0;
		String semester = "";
		String blocks = "";
		String classroom = "";
		int viewCount = 0;
		int color = 0;

		for (int i = 0; i < elements.size(); i++) {
			Element e = elements.get(i);
			switch (i) {
				case INDEX_COURSE_ID:
					courseId = e.text();
					break;
				case INDEX_COURSE_NAME:
					courseName = e.text();
					break;
				case INDEX_COURSE_YEAR:
					try {
						courseYear = Integer.parseInt(e.text());
					} catch (NumberFormatException exc) {
						courseYear = 0;
					}
					break;
				case INDEX_COURSE_SEMESTER:
					semester = e.text();
					break;
				case INDEX_COURSE_BLOCK:
					blocks = e.text();
					break;
				case INDEX_COURSE_CLASSROOM:
					classroom = e.text();
					break;
				case INDEX_COURSE_VIEWCOUNT:
					try {
						viewCount = Integer.parseInt(e.text());
					} catch (NumberFormatException exc) {
						viewCount = 0; }
					break;
			}
		}

		return new Course(courseId, courseName, courseYear, semester,
				blocks, classroom, viewCount, color);
	}

	public ArrayList<Course> getCourses() {
		return courses;
	}
}
