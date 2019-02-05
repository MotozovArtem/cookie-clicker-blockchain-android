package ru.rienel.clicker.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import org.junit.Before;
import org.junit.Test;
import ru.rienel.clicker.db.domain.Block;
import ru.rienel.clicker.db.domain.dao.DaoException;
import ru.rienel.clicker.db.domain.dao.Repository;
import ru.rienel.clicker.db.domain.dao.impl.BlockDaoImpl;
import ru.rienel.clicker.db.helper.BlockChainBaseHelper;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class BlockDaoTest {
	@Before
	public void createDataBase(){
		Context appContext = InstrumentationRegistry.getContext();
		new BlockChainBaseHelper(appContext);
	}

	@Test
	public void testAddFullBlock(){
		// Environment init
		Context appContext = InstrumentationRegistry.getContext();
		Repository<Block> blockDao = new BlockDaoImpl(appContext);

		// Init block
		Block block = new Block();
		block.setId(0);
		block.setMessage("Test");
		block.setCreationTime(new Date(System.currentTimeMillis()));
		block.setHashOfBlock("hash");
		block.setHashOfPreviousBlock("prevHash");

		//Test block
		try {
			blockDao.add(block);
		} catch (DaoException e) {
			e.printStackTrace();
		}

		Block findBlock = null;
		try {
			findBlock = blockDao.findById(0);
		} catch (DaoException e) {
			e.printStackTrace();
		}
		assertNotNull(findBlock);
	}
}
