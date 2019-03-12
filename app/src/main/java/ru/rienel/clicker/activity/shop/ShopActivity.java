package ru.rienel.clicker.activity.shop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ru.rienel.clicker.R;
import ru.rienel.clicker.activity.game.GameActivity;

public class ShopActivity extends AppCompatActivity implements View.OnClickListener {

	private int donutPerTap;
	private int points;
	private int mPoints;
	private long currentTime;
	private boolean flagShop = false;

	private Button temporaryTap;
	private Button temporaryAutoTap;
	private Button plus5Donut;
	private TextView textViewMPoints;
	private TextView textViewPoints;
	private TextView textViewDPC;
	private SharedPreferences saves;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_activity);

		temporaryTap = findViewById(R.id.btn_2);
		temporaryAutoTap = findViewById(R.id.btn_4);
		plus5Donut = findViewById(R.id.btn_5);
		temporaryTap.setEnabled(false);
		temporaryAutoTap.setEnabled(false);
		plus5Donut.setEnabled(false);

		loadGameSaves();
		textViewPoints = findViewById(R.id.pointsTextView);
		textViewMPoints = findViewById(R.id.mPointsTextView);
		textViewDPC = findViewById(R.id.donutPerClickTextView);
		updateTextView();


		currentTime = getIntent().getLongExtra("currentTime", 60000);
		checkingPoints();

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_2) {
			int tempTap = saves.getInt("tempTap", 0);
			saves.edit().putInt("tempTap", tempTap + 1).apply();
			updatePoints(10);
			updateTextView();
			checkingPoints();
		} else if (v.getId() == R.id.btn_4) {
			int tempAutoTap = saves.getInt("tempAutoTap", 0);
			saves.edit().putInt("tempAutoTap", tempAutoTap + 1).apply();
			updatePoints(20);
			updateTextView();
			checkingPoints();
		} else if (v.getId() == 0) {  // TODO chahge "0" on multiplayer tap button from XML
			updateDonutPerTap(1,100);
		} else if (v.getId() == 0) { // TODO change "0" on multiplaer auto tap button from XML
			int multiplayerAutoTap = saves.getInt("mAutoTap",0);
			saves.edit().putInt("mAutoTap", multiplayerAutoTap + 1).apply();
			updateMPoints(300);
		} else if (v.getId() == R.id.btnBack) {
//        	startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showDonutFragment();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean updateDps(int donutPerTap, int points) {
		this.donutPerTap = donutPerTap;
		updatePoints(points);
		return true;
	}

	private void updatePoints(int points) {
		this.points -= points;
	}

	private void showDonutFragment() {
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra("dpc", donutPerTap);
		intent.putExtra("points", points);
		intent.putExtra("flagShop", flagShop);
		intent.putExtra("currentTime", currentTime);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("dpc_state", donutPerTap);
		outState.putInt("points_state", points);
		outState.putBoolean("flagShop_state", flagShop);
		outState.putLong("currentTime_state", currentTime);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		donutPerTap = savedInstanceState.getInt("dpc_state");
		points = savedInstanceState.getInt("points_state");
		flagShop = savedInstanceState.getBoolean("flagShop_state");
		currentTime = savedInstanceState.getLong("currentTime_state");
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadGameSaves();
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveGameSaves(this.points, this.donutPerTap, this.mPoints);
	}

	private boolean updateDonutPerTap(int donutPerTap, int mPoints) {
		this.donutPerTap -=  donutPerTap;
		updateMPoints(mPoints);
		updateTextView();
		return true;
	}


	private void updateMPoints (int mPoints) {
		this.mPoints -= mPoints;
	}

	private void checkingPoints() {
		if (points >= 10) {
			temporaryTap.setEnabled(true);
			if (points >= 20) {
				temporaryAutoTap.setEnabled(true);
				if (points >= 30) {
					plus5Donut.setEnabled(false);
				} else {
					plus5Donut.setEnabled(false);
				}
			} else {
				temporaryAutoTap.setEnabled(false);
				plus5Donut.setEnabled(false);
			}
		} else {
			temporaryTap.setEnabled(false);
			temporaryAutoTap.setEnabled(false);
			plus5Donut.setEnabled(false);
		}
	}

	private void saveGameSaves(int points, int dpc, int mPoints) {
		saves = getSharedPreferences(getString(R.string.gameSaves), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = saves.edit();
		editor.putInt("points", points);
		editor.putInt("donutPerTap", dpc);
		editor.putInt("mPoints", mPoints);
		editor.apply();
	}

	private void loadGameSaves () {
		saves = getSharedPreferences(getString(R.string.gameSaves), Context.MODE_PRIVATE);
			this.donutPerTap = saves.getInt("donutPerTap",0);
			this.points = saves.getInt("points", 0);
			this.mPoints = saves.getInt("mPoints",0);

	}

	private void updateTextView() {
		textViewPoints.setText(String.valueOf(getResources().getString(R.string.points) + this.points));
		textViewDPC.setText(String.valueOf(getResources().getString(R.string.DPC) + this.donutPerTap));
		textViewMPoints.setText(String.valueOf(getResources().getString(R.string.multiplayer_points) + this.mPoints));
	}

}

