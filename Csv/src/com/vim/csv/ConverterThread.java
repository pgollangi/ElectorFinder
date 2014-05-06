package com.vim.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class ConverterThread extends Thread {

	private static final String CONVERT_COMMAND = "pdf2htmlEX ";

	private LinkedBlockingQueue<File> queue;
	private File dir;

	public ConverterThread(LinkedBlockingQueue<File> queue, File dir) {
		this.queue = queue;
		this.dir = dir;
	}

	@Override
	public void run() {
		File take = null;
		while (!queue.isEmpty()) {
			try {
				take = queue.take();
				convert(take);
			} catch (Exception e) {
				if (take != null) {
					System.out.println("Error while converting "
							+ take.getName());
				}
				e.printStackTrace();
			}
		}
	}

	private void convert(File take) throws Exception {
		convertToCSV(take);
	}

	private void convertToCSV(File child) throws Exception {
		String fName = child.getName();
		String htmlName = fName.replaceAll("(?i).pdf", ".html");
		String csvName = fName.replaceAll("(?i).pdf", ".csv");
		if (new File(dir, csvName).exists()) {
			// Already Converted to CSV
			// System.out.println("Already Converted : " + fName);
			return;
		}

		System.out.println("Converting to CSV: " + fName);
		File html = new File(dir, htmlName);
		if (!html.exists()) {
			// HTML converted..
			int exitVal = convertToHtml(child);
			if (exitVal != 0) {
				// cannot convert to html
				// System.out.println("Cannot convert to html: " + fName);
				return;
			}
		}

		String inData = readFile(html);

		File out = new File(dir, csvName);
		BufferedWriter w = new BufferedWriter(new FileWriter(out));

		// Convert to CSV
		convertToCSV(inData, w);
		w.close();

		// Delete HTML
		html.delete();
	}

	private int convertToHtml(File child) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		Process p = runtime.exec(CONVERT_COMMAND + child.getName());
		return p.waitFor();
	}

	private void convertToCSV(String in, BufferedWriter out) throws Exception {
		in = in.replaceAll("(?s)<script[^>]*>(.*?)</script>", "");
		in = in.replaceAll("(?s)<style[^>]*>(.*?)</style>", "");
		in = in.replaceAll("(?s)<span[^>]*>(.*?)</span>", "");
		in = in.replaceAll("<.+?>", "\n").replaceAll("&.+?;", "")
				.replaceAll("\\n+", "\n");

		String[] split = in.split("\n");

		int current = 0;
		int total = split.length;
		for (; current < total; current++) {
			if (split[current].isEmpty()) {
				continue;
			}
			if (isElector(split, current)) {
				// Read Member
				current = readMembers(split, current, out);
				continue;
			}

			if (isSupplementElector(split, current)) {
				// Read Member
				current = readSupplementMember(split, current, out);
			}

		}
	}

	private int readSupplementMember(String[] split, int current,
			BufferedWriter out) throws IOException {

		String sNumCheck = split[current - 1];

		Integer sNo = null;
		try {
			sNo = Integer.valueOf(sNumCheck.trim());
		} catch (Exception e) {
		}

		current = current + 4;

		Member m = new Member();

		String vId = split[current];
		m.id = vId;
		current++;

		StringBuffer buff = new StringBuffer();
		for (;; current++) {
			if (split[current].equals("Sex:")) {
				break;
			}
			buff.append(split[current]);
			buff.append("\n");
		}

		String totalName = buff.toString();
		String[] nameSplit = totalName.split("\n");

		int nameLength = nameSplit.length;
		String name = "";
		String relative = "";
		int devider = nameLength / 2;
		boolean isOddCount = nameLength % 2 != 0;
		for (int i = 0; i < nameLength; i++) {
			String cs = nameSplit[i];
			if (i < devider) {
				name += cs + " ";
			} else if (isOddCount) {
				if (nameSplit.length == 1
						|| cs.length() < nameSplit[i + 1].length()) {
					name += cs + " ";
				} else {
					relative += cs + " ";
				}
				isOddCount = false;
			} else {
				relative += cs + " ";
			}
		}
		m.name = name;
		m.relativeName = relative;

		// SEX
		current++;
		m.gender = String.valueOf(split[current].charAt(0)).toUpperCase();

		current++;

		buff = new StringBuffer();
		// Read Address
		for (;; current++) {
			String cs = split[current];
			if (cs.equalsIgnoreCase("Photo of the ")) {
				break;
			}
			if (cs.equalsIgnoreCase("D E L E T E D")) {
				m.name = "(DELETED)" + m.name;
				break;
			}
			buff.append(split[current]);
			buff.append(" ");
		}
		String address = buff.toString();

		// Read Age
		String age = null;
		if (sNo == null) {
			// Read Age
			age = split[current - 2];
			try {
				Integer.parseInt(age.trim());
			} catch (Exception e) {
				age = split[current - 3];
			}
			String sNum = split[current - 1];
			address = address.replace(age + " " + sNum, "");
			// S NO
			sNo = Integer.valueOf(sNum.trim());
			m.num = sNo;
		} else {
			age = split[current - 1];
			try {
				Integer.parseInt(age.trim());
			} catch (Exception e) {
				age = split[current - 2];
			}
			address = address.replace(age + " ", "");
			m.num = sNo;
		}
		m.hus = address.replaceAll(",", " ");

		// AGE
		m.age = Integer.valueOf(age.trim());

		out.write(m.toString());
		out.write("\n");
		return current;

	}

	private int readMembers(String[] split, int current, BufferedWriter out)
			throws IOException {

		Member m = new Member();

		int sNo = Integer.valueOf(split[current].trim());
		m.num = sNo;
		current = current + 5;

		String vId = split[current];
		m.id = vId;
		current++;

		StringBuffer buff = new StringBuffer();
		for (;; current++) {
			if (split[current].endsWith("Name:")) {
				break;
			}
			buff.append(split[current]);
			buff.append("\n");
		}

		String totalName = buff.toString();
		String[] nameSplit = totalName.split("\n");

		int nameLength = nameSplit.length;
		String name = "";
		String relative = "";
		int devider = nameLength / 2;
		boolean isOddCount = nameLength % 2 != 0;
		for (int i = 0; i < nameLength; i++) {
			String cs = nameSplit[i];
			if (i < devider) {
				name += cs + " ";
			} else if (isOddCount) {
				if (nameSplit.length == 1
						|| cs.length() < nameSplit[i + 1].length()) {
					name += cs + " ";
				} else {
					relative += cs + " ";
				}
				isOddCount = false;
			} else {
				relative += cs + " ";
			}
		}
		m.name = name;
		m.relativeName = relative;

		// Skip Relative Name
		current++;
		buff = new StringBuffer();
		// Read Address
		for (;; current++) {
			String cs = split[current];
			if (cs.equalsIgnoreCase("MALE") || cs.equalsIgnoreCase("FEMALE")) {
				break;
			}
			buff.append(split[current]);
			buff.append(" ");
		}
		String address = buff.toString();
		// Read Age
		String age = split[current - 1];
		address = address.replace(age, "");
		m.hus = address.replaceAll(",", " ");
		m.age = Integer.valueOf(age.trim());
		m.gender = String.valueOf(split[current].charAt(0)).toUpperCase();
		out.write(m.toString());
		out.write("\n");
		return current;
	}

	private boolean isElector(String[] split, int current) {
		if (split.length <= current + 1) {
			return false;
		}
		String eName = split[current + 1];
		if (!(eName.equals("Electors Name:") || eName.equals("Elector's Name:"))) {
			return false;
		}

		String sNo = split[current];
		try {
			Integer.parseInt(sNo.trim());
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	private boolean isSupplementElector(String[] split, int current) {
		if (split.length <= current + 4) {
			return false;
		}
		if (split[current].equals("Age:")
				&& split[current + 1].equals("House No:")
				&& split[current + 2].endsWith("Name:")
				&& (split[current + 3].equals("Electors Name:") || split[current + 3]
						.equals("Elector's Name:"))) {
			return true;
		}
		return false;
	}

	private static String readFile(File in) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(in));
		String line = null;
		StringBuffer buff = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			buff.append(line);
			buff.append('\n');
		}
		reader.close();
		return buff.toString();
	}
}
