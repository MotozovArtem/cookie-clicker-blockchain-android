package ru.rienel.clicker.db.factory.domain.impl;

import android.database.Cursor;
import ru.rienel.clicker.db.domain.AppDbSchema.BlocksTable;
import ru.rienel.clicker.db.domain.Block;
import ru.rienel.clicker.db.factory.domain.BlockFactory;

import java.util.Date;

public class BlockFactoryImpl implements BlockFactory {
	@Override
	public Block build(Integer id, String message, Integer goal,
	                   Date creationTime, String hashOfPreviousBlock, String hashOfBlock) {
		Block block = new Block();
		block.setId(id);
		block.setMessage(message);
		block.setGoal(goal);
		block.setCreationTime(creationTime);
		block.setHashOfPreviousBlock(hashOfPreviousBlock);
		block.setHashOfBlock(hashOfBlock);
		return block;
	}

	@Override
	public Block buildFromCursor(Cursor cursor) {
		Block block = new Block();
		block.setId(cursor.getInt(cursor.getColumnIndex(BlocksTable.Columns.ID)));
		block.setMessage(cursor.getString(cursor.getColumnIndex(BlocksTable.Columns.MESSAGE)));
		block.setGoal(cursor.getInt(cursor.getColumnIndex(BlocksTable.Columns.GOAL)));
		block.setCreationTime(
				new Date(
						cursor.getInt(cursor.getColumnIndex(BlocksTable.Columns.CREATION_TIME))
				)
		);
		block.setHashOfPreviousBlock(cursor.getString(cursor.getColumnIndex(BlocksTable.Columns.HASH_OF_PREVIOUS_BLOCK)));
		block.setHashOfBlock(cursor.getString(cursor.getColumnIndex(BlocksTable.Columns.HASH_OF_BLOCK)));
		return block;
	}
}
