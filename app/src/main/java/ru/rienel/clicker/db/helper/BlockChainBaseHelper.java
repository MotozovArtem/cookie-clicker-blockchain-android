package ru.rienel.clicker.db.helper;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.rienel.clicker.db.AppDataBaseSchema.BlocksTable;
import ru.rienel.clicker.db.domain.Block;
import ru.rienel.clicker.db.factory.domain.BlockFactory;

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

		ContentValues contentValues = createContentValuesForGenesisBlock(db);
		db.insert(BlocksTable.NAME, null, contentValues);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE " + BlocksTable.NAME);
		onCreate(db);
	}

	private ContentValues createContentValuesForGenesisBlock(SQLiteDatabase db) {
		Block genesisBlock = new BlockFactory().build("Genesis block",
				0, new Date(System.currentTimeMillis() - 1000),
				null, null, "GENESIS");
		ContentValues contentValues = new ContentValues();
		contentValues.put(BlocksTable.Columns.ID, genesisBlock.getId());
		contentValues.put(BlocksTable.Columns.MESSAGE, genesisBlock.getMessage());
		contentValues.put(BlocksTable.Columns.GOAL, genesisBlock.getGoal());
		contentValues.put(BlocksTable.Columns.CREATION_TIME, genesisBlock.getCreationTime().getTime());
		contentValues.put(BlocksTable.Columns.OPPONENT, genesisBlock.getHashOfPreviousBlock());
		contentValues.put(BlocksTable.Columns.HASH_OF_PREVIOUS_BLOCK, genesisBlock.getHashOfPreviousBlock());
		contentValues.put(BlocksTable.Columns.HASH_OF_BLOCK, genesisBlock.getHashOfBlock());
		return contentValues;
	}


}
