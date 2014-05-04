package com.vimukti.electoralroll;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "electorals.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ Electoral.TABLE_NAME + "(" + Electoral.COLUMN_ID
			+ " INTEGER primary key autoincrement, " + Electoral.COLUMN_SERIAL
			+ " INTEGER, " + Electoral.COLUMN_NAME + " text, "
			+ Electoral.COLUMN_VOTER_ID + " text, " + Electoral.COLUMN_RELATIVE
			+ " text, " + Electoral.COLUMN_HOUSE_NO + " text, "
			+ Electoral.COLUMN_AGE + " INTEGER, " + Electoral.COLUMN_GENDER
			+ " text, " + Electoral.COLUMN_PART_NO + " INTEGER, "
			+ Electoral.COLUMN_CONSTITUENCY + " INTEGER " + ");";

	private static final String CREATE_NAME_INDEX = "CREATE INDEX name_idx ON "
			+ Electoral.TABLE_NAME + " (" + Electoral.COLUMN_NAME + ")";
	private static final String CREATE_RELATIVE_INDEX = "CREATE INDEX relative_to_idx ON "
			+ Electoral.TABLE_NAME + " (" + Electoral.COLUMN_RELATIVE + ")";
	private static final String CREATE_VOTER_ID_INDEX = "CREATE INDEX voter_id_idx ON "
			+ Electoral.TABLE_NAME + " (" + Electoral.COLUMN_VOTER_ID + ")";
	private static final String CREATE_H_NO_INDEX = "CREATE INDEX h_no_idx ON "
			+ Electoral.TABLE_NAME + " (" + Electoral.COLUMN_HOUSE_NO + ")";
	private static final String CREATE_PART_NO_INDEX = "CREATE INDEX part_no_idx ON "
			+ Electoral.TABLE_NAME + " (" + Electoral.COLUMN_PART_NO + ")";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		database.execSQL(CREATE_NAME_INDEX);
		database.execSQL(CREATE_RELATIVE_INDEX);
		database.execSQL(CREATE_VOTER_ID_INDEX);
		database.execSQL(CREATE_H_NO_INDEX);
		database.execSQL(CREATE_PART_NO_INDEX);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == newVersion) {
			return;
		}
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + Electoral.TABLE_NAME);
		onCreate(db);
	}
}
