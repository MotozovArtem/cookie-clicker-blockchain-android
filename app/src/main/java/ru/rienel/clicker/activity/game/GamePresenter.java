package ru.rienel.clicker.activity.game;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import android.util.Log;

import ru.rienel.clicker.common.Preconditions;
import ru.rienel.clicker.common.SignalHandler;
import ru.rienel.clicker.db.domain.Block;
import ru.rienel.clicker.db.domain.dao.DaoException;
import ru.rienel.clicker.db.domain.dao.Repository;
import ru.rienel.clicker.db.factory.domain.BlockFactory;
import ru.rienel.clicker.net.Client;
import ru.rienel.clicker.net.SendReceive;
import ru.rienel.clicker.net.Server;
import ru.rienel.clicker.net.factory.ClientThreadFactory;
import ru.rienel.clicker.net.factory.SendReceiveThreadFactory;
import ru.rienel.clicker.net.factory.ServerThreadFactory;


public class GamePresenter implements GameContract.Presenter {
	private static final String TAG = GamePresenter.class.getName();
	private final Integer DONUT_PER_CLICK;

	private GameContract.View gameView;
	private Integer clicks;
	private Repository<Block> blockRepository;
	private SignalHandler signalHandler;
	private InetAddress opponentAddress;

	private ClientThreadFactory clientFactory = new ClientThreadFactory();
	private ServerThreadFactory serverFactory = new ServerThreadFactory();
	private SendReceiveThreadFactory sendReceiveFactory = new SendReceiveThreadFactory();

	private Client client;
	private Server server;
	private SendReceive sendReceive;

	private Thread clientThread;
	private Thread serverThread;
	private Thread sendReceiveThread;

	public GamePresenter(GameContract.View gameView, GameType gameType, InetAddress address) {
		Preconditions.checkNotNull(gameView);
		Preconditions.checkNotNull(gameType);

		this.gameView = gameView;
		this.clicks = 0; // fixme: bug warning
		DONUT_PER_CLICK = 1;

		if (GameType.MULTIPLAYER == gameType) {
			signalHandler = new SignalHandler();

			if (opponentAddress != null) {
				clientThread = clientFactory.newThread(Client.newInstanceTo(opponentAddress));
				try {
					serverThread = serverFactory.newThread(new Server(signalHandler));
				} catch (IOException e) {
					Log.e(TAG, "GamePresenter(Multiplayer)", e);
				}
				sendReceiveThread = sendReceiveFactory.newThread(new SendReceive(client.getClientSocket(), signalHandler));
			}
		}
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
		gameView.setPoints(clicks);
		gameView.setNewClick(DONUT_PER_CLICK);
	}

	@Override
	public void finishGame(String message, Integer goal) {
		Block newBlock = BlockFactory.build(message, goal,
				new Date(System.currentTimeMillis()), "None",
				"none", "HASH");
		try {
			blockRepository.add(newBlock);
		} catch (DaoException e) {
			Log.i(TAG, "Failed add new block", e);
		}
	}
}
