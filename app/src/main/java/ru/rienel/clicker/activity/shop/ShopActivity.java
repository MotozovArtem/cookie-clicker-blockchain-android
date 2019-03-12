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

	private int donutPerClick;
	private int clicks;
	private int mClicks;
	private long currentTime;
	private boolean flagShop = false;

	private Button temporaryTap;
	private Button temporaryAutoTap;
	private Button plus5Donut;
	private TextView textViewMClicks;
	private TextView textViewClicks;
	private TextView textViewDPC;
	private SharedPreferences saves;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_activity);

		temporaryTap = findViewById(R.id.tempIncrementTap);
		temporaryAutoTap = findViewById(R.id.tempAutoTap);
		plus5Donut = findViewById(R.id.btn_5);
		temporaryTap.setEnabled(false);
		temporaryAutoTap.setEnabled(false);
		plus5Donut.setEnabled(false);

		loadGameSaves();
		textViewClicks = findViewById(R.id.clicksTextView);
		textViewMClicks = findViewById(R.id.mClicksTextView);
		textViewDPC = findViewById(R.id.donutPerClickTextView);
		updateTextView();


		currentTime = getIntent().getLongExtra("currentTime", 60000);
		checkingClicks();

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.tempIncrementTap) {
			int tempTap = saves.getInt("tempTap", 0);
			saves.edit().putInt("tempTap", tempTap + 1).apply();
			updateClicks(10);
			updateTextView();
			checkingClicks();
		} else if (v.getId() == R.id.tempAutoTap) {
			int tempAutoTap = saves.getInt("tempAutoTap", 0);
			saves.edit().putInt("tempAutoTap", tempAutoTap + 1).apply();
			updateClicks(20);
			updateTextView();
			checkingClicks();
		} else if (v.getId() == 0) {  // TODO chahge "0" on multiplayer tap button from XML
			updateDonutPerTap(1,100);
		} else if (v.getId() == 0) { // TODO change "0" on multiplaer auto tap button from XML
			int multiplayerAutoTap = saves.getInt("mAutoTap",0);
			saves.edit().putInt("mAutoTap", multiplayerAutoTap + 1).apply();
			updateMClicks(300);
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

	private boolean updateDps(int donutPerTap, int clicks) {
		this.donutPerClick = donutPerTap;
		updateClicks(clicks);
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("dpc_state", donutPerClick);
		outState.putInt("clicks_state", clicks);
		outState.putBoolean("flagShop_state", flagShop);
		outState.putLong("currentTime_state", currentTime);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		donutPerClick = savedInstanceState.getInt("dpc_state");
		clicks = savedInstanceState.getInt("clicks_state");
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
		saveGameSaves(this.clicks, this.donutPerClick, this.mClicks);
	}

	private void updateClicks(int clicks) {
		this.clicks -= clicks;
	}

	private void showDonutFragment() {
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra("dpc", donutPerClick);
		intent.putExtra("clicks", clicks);
		intent.putExtra("flagShop", flagShop);
		intent.putExtra("currentTime", currentTime);
		setResult(RESULT_OK, intent);
		finish();
	}

	private boolean updateDonutPerTap(int donutPerClick, int mClicks) {
		this.donutPerClick -=  donutPerClick;
		updateMClicks(mClicks);
		updateTextView();
		return true;
	}


	private void updateMClicks(int mClicks) {
		this.mClicks -= mClicks;
	}

	private void checkingClicks() {
		if (clicks >= 10) {
			temporaryTap.setEnabled(true);
			if (clicks >= 20) {
				temporaryAutoTap.setEnabled(true);
				if (clicks >= 30) {
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

	private void saveGameSaves(int clicks, int dpc, int mClicks) {
		saves = getSharedPreferences(getString(R.string.gameSaves), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = saves.edit();
		editor.putInt("clicks", clicks);
		editor.putInt("donutPerClick", dpc);
		editor.putInt("mClicks", mClicks);
		editor.apply();
	}

	private void loadGameSaves () {
		saves = getSharedPreferences(getString(R.string.gameSaves), Context.MODE_PRIVATE);
			this.donutPerClick = saves.getInt("donutPerClick",0);
			this.clicks = saves.getInt("clicks", 0);
			this.mClicks = saves.getInt("mClicks",0);

	}

	private void updateTextView() {
		textViewClicks.setText(String.valueOf(getResources().getString(R.string.clicks) + this.clicks));
		textViewDPC.setText(String.valueOf(getResources().getString(R.string.DPC) + this.donutPerClick));
		textViewMClicks.setText(String.valueOf(getResources().getString(R.string.multiplayer_clicks) + this.mClicks));
	}

}

