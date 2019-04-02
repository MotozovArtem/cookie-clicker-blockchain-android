package ru.rienel.clicker.activity.game;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

import ru.rienel.clicker.common.Preconditions;
import ru.rienel.clicker.db.domain.Block;
import ru.rienel.clicker.db.domain.dao.DaoException;
import ru.rienel.clicker.db.domain.dao.Repository;
import ru.rienel.clicker.db.domain.dao.impl.BlockDaoImpl;
import ru.rienel.clicker.db.factory.domain.BlockFactory;
import ru.rienel.clicker.net.Client;
import ru.rienel.clicker.net.Client.ClientConnectionEvent;
import ru.rienel.clicker.net.Client.ClientConnectionListener;
import ru.rienel.clicker.net.Server;


public class GamePresenter implements GameContract.Presenter {
	private static final String TAG = GamePresenter.class.getName();
	private final Integer DONUT_PER_CLICK;

	private GameContract.View gameView;
	private Integer clicks;
	private Repository<Block> blockRepository;
	private InetAddress opponentAddress;

	private Server server;
	private Client client;
	private ExecutorService executorService = Executors.newFixedThreadPool(2);

	public GamePresenter(GameContract.View gameView, GameType gameType, InetAddress address, Server server, Client client) {
		Preconditions.checkNotNull(gameView);
		Preconditions.checkNotNull(gameType);

		this.gameView = gameView;
		this.clicks = 0; // fixme: bug warning
		DONUT_PER_CLICK = 1;

		this.server = server;
		//this.client = client; //TODO: Get client from intent, cause client should came from OpponentsActivity
		//this.client.addListener(new ConnectionListener());

		gameView.setPresenter(this);
	}

	@Override
	public void start() {
	}

	@Override
	public void startGame() {
	}

	@Override
	public void sendSignalToOpponent() {

	}

	@Override
	public void handleClick() {
		this.clicks += DONUT_PER_CLICK;
		gameView.setClicks(clicks);
		gameView.setNewClick(DONUT_PER_CLICK);
	}

	@Override
	public void finishGame(String message, Integer goal) {
		blockRepository = new BlockDaoImpl(gameView.getActivityContext());
		List<Block> blockList = blockRepository.findAll();
		Block newBlock = BlockFactory.build(message, goal,
				new Date(System.currentTimeMillis()), "None",
				blockList.get(blockList.size()-1).getHashOfBlock(), "HASH");
		try {
			blockRepository.add(newBlock);
		} catch (DaoException e) {
			Log.i(TAG, "Failed add new block", e);
		}
	}

	public class ConnectionListener implements ClientConnectionListener {

		@Override
		public void connected(ClientConnectionEvent event) {
			// do nothing,
		}

		@Override
		public void disconnected(ClientConnectionEvent event) {

		}

		@Override
		public void receivedSignal(ClientConnectionEvent event) {

		}
	}
}
