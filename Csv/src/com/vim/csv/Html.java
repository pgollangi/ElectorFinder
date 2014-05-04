package com.vim.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Html {
	public static void main(String[] args) throws Exception {
		File dir = new File("./");
		File[] listFiles = dir.listFiles();
		for (File f : listFiles) {
			try {
				parse(f);
			} catch (Exception e) {
				System.out.println("Fail :" + f.getName());
			}
		}
	}

	private static void parse(File f) throws Exception {
		if (!f.getName().endsWith("s.html")) {
			return;
		}
		String data = readFile(f);
		String newFile = f.getName().replaceFirst("s.html", ".csv");
		File file = new File(f.getParent(), newFile);
		// if file doesnt exists, then create it
		if (file.exists()) {
			return;
		}
		System.out.println("Parsing :" + f.getName());
		file.createNewFile();
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		parse(data, bw);
		bw.close();
	}

	private static String readFile(File file) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append('\n');
		}
		reader.close();
		return stringBuilder.toString();
	}

	private static void parse(String full, BufferedWriter bw) {
		full = full.replaceAll("(<br/>)", "\n").replaceAll("<.+?>", "")
				.replaceAll("&.+?;", " ");
		String[] lines = full.split("\n");

		List<String> areas = new ArrayList<String>();
		int i = 0;
		for (; i < lines.length; i++) {
			String l = lines[i];
			if (l.isEmpty()) {
				continue;
			}
			if (l.contains("DETAILS OF PART AND POLLING AREA")) {
				i += 4;
				String a = lines[i++];
				while (a.isEmpty() || !a.contains("Main Town")) {
					if (a.isEmpty()) {
						a = lines[i++];
						continue;
					}
					areas.add(getArea(a));
					a = lines[i++];
				}
				break;
			}
		}
		boolean pageStart = false;
		boolean areaFound = false;
		int rows = 0;
		for (; i < lines.length; i++) {
			String l = lines[i];
			if (l.isEmpty()) {
				continue;
			}

			if (l.startsWith("Page No")) {
				areaFound = false;
				pageStart = true;
			}
			if (pageStart) {
				for (String a : areas) {
					if (l.contains(a)) {
						areaFound = true;
						i++;
						i++;
						break;
					}
				}
			}
			if (!areaFound) {
				continue;
			}
			// Reading page
			try {
				Member[] group = new Member[3];
				group[0] = new Member();
				group[1] = new Member();
				group[2] = new Member();

				i = initNumAndId(i, lines, group);

				i = initName(i, lines, group);

				i = readFathersName(i, lines, group);

				i = houseNum(i, lines, group);

				i = gender(i, lines, group);
				print(group, bw);
				i--;
				rows++;
				if (rows == 10) {
					rows = 0;
					pageStart = false;
					areaFound = false;
				}
			} catch (Exception e) {
				rows = 0;
				pageStart = false;
				areaFound = false;
			}
		}
	}

	private static String getArea(String line) {
		String[] split = line.split(",");
		String area = split[0].trim();
		int indexOf = area.indexOf(" ");
		if (indexOf > 0) {
			area = area.substring(indexOf).trim();
		}
		return area;
	}

	private static void print(Member[] group, BufferedWriter bw)
			throws IOException {
		for (Member m : group) {
			bw.write(m.toString());
			bw.write("\n");
		}
	}

	private static int gender(int i, String[] lines, Member[] group) {
		int count = 0;
		while (count != 3) {
			while (true) {
				String l = lines[i++].trim();
				if (l.contains("Sex:")) {
					Member m = group[count++];
					m.gender = l.contains("Female") ? "F" : "M";
					break;
				}
			}
		}
		return i;
	}

	private static int houseNum(int i, String[] lines, Member[] group) {
		int count = 0;
		while (count != 3) {
			while (true) {
				String l = lines[i++].trim();
				if (l.contains("House No:")) {
					break;
				}
			}
			String l = lines[i++].trim();
			if (l.isEmpty()) {
				l = lines[i++].trim();
			}
			Member m = group[count++];
			m.hus = l.replaceAll(",", " ").trim();
		}
		return i;
	}

	private static int readFathersName(int i, String[] lines, Member[] group) {
		int count = 0;
		while (count != 3) {
			while (true) {
				String l = lines[i++].trim();
				if (l.contains(":")) {
					i--;
					break;
				}
			}
			String l = lines[i++].trim();
			if (l.isEmpty()) {
				l = lines[i++].trim();
			}
			String[] split = l.split(":");
			String name = (split.length > 1) ? split[1] : "";
			if (name.isEmpty()) {
				i++;
				name = lines[i++].trim();
			}
			Member m = group[count++];
			m.relativeName = name.trim();
		}
		return i;
	}

	private static int initName(int i, String[] lines, Member[] group) {
		int count = 0;
		while (count != 3) {
			String l = lines[i++].trim();
			if (l.isEmpty()) {
				continue;
			}
			String[] split = l.split(":");
			String name = split.length > 1 ? split[1] : "";
			if (name.isEmpty()) {
				i++;
				name = lines[i++].trim();
			}
			Member m = group[count++];
			m.name = name.trim();
		}
		return i;
	}

	private static int initNumAndId(int i, String[] lines, Member[] group) {
		int count = 0;
		while (count != 3) {
			String l = lines[i++].trim();
			if (l.isEmpty()) {
				continue;
			}
			int num = 0;
			String id = "";
			boolean foundDigit = false;
			int s = 0;
			for (int j = 0; j < l.length(); j++) {
				if (Character.isDigit(l.charAt(j))) {
					foundDigit = true;
					continue;
				}
				if (!foundDigit) {
					s = j + 1;
					continue;
				}
				num = Integer.valueOf(l.substring(s, j));
				id = l.substring(j, l.length());
				break;
			}
			if (id.isEmpty()) {
				i++;
				id = lines[i++].trim();
			}
			Member m = group[count++];
			m.num = num;
			m.id = id.trim();
		}
		return i;
	}
}
