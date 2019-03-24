package ru.rienel.clicker.activity.game;

import java.net.InetAddress;
import java.util.Date;

import android.util.Log;

import ru.rienel.clicker.common.Preconditions;
import ru.rienel.clicker.common.SignalHandler;
import ru.rienel.clicker.db.domain.Block;
import ru.rienel.clicker.db.domain.dao.DaoException;
import ru.rienel.clicker.db.domain.dao.Repository;
import ru.rienel.clicker.db.factory.domain.BlockFactory;


public class GamePresenter implements GameContract.Presenter {
	private static final String TAG = GamePresenter.class.getName();
	private final Integer DONUT_PER_CLICK;

	private GameContract.View gameView;
	private Integer clicks;
	private Repository<Block> blockRepository;
	private SignalHandler signalHandler;
	private InetAddress opponentAddress;


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
		Block newBlock = BlockFactory.build(message, goal,
				new Date(System.currentTimeMillis()), "None",
				"none", "HASH");
		try {
			blockRepository.add(newBlock);
		} catch (DaoException e) {
			Log.i(TAG, "Failed add new block", e);
		}
	}

	public GamePresenter(GameContract.View gameView, GameType gameType, InetAddress address) {
		Preconditions.checkNotNull(gameView);
		Preconditions.checkNotNull(gameType);

		this.gameView = gameView;
		this.clicks = 0; // fixme: bug warning
		DONUT_PER_CLICK = 1;

		if (GameType.MULTIPLAYER == gameType) {
			signalHandler = new SignalHandler();
		}
		gameView.setPresenter(this);
	}
}
