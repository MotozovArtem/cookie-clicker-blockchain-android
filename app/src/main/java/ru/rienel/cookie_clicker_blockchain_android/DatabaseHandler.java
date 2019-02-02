package ru.rienel.cookie_clicker_blockchain_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper implements IDatabaseHandler {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "statistics_db";
	private static final String TABLE_STATISTICS = "statistics";
	private static final String COLUMN_ID = "id";
	private static final String COLUMN_POINTS = "points";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_STATISTICS_TABLE = "CREATE TABLE " + TABLE_STATISTICS + "("
				+ COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_POINTS + " INTEGER" + ")";
		db.execSQL(CREATE_STATISTICS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATISTICS);
		onCreate(db);
	}

	@Override
	public void addPoints(int points) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_POINTS, points);

		db.insert(TABLE_STATISTICS, null, values);
		db.close();
	}


	@Override
	public List<Integer> getAllPoints() {
		List<Integer> pointsList = new ArrayList<Integer>();
		String selectQuery = "SELECT  * FROM " + TABLE_STATISTICS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				int points = cursor.getInt(1);
				pointsList.add(points);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return pointsList;
	}


	@Override
	public void deleteAll() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_STATISTICS, null, null);
		db.close();
	}

	@Override
	public int getPointsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_STATISTICS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int pointsCount = cursor.getCount();
		cursor.close();
		db.close();
		return pointsCount;
	}
}