package ru.rienel.clicker.activity.game;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import ru.rienel.clicker.R;

public class GameActivity extends AppCompatActivity {

	private static final String TAG = "GameActivity";

	private GamePresenter gamePresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_activity);

		FragmentManager fragmentManager = getSupportFragmentManager();
		GameFragment fragment = (GameFragment) fragmentManager.findFragmentById(R.id.game_container);

		if (fragment == null) {
			fragment = GameFragment.newInstance();
			fragmentManager.beginTransaction()
					.add(R.id.game_container, fragment)
					.commit();
		}

		gamePresenter = new GamePresenter(fragment);
	}
}
