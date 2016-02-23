package com.kiddos.nuktimetable;


import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.util.*;
import java.util.regex.*;

public class LatestHTMLParser {
	private static final String TABLE_BODY_TAG_NAME = "tbody";
	private static final int INDEX_COURSE_ID = 0;
	private static final int INDEX_COURSE_NAME = 1;
	private static final int INDEX_COURSE_BLOCK = 4;
	private static final int INDEX_COURSE_CLASSROOM = 5;
	private ArrayList<Course> courses;

	public LatestHTMLParser(String html) {
		courses = new ArrayList<>();
		Document doc = Jsoup.parse(html);
		Element table = doc.getElementsByTag("table").get(0);
		for (Element e : table.children()) {
			if (e.tagName().equals(TABLE_BODY_TAG_NAME)) {
				processTableBody(e);
			}
		}
	}

	private void processTableBody(Element tbody) {
		Elements elements = tbody.children();
		boolean first = true;
		for (Element tr : elements) {
			if (first) {
				first = false;
				continue;
			}
			System.out.println(tr.text());
			Course course = processRowItem(tr);
			courses.add(course);
		}
	}

	private Course processRowItem(Element row) {
		Elements elements = row.children();

		String courseId = "";
		String courseName = "";
		final Calendar currentTime = Calendar.getInstance();
		final int courseYear = currentTime.get(Calendar.YEAR);

		final String semester;
		if (currentTime.get(Calendar.MONTH) >= 1 && currentTime.get(Calendar.MONTH) <= 6) {
			 semester = "下";
		} else {
			semester = "上";
		}

		String blocks = "";
		String classroom = "";
		final int viewCount = 0;
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
				case INDEX_COURSE_BLOCK:
					final String blockData = e.text();
					Map<Integer, ArrayList<Integer>> block = new HashMap<>();
					Pattern p = Pattern.compile("(\\S)([0-9]+)");
					Matcher matcher = p.matcher(blockData);
					while (matcher.find()) {
						String day = matcher.group(1);
						int blockNumber = Integer.parseInt(matcher.group(2));
						int key = 0;
						switch (day) {
							case "一":
								key = 1;
								break;
							case "二":
								key = 2;
								break;
							case "三":
								key = 3;
								break;
							case "四":
								key = 4;
								break;
							case "五":
								key = 5;
								break;
							case "六":
								key = 6;
								break;
						}
						ArrayList<Integer> blockNum = block.get(key);
						if (blockNum == null) {
							blockNum = new ArrayList<>();
						}

						blockNum.add(blockNumber);
						block.put(key, blockNum);
					}

					for (int key : block.keySet()) {
						ArrayList<Integer> blockNum = block.get(key);
						blocks = String.format("(%d)", key);
						for (int b : blockNum) {
							blocks += "_" + b;
						}
					}
					System.out.println("blocks: " + blocks);
					break;
				case INDEX_COURSE_CLASSROOM:
					String[] classroomData = e.text().split(",");
					if (classroomData.length == 0) {
						classroom = "";
					} else {
						classroom = classroomData[0] == null ? "" : classroomData[0];
					}
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