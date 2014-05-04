package com.vimukti.electoralroll;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ElectoralDataSource {
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { Electoral.COLUMN_ID,
			Electoral.COLUMN_SERIAL, Electoral.COLUMN_NAME,
			Electoral.COLUMN_VOTER_ID, Electoral.COLUMN_RELATIVE,
			Electoral.COLUMN_HOUSE_NO, Electoral.COLUMN_AGE,
			Electoral.COLUMN_GENDER, Electoral.COLUMN_PART_NO,
			Electoral.COLUMN_CONSTITUENCY };

	public ElectoralDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open(boolean isRead) throws SQLException {
		if (isRead) {
			database = dbHelper.getWritableDatabase();
		} else {
			database = dbHelper.getReadableDatabase();
		}
	}

	public void close() {
		dbHelper.close();
	}

	public void createElectorals(List<Electoral> list) {
		for (Electoral e : list) {
			createElectoral(e);
		}
	}

	public void createElectoral(Electoral e) {
		ContentValues values = new ContentValues();
		values.put(Electoral.COLUMN_SERIAL, e.getSerial());
		values.put(Electoral.COLUMN_NAME, e.getName());
		values.put(Electoral.COLUMN_VOTER_ID, e.getVoterId());
		values.put(Electoral.COLUMN_RELATIVE, e.getRelative());
		values.put(Electoral.COLUMN_HOUSE_NO, e.getHouseNo());
		values.put(Electoral.COLUMN_AGE, e.getAge());
		values.put(Electoral.COLUMN_GENDER, e.getGender());
		values.put(Electoral.COLUMN_PART_NO, e.getPartNo());
		values.put(Electoral.COLUMN_CONSTITUENCY, e.getConstituency());

		long insertId = database.insert(Electoral.TABLE_NAME, null, values);
		Cursor cursor = database.query(Electoral.TABLE_NAME, allColumns,
				Electoral.COLUMN_ID + " = " + insertId, null, null, null, null);
		cursor.close();
	}

	public List<Electoral> getAllElectorals() {

		Cursor cursor = database.query(Electoral.TABLE_NAME, allColumns, null,
				null, null, null, null);

		return fetchResult(cursor);
	}

	private List<Electoral> fetchResult(Cursor cursor) {
		List<Electoral> electorals = new ArrayList<Electoral>();

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Electoral electoral = cursorToElectoral(cursor);
			electorals.add(electoral);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return electorals;
	}

	private Electoral cursorToElectoral(Cursor cursor) {
		Electoral electoral = new Electoral();
		electoral.setId(cursor.getLong(0));
		electoral.setSerial(cursor.getInt(1));
		electoral.setName(cursor.getString(2));
		electoral.setVoterId(cursor.getString(3));
		electoral.setRelative(cursor.getString(4));
		electoral.setHouseNo(cursor.getString(5));
		electoral.setAge(cursor.getInt(6));
		electoral.setGender(cursor.getString(7));
		electoral.setPartNo(cursor.getInt(8));
		electoral.setConstituency(cursor.getInt(9));
		return electoral;
	}

	public List<Electoral> findElectorals(String text) {
		String selectQuery = " select * from " + Electoral.TABLE_NAME
				+ " where " + Electoral.COLUMN_NAME + " like  '%" + text
				+ "%' or " + Electoral.COLUMN_RELATIVE + " like '%" + text
				+ "%' or " + Electoral.COLUMN_VOTER_ID + " like '%" + text
				+ "%' or " + Electoral.COLUMN_HOUSE_NO + " like '%" + text
				+ "%' or " + Electoral.COLUMN_PART_NO + "||'-'||"
				+ Electoral.COLUMN_SERIAL + " like '" + text + "' ORDER BY "
				+ Electoral.COLUMN_PART_NO + "," + Electoral.COLUMN_SERIAL
				+ " DESC";

		Cursor cursor = database.rawQuery(selectQuery, null);
		return fetchResult(cursor);
	}
}
