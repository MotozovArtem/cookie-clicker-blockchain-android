package ru.rienel.clicker.db.domain.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ru.rienel.clicker.db.domain.AppDataBaseSchema.BlocksTable;
import ru.rienel.clicker.db.domain.Block;
import ru.rienel.clicker.db.domain.dao.DaoException;
import ru.rienel.clicker.db.domain.dao.Repository;
import ru.rienel.clicker.db.factory.domain.BlockFactory;
import ru.rienel.clicker.db.helper.BlockChainBaseHelper;

public class BlockDaoImpl implements Repository<Block> {
	private BlockChainBaseHelper dbHelper;

	public BlockDaoImpl(Context context) {
		dbHelper = new BlockChainBaseHelper(context);
	}

	private ContentValues getValues(Block model) {
		ContentValues values = new ContentValues();
		values.put(BlocksTable.Columns.ID, model.getId());
		values.put(BlocksTable.Columns.MESSAGE, model.getMessage());
		values.put(BlocksTable.Columns.GOAL, model.getGoal());
		values.put(BlocksTable.Columns.CREATION_TIME, model.getCreationTime().getTime());
		values.put(BlocksTable.Columns.HASH_OF_PREVIOUS_BLOCK, model.getHashOfPreviousBlock());
		values.put(BlocksTable.Columns.HASH_OF_BLOCK, model.getHashOfBlock());
		return values;
	}

	@Override
	public void add(Block model) throws DaoException {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = getValues(model);
		long rowId = -1;
		db.beginTransaction();
		rowId = db.insert(BlocksTable.NAME, null, values);
		if (rowId == -1) {
			db.endTransaction();
			throw new DaoException("Block insert error");
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	@Override
	public Block findById(Integer id) throws DaoException {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(BlocksTable.NAME, BlocksTable.allColumns(),
				String.format("%s=?", BlocksTable.Columns.ID), new String[]{id.toString()},
				null,
				null,
				String.format("%s ASC", BlocksTable.Columns.CREATION_TIME));
		cursor.moveToFirst();
		int rowsCount = cursor.getCount();
		if (rowsCount == 0) {
			cursor.close();
			throw new DaoException(String.format("Block with this id: %d not found!", id));
		}

		Block block;
		block = BlockFactory.buildFromCursor(cursor);
		cursor.close();
		return block;
	}

	@Override
	public List<Block> findAll() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(BlocksTable.NAME, BlocksTable.allColumns(),
				null, null, null, null, BlocksTable.Columns.CREATION_TIME);
		List<Block> blocks = new ArrayList<>();
		cursor.moveToFirst();
		int rowsCount = cursor.getCount();
		if (rowsCount == 0 || rowsCount == -1) {
			cursor.close();
			return Collections.emptyList();
		}
		do {
			blocks.add(BlockFactory.buildFromCursor(cursor));
		} while (cursor.moveToNext());
		cursor.close();
		return blocks;
	}

	@Override
	public void deleteById(Integer id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int affectedRows =
				db.delete(BlocksTable.NAME, String.format("%s=?", BlocksTable.Columns.ID),
						new String[]{id.toString()});
//		if (affectedRows == 0) {
//			;
//		}
	}

	@Override
	public void delete(Block model) throws DaoException {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int affectedRows =
				db.delete(BlocksTable.NAME, String.format("%s=?", BlocksTable.Columns.ID),
						new String[]{model.getId().toString()});
	}

	@Override
	public void update(Block model) throws DaoException {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int affectedRows = db.update(BlocksTable.NAME, getValues(model), String.format("%s=?", BlocksTable.Columns.ID),
				new String[]{model.getId().toString()});
	}

	@Override
	public int count() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT count(id) FROM " + BlocksTable.NAME, null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return count;
	}
}
