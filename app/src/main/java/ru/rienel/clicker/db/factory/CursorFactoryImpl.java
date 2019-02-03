package ru.rienel.clicker.db.factory;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;

public class CursorFactoryImpl implements SQLiteDatabase.CursorFactory {
	@Override
	public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
		return null;
	}
}
