package ru.rienel.clicker.activity.game;

import java.net.InetAddress;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import ru.rienel.clicker.R;
import ru.rienel.clicker.net.Server;

public class GameActivity extends AppCompatActivity {
	private static final String TAG = "GameActivity";

	public static final String INTENT_ADDRESS = "address";
	public static final String INTENT_GAME_TYPE = "gameType";

	private GamePresenter gamePresenter;
	private Server server;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_activity);

		FragmentManager fragmentManager = getSupportFragmentManager();
		GameFragment fragment = (GameFragment)fragmentManager.findFragmentById(R.id.game_container);

		if (fragment == null) {
			fragment = GameFragment.newInstance();
			fragmentManager.beginTransaction()
					.add(R.id.game_container, fragment)
					.commit();
		}
		Intent intent = getIntent();


		GameType gameType = null;
		InetAddress address = null;

		if (intent != null) {
			gameType = (GameType)intent.getSerializableExtra(INTENT_GAME_TYPE);
			address = (InetAddress)intent.getSerializableExtra(INTENT_ADDRESS);
		}
		if (gameType == null) {
			gameType = GameType.SINGLEPLAYER;
		}

		server = Server.getInstance();
// TODO: Get client from intent cause user came from OpponentsActivity for multiplayer functionality
		//Client client = new Client();

		gamePresenter = new GamePresenter(fragment, gameType, address, server, null);
	}
}
