package com.vim.csv;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;

public class Converter {

	private static final int THREADS_COUNT = 6;

	public static void main(String[] args) throws Exception {
		String dir = "./";
		if (args.length > 0) {
			dir = args[0];
		}
		new Converter(dir).start();
	}

	private String dir;

	public Converter(String dir) {
		this.dir = dir;
	}

	public void start() throws Exception {
		File dir = new File(this.dir);

		ArrayList<File> pdfs = new ArrayList<File>();

		File[] listFiles = dir.listFiles();
		for (File child : listFiles) {
			String name = child.getName();
			if (!name.toLowerCase().endsWith(".pdf")) {
				continue;
			}
			pdfs.add(child);
		}

		Collections.sort(pdfs);

		LinkedBlockingQueue<File> queue = new LinkedBlockingQueue<File>(pdfs);
		for (int i = 1; i <= THREADS_COUNT; i++) {
			ConverterThread t1 = new ConverterThread(queue, dir);
			t1.setName("Converter " + i);
			t1.start();
		}
	}
}
