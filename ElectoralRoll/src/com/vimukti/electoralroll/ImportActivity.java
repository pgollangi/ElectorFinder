package com.vimukti.electoralroll;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVReader;

public class ImportActivity extends Activity {

	private final static String SERVER_URL = "http://blog.ecgine.com/loksatta/%03d%03d.csv";

	private EditText cNum;
	private EditText from;
	private EditText to;

	ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import);

		this.cNum = (EditText) findViewById(R.id.c_num);
		this.from = (EditText) findViewById(R.id.from);
		this.to = (EditText) findViewById(R.id.to);
	}

	public void onImport(View v) {

		// instantiate it within the onCreate method
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Importing...");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(false);

		String c = cNum.getText().toString();
		String from = this.from.getText().toString();
		String to = this.to.getText().toString();

		Integer ci = Integer.valueOf(c);
		Integer fromi = Integer.valueOf(from);
		Integer toi = Integer.valueOf(to);

		if (ci <= 0) {
			Toast.makeText(this, "Invalid Constituency", Toast.LENGTH_LONG)
					.show();
			return;
		}

		if (toi <= 0 || fromi <= 0) {
			Toast.makeText(this, "Invalid Part No", Toast.LENGTH_LONG).show();
			return;
		}

		if (toi < fromi) {
			Toast.makeText(this, "Invalid Part No", Toast.LENGTH_LONG).show();
			return;
		}

		mProgressDialog.show();
		DownloadTask download = new DownloadTask(this);

		download.execute(c, from, to);
	}

	private class DownloadTask extends AsyncTask<String, Integer, String> {

		private Context context;
		private PowerManager.WakeLock mWakeLock;

		private int currentPart = 0;
		private int constituency = 0;

		public DownloadTask(Context context) {
			this.context = context;
		}

		@Override
		protected String doInBackground(String... sUrl) {
			this.constituency = Integer.valueOf(sUrl[0]);
			int from = Integer.valueOf(sUrl[1]);
			int to = Integer.valueOf(sUrl[2]);

			ElectoralDataSource ds = new ElectoralDataSource(context);
			ds.open(false);
			try {
				for (currentPart = from; currentPart <= to; currentPart++) {
					CSVReader reader = null;
					try {
						publishProgress(1, currentPart);
						String strUrl = String.format(SERVER_URL, constituency,
								currentPart);
						URL url = new URL(strUrl);
						reader = new CSVReader(new InputStreamReader(
								url.openStream()));

						String[] row = null;
						while ((row = reader.readNext()) != null) {
							try {
								Electoral e = prepareElectoral(row);
								ds.createElectoral(e);
							} catch (Exception e) {
								// Just Continue to next record
							}
						}
					} catch (Exception e) {
						publishProgress(-1);
					} finally {
						try {
							if (reader != null) {
								reader.close();
							}
						} catch (IOException e) {
						}
					}
				}
			} finally {
				ds.close();
				publishProgress(0);
			}

			return null;
		}

		private void importComplete() {
			mProgressDialog.hide();
			Intent search = new Intent(context, SearchElectoralActivity.class);
			startActivity(search);
		}

		private Electoral prepareElectoral(String[] str) {
			Electoral e = new Electoral();
			e.setSerial(Integer.valueOf(str[0]));
			e.setVoterId(str[1]);
			e.setName(str[2]);
			e.setRelative(str[3]);
			e.setHouseNo(str[4]);
			e.setAge(Integer.valueOf(str[5]));
			e.setGender(str[6]);
			e.setPartNo(currentPart);
			e.setConstituency(constituency);
			return e;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {

			Integer status = values[0];
			if (status < 0) {
				// Error
				Toast.makeText(context, "Error while importing " + currentPart,
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (status == 0) {
				importComplete();
				return;
			}

			Integer current = values[1];
			mProgressDialog.setMessage("Importing PartNo " + current + " ...");

		}

	}
}
