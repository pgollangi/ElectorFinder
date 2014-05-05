package com.vim.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class ConverterThread extends Thread {

	private static final String ELECTOR_NAME_LABEL = "Electors Name:";
	private static final String CONVERT_COMMAND = "\"C:\\Users\\Prasanna Kumar\\GitHub\\ElectorFinder\\Csv\\pdf2htmlEX-v0.11-win32-static-with-poppler-data\\pdf2htmlEX\" ";

	private LinkedBlockingQueue<File> queue;
	private File dir;

	public ConverterThread(LinkedBlockingQueue<File> queue, File dir) {
		this.queue = queue;
		this.dir = dir;
	}

	@Override
	public void run() {
		while (!queue.isEmpty()) {
			try {
				File take = queue.take();
				convert(take);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void convert(File take) throws Exception {
		convertToCSV(take);
	}

	private void convertToCSV(File child) throws Exception {
		String fName = child.getName();
		String htmlName = fName.replace(".pdf", ".html");
		String csvName = fName.replace(".pdf", ".csv");
		if (new File(dir, csvName).exists()) {
			// Already Converted to CSV
			System.out.println("Already Converted : " + fName);
			return;
		}

		File html = new File(dir, htmlName);
		if (!html.exists()) {
			// HTML converted..
			int exitVal = convertToHtml(child);
			if (exitVal != 0) {
				// cannot convert to html
				System.out.println("Cannot convert to html: " + fName);
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
		// html.delete();
	}

	private int convertToHtml(File child) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		Process p = runtime.exec(CONVERT_COMMAND + "--dest-dir \""
				+ dir.getAbsolutePath() + "\" \"" + child.getName() + "\"",
				null, dir);
		return p.waitFor();
	}

	private void convertToCSV(String in, BufferedWriter out) throws Exception {
		in = in.replaceAll("(?s)<script[^>]*>(.*?)</script>", "");
		in = in.replaceAll("(?s)<style[^>]*>(.*?)</style>", "");
		in = in.replaceAll("<.+?>", "\n").replaceAll("&.+?;", "").replaceAll(
				"\\n+", "\n");

		String[] split = in.split("\n");

		out.write(in);

		int current = 0;
		int total = split.length;
		for (; current < total; current++) {
			if (split[current].isEmpty()) {
				continue;
			}
			if (!isElector(split, current)) {
				continue;
			}
			// Read Member
			current = readMembers(split, current, out);
		}
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
		m.hus = address;
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
		if (!eName.equals(ELECTOR_NAME_LABEL)) {
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
