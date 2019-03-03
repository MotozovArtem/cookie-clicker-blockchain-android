package ru.rienel.clicker.activity;

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

public class ShopActivity extends AppCompatActivity implements View.OnClickListener {

	private int donutPerTap;
	private int points;
	private long currentTime;
	private boolean flagShop = false;

	private Button plus2Donut;
	private Button plus4Donut;
	private Button plus5Donut;
	private TextView textViewPoints;
	private TextView textViewDPC;
	private SharedPreferences saves;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_activity);

		plus2Donut = findViewById(R.id.btn_2);
		plus4Donut = findViewById(R.id.btn_4);
		plus5Donut = findViewById(R.id.btn_5);
		plus2Donut.setEnabled(false);
		plus4Donut.setEnabled(false);
		plus5Donut.setEnabled(false);

		loadGameSaves();
		textViewPoints = findViewById(R.id.textView1);
		textViewDPC = findViewById(R.id.textView2);
		textViewPoints.setText(String.valueOf(getResources().getString(R.string.points) + this.points));
		textViewDPC.setText(String.valueOf(getResources().getString(R.string.DPC) + this.donutPerTap));

		currentTime = getIntent().getLongExtra("currentTime", 60000);
		checkingPoints();




	}

	@Override
	public void onClick(View v) {
        if (v.getId() == R.id.btn_2) {
            int tempTap = saves.getInt("tempTap",0);
            saves.edit().putInt("tempTap", tempTap + 1).apply();
            updatePoints(10);
            updateTextView();
            checkingPoints();
        } else if (v.getId() == R.id.btn_4) {
            int tempAutoTap = saves.getInt("tempAutoTap",0);
            saves.edit().putInt("tempAutoTap", tempAutoTap + 1).apply();
            updatePoints(20);
            updateTextView();
            checkingPoints();
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

//	private boolean updateDps(int donutPerTap, int points) {
//		this.donutPerTap = this.donutPerTap + donutPerTap;
//		updatePoints(points);
//		updateTextView();
//		return true;
//	}

	private void updatePoints(int points) {
		this.points -= points;
	}

	private void showDonutFragment() {
		Intent intent = new Intent(this, GameActivity.class);
		setResult(RESULT_OK, intent);
		finish();
	}

	private void checkingPoints() {
		if (points >= 10) {
			plus2Donut.setEnabled(true);
			if (points >= 20) {
				plus4Donut.setEnabled(true);
				if (points >= 30) {
					plus5Donut.setEnabled(false);
				} else {
					plus5Donut.setEnabled(false);
				}
			} else {
				plus4Donut.setEnabled(false);
				plus5Donut.setEnabled(false);
			}
		} else {
			plus2Donut.setEnabled(false);
			plus4Donut.setEnabled(false);
			plus5Donut.setEnabled(false);
		}
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

	private void saveGameSaves(int points, int dpc) {
		saves = getSharedPreferences(getString(R.string.gameSaves), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = saves.edit();
		editor.putInt("points", points);
		editor.putInt("donutPerTap", dpc);
		editor.apply();
	}

	private void loadGameSaves () {
		saves = getSharedPreferences(getString(R.string.gameSaves), Context.MODE_PRIVATE);
			this.donutPerTap = saves.getInt("donutPerTap",0);
			this.points = saves.getInt("points", 0);

	}

	@Override
	protected void onResume() {
		super.onResume();
		loadGameSaves();
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveGameSaves(this.points, this.donutPerTap);
	}

	private void updateTextView() {
		textViewPoints.setText(String.valueOf(getResources().getString(R.string.points) + this.points));
		textViewDPC.setText(String.valueOf(getResources().getString(R.string.DPC) + this.donutPerTap));
	}

}

