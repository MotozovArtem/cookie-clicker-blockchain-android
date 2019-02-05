package ru.rienel.clicker.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import ru.rienel.clicker.db.domain.AppDbSchema.BlocksTable;

public class BlockChainBaseHelper extends SQLiteOpenHelper {
	private static final int VERSION = 1;
	private static final String DATABASE_NAME = "storage.db";

	public BlockChainBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + BlocksTable.NAME + "(  " +
				BlocksTable.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				BlocksTable.Columns.MESSAGE + " VARCHAR(255)," +
				BlocksTable.Columns.GOAL + " INTEGER," +
				BlocksTable.Columns.CREATION_TIME + " INTEGER," +
				BlocksTable.Columns.OPPONENT + " VARCHAR(100)," +
				BlocksTable.Columns.HASH_OF_PREVIOUS_BLOCK + " VARCHAR(32)," +
				BlocksTable.Columns.HASH_OF_BLOCK + " VARCHAR(32)" +
				" )");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE "+BlocksTable.NAME);
		onCreate(db);
	}
}
