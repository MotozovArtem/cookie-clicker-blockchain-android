package ru.rienel.clicker.db.factory.domain;

import android.database.Cursor;
import ru.rienel.clicker.db.domain.Block;

import java.util.Date;

public interface BlockFactory {
	Block build(Integer id, String message, Integer goal, Date creationTime,
	            String opponent, String hashOfPreviousBlock, String hashOfBlock);

	Block buildFromCursor(Cursor cursor);
	
	Block build(String message, Integer goal, Date creationTime, String opponent, 
	            String hashOfPreviousBlock, String hashOfBlock);
}
