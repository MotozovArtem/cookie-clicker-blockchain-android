package ru.rienel.clicker.activity.main;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;


import ru.rienel.clicker.R;
import ru.rienel.clicker.activity.game.GameActivity;
import ru.rienel.clicker.activity.opponents.OpponentsActivity;
import ru.rienel.clicker.activity.statistics.StatisticsActivity;
import ru.rienel.clicker.common.DialogDonut;

public class MainActivity extends AppCompatActivity implements MainContract.View {
	private Button startGame;
	private Button statistics;
	private Button multiplayer;
	private ImageView imageDonut;

	private MainContract.Presenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_game);

		startGame = findViewById(R.id.start);
		statistics = findViewById(R.id.statistic);
		multiplayer = findViewById(R.id.multiplayer);
		imageDonut = findViewById(R.id.donut);

		startGame.setOnClickListener(buildChageActivityOnClickListener(this, GameActivity.class));
		statistics.setOnClickListener(buildChageActivityOnClickListener(this, StatisticsActivity.class));
		multiplayer.setOnClickListener(buildChageActivityOnClickListener(this, OpponentsActivity.class));
		imageDonut.setOnClickListener(showDialog());
	}


	public View.OnClickListener showDialog() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogDonut dialog = new DialogDonut();
				dialog.show(getSupportFragmentManager(), "custom");
			}
		};

	}

	private View.OnClickListener buildChageActivityOnClickListener(final Context context,
	                                                               final Class<?> activityClass) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, activityClass);
				startActivity(intent);
			}
		};
	}


	@Override
	public void setPresenter(MainContract.Presenter presenter) {
		this.presenter = presenter;
	}

}
