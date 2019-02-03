package ru.rienel.clicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class StartGameActivity extends AppCompatActivity implements View.OnClickListener {
	Button btnStartGame;
	Button btnViewStatistic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_game);

		btnStartGame = findViewById(R.id.btnStart);
		btnViewStatistic = findViewById(R.id.btnStatistic);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnStart) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);

		} else if (v.getId() == R.id.btnStatistic) {
			Intent intent = new Intent(this, StatisticsActivity.class);
			startActivity(intent);
		}
	}


}
