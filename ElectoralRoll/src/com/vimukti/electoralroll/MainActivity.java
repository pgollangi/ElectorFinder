package com.vimukti.electoralroll;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void onSearch(View btn) {
		Intent intent = new Intent(this, SearchElectoralActivity.class);
		startActivity(intent);
	}

	public void onImport(View btn) {
		Intent intent = new Intent(this, ImportActivity.class);
		startActivity(intent);
	}

	public void onClear(View v) {
		new AlertDialog.Builder(this)
				.setTitle("Clear Data")
				.setMessage("Are you sure you want to clear all data?")
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								deleteDatabase(MySQLiteHelper.DATABASE_NAME);
								Toast.makeText(getApplicationContext(),
										"Successful!!", Toast.LENGTH_LONG)
										.show();
							}
						})
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();

	}
}
