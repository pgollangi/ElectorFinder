package com.vim.csv;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

public class Converter {

	private static final int THREADS_COUNT = 8;

	public static void main(String[] args) throws Exception {
		String dir = "./";
		if (args.length > 0) {
			dir = args[0];
		}
		new Converter(dir).start();
		System.out.println("Done");
	}

	private String dir;

	public Converter(String dir) {
		this.dir = dir;
	}

	public void start() throws Exception {
		File dir = new File(this.dir);

		LinkedBlockingQueue<File> queue = new LinkedBlockingQueue<File>();

		File[] listFiles = dir.listFiles();
		for (File child : listFiles) {
			String name = child.getName();
			if (!name.toLowerCase().endsWith(".pdf")) {
				continue;
			}
			queue.add(child);
		}

		for (int i = 1; i <= THREADS_COUNT; i++) {
			ConverterThread t1 = new ConverterThread(queue, dir);
			t1.setName("Converter " + i);
			t1.start();
		}
	}
}
