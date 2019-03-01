package ru.rienel.clicker.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import ru.rienel.clicker.R;

public class MainActivity extends AppCompatActivity {
	private Button startGame;
	private Button statistics;
	private Button multiplayer;
	private Button shop;
	private Button clear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_game);

		startGame = findViewById(R.id.start);
		statistics = findViewById(R.id.statistic);
		multiplayer = findViewById(R.id.multiplayer);
		shop = findViewById(R.id.btnShop);
		clear = findViewById(R.id.btnClearGameSaves);

		startGame.setOnClickListener(buildChageActivityOnClickListener(this, GameActivity.class));
		statistics.setOnClickListener(buildChageActivityOnClickListener(this, StatisticsActivity.class));
		multiplayer.setOnClickListener(buildChageActivityOnClickListener(this, OpponentsActivity.class));
		shop.setOnClickListener(buildChageActivityOnClickListener(this, ShopActivity.class));
		clear.setOnClickListener(buildChageActivityOnClickListener(this,null));

		checkFirstLoadGameSaves();
	}

	private View.OnClickListener buildChageActivityOnClickListener(final Context context,
	                                                               final Class<?> activityClass) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.btnClearGameSaves) {
					clearGameSaves();
					checkFirstLoadGameSaves();
				} else {
					Intent intent = new Intent(context, activityClass);
					startActivity(intent);
				}
			}
		};
	}

	private boolean checkFirstLoadGameSaves () {
		SharedPreferences saves = getSharedPreferences(getString(R.string.gameSaves), Context.MODE_PRIVATE);
		boolean hasVisited = saves.getBoolean("hasVisited", false);
		if (!hasVisited) {
			SharedPreferences.Editor editor = saves.edit();
			editor.putBoolean("hasVisited", true);
			editor.putInt("points", 0);
			editor.putInt("DPC", 1);
			editor.apply();
			return true;
		}
		return false;
	}

	private void clearGameSaves() {
		this.getSharedPreferences(getString(R.string.gameSaves), Context.MODE_PRIVATE).edit().clear().apply();
	}
}
