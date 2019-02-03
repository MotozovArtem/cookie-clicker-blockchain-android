package ru.rienel.clicker.db.factory.domain;

import android.database.Cursor;
import ru.rienel.clicker.db.domain.Block;

import java.util.Date;

public interface BlockFactory {
	Block build(Integer id, String message, Integer goal, Date creationTime,
	            String hashOfPreviousBlock, String hashOfBlock);

	Block buildFromCursor(Cursor cursor);
}
