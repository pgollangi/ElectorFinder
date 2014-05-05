package com.vim.csv;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

public class Converter {

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
			if (!name.endsWith(".pdf")) {
				continue;
			}
			queue.add(child);
		}

		ConverterThread t1 = new ConverterThread(queue, dir);
		t1.start();
	}

}
