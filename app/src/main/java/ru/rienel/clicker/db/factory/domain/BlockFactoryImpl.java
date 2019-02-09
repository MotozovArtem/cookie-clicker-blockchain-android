package ru.rienel.clicker.db.factory.domain;

import android.database.Cursor;
import ru.rienel.clicker.db.domain.AppDbSchema.BlocksTable;
import ru.rienel.clicker.db.domain.Block;
import java.util.Date;

public class BlockFactoryImpl{
	public static Block build(Integer id, String message, Integer goal,
	                   Date creationTime, String opponent, String hashOfPreviousBlock, String hashOfBlock) {
		Block block = new Block();
		block.setId(id);
		block.setMessage(message);
		block.setGoal(goal);
		block.setCreationTime(creationTime);
		block.setHashOfPreviousBlock(hashOfPreviousBlock);
		block.setHashOfBlock(hashOfBlock);
		return block;
	}

	public static Block buildFromCursor(Cursor cursor) {
		Block block = new Block();
		Integer index = null;
		index = cursor.getColumnIndex(BlocksTable.Columns.ID);
		if (index != -1) {
			block.setId(cursor.getInt(index));
		}

		index = cursor.getColumnIndex(BlocksTable.Columns.MESSAGE);
		if (index != -1) {
			block.setMessage(cursor.getString(index));
		}

		index = cursor.getColumnIndex(BlocksTable.Columns.GOAL);
		if (index != -1) {
			block.setGoal(cursor.getInt(index));
		}

		index = cursor.getColumnIndex(BlocksTable.Columns.CREATION_TIME);
		if (index != -1) {
			block.setCreationTime(
					new Date(cursor.getInt(index)));
		}

		index = cursor.getColumnIndex(BlocksTable.Columns.OPPONENT);
		if (index != -1) {
			block.setOpponent(cursor.getString(index));
		}

		index = cursor.getColumnIndex(BlocksTable.Columns.HASH_OF_PREVIOUS_BLOCK);
		if (index != -1) {
			block.setHashOfPreviousBlock(cursor.getString(index));
		}

		index = cursor.getColumnIndex(BlocksTable.Columns.HASH_OF_BLOCK);
		if (index != -1) {
			block.setHashOfBlock(cursor.getString(index));
		}
		return block;
	}

	public static Block build(String message, Integer goal, Date creationTime,
	                   String opponent, String hashOfPreviousBlock, String hashOfBlock) {
		return build(null,
				message, goal, creationTime, opponent, hashOfPreviousBlock, hashOfBlock);
	}
}
