package ru.rienel.clicker.activity.main;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import ru.rienel.clicker.R;
import ru.rienel.clicker.activity.game.GameActivity;
import ru.rienel.clicker.activity.opponents.OpponentsActivity;
import ru.rienel.clicker.activity.shop.ShopActivity;
import ru.rienel.clicker.activity.statistics.StatisticsActivity;
import ru.rienel.clicker.ui.dialog.DonutDialogFragment;

public class MainActivity extends AppCompatActivity implements MainContract.View {
	private Button startGame;
	private Button statistics;
	private Button multiplayer;
	private Button shop;
	private Button clear;

	private MainContract.Presenter presenter;
	private ImageView imageDonut;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_game);

		startGame = findViewById(R.id.start);
		statistics = findViewById(R.id.statistic);
		multiplayer = findViewById(R.id.multiplayer);
		shop = findViewById(R.id.btnShop);
		clear = findViewById(R.id.btnClearGameSaves);
		imageDonut = findViewById(R.id.donut);

		startGame.setOnClickListener(buildChangeActivityOnClickListener(this, GameActivity.class));
		statistics.setOnClickListener(buildChangeActivityOnClickListener(this, StatisticsActivity.class));
		multiplayer.setOnClickListener(buildChangeActivityOnClickListener(this, OpponentsActivity.class));
		shop.setOnClickListener(buildChangeActivityOnClickListener(this, ShopActivity.class));
		clear.setOnClickListener(newOnClearGamesSavesClickListener());
		imageDonut.setOnClickListener(newOnImageDonutClickListnener());

		checkFirstLoadGameSaves();
	}

	@Override
	public void setPresenter(MainContract.Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void replaceDonut(int resourceId) {
		imageDonut.setImageResource(resourceId);
	}

	public View.OnClickListener newOnImageDonutClickListnener() {
		return (v) -> {
			DonutDialogFragment dialog = new DonutDialogFragment();
			dialog.show(getSupportFragmentManager(), "custom");
		};
	}

	private View.OnClickListener buildChangeActivityOnClickListener(final Context context,
	                                                                final Class<?> activityClass) {
		return view -> {
			Intent intent = new Intent(context, activityClass);
			startActivity(intent);
		};
	}

	private boolean checkFirstLoadGameSaves() {
		SharedPreferences saves = getSharedPreferences(getString(R.string.gameSaves), Context.MODE_PRIVATE);
		boolean hasVisited = saves.getBoolean("hasVisited", false);
		if (!hasVisited) {
			SharedPreferences.Editor editor = saves.edit();
			editor.putBoolean("hasVisited", true);
			editor.putInt("clicks", 0);
			editor.putInt("donutPerClicks", 1);            // Increase only for multiplayer clicks; defoult value is "1";
			editor.putInt("mAutoClicks", 1);            // Counter of Auto tap's (purchased for multiplayer clicks)
			editor.putInt("tempClicks", 0);                // Counter of temporary increment tap's  (purchased for comman clicks)
			editor.putInt("tempAutoClicks", 0);            // Counter of temporary Auto tap's  (purchased for comman clicks)
			editor.putInt("mClicks", 1000000000);    // Multiplayer Clicks
			editor.apply();
			return true;
		}
		return false;
	}

	private void clearGameSaves() {
		this.getSharedPreferences(getString(R.string.gameSaves), Context.MODE_PRIVATE).edit().clear().apply();
	}

	public View.OnClickListener newOnClearGamesSavesClickListener() {
		return view -> {
			clearGameSaves();
			checkFirstLoadGameSaves();
		};
	}

}
