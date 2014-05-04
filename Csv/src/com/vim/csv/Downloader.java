package com.vim.csv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Downloader {

	// static String url =
	// "http://ceoaperms.ap.gov.in/Electoral_Rolls/PDFGeneration.aspx?urlPath=D:\\FinalRolls_2014\\AC_046\\English\\AC046_FIN_E_017.PDF";

	public static void main(String[] args) throws IOException {
		StringBuilder str = new StringBuilder();
		// str.append("http://ceoaperms.ap.gov.in/Electoral_Rolls/PDFGeneration.aspx?urlPath=D:\\FinalRolls_2014\\AC_");
		// str.append("046");
		// str.append("\\English\\AC");
		// str.append("046");
		// str.append("_FIN_E_");
		// str.append("017");
		// str.append(".PDF");
		URL website = new URL(
				"http://ceoaperms.ap.gov.in/Electoral_Rolls/PDFGeneration.aspx?urlPath=D:\\FinalRolls_2014\\AC_187\\English\\AC187_FIN_E_145.PDF");
		for (int i = 1; i < 295; i++) {
			int j = 1;
			while (true) {
				// StringBuilder str = new StringBuilder();
				// str.append("http://ceoaperms.ap.gov.in/Electoral_Rolls/PDFGeneration.aspx?urlPath=D:\\FinalRolls_2014\\AC_");
				// str.append(getFormattedString(i));
				// str.append("\\English\\AC");
				// str.append(getFormattedString(i));
				// str.append("_FIN_E_");
				// str.append(getFormattedString(j));
				// str.append(".PDF");
				// System.out.println(str.toString());
				try {
					// URL website = new URL(str.toString());
					ReadableByteChannel rbc = Channels.newChannel(website
							.openStream());
					File file = new File(getFormattedString(i)
							+ getFormattedString(j) + ".pdf");
					if (!file.exists()) {
						file.createNewFile();
					}
					FileOutputStream fos = new FileOutputStream(file);
					long transferFrom = fos.getChannel().transferFrom(rbc, 0,
							Long.MAX_VALUE);
					fos.close();
					if (transferFrom < 5 * 1024) {
						file.delete();
						break;
					}
					j++;
				} catch (Exception e) {
					// e.printStackTrace();
					break;
				}
			}
		}

		// System.out.println(str.toString());

	}

	private static String getFormattedString(int i) {
		return String.format("%03d", i);
	}
}
