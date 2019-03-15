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
	private int mAutoClicksCounter;
	private long currentTime;
	private boolean flagShop = false;

	private Button temporaryTap;
	private Button temporaryAutoTap;
	private Button mAutoClick;
	private Button mIncrementClick;
	private TextView textViewMClicks;
	private TextView textViewClicks;
	private TextView textViewDPC;
	private TextView textViewMultiplayerAutoClicks;
	private SharedPreferences saves;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_activity);

		temporaryTap = findViewById(R.id.tempIncrementClick);
		temporaryAutoTap = findViewById(R.id.tempAutoClick);
		mAutoClick = findViewById(R.id.mAutoClick);
		mIncrementClick = findViewById(R.id.mIncrementClick);
		temporaryTap.setEnabled(false);
		temporaryAutoTap.setEnabled(false);
		mAutoClick.setEnabled(false);
		mIncrementClick.setEnabled(false);

		loadGameSaves();
		textViewClicks = findViewById(R.id.clicksTextView);
		textViewMClicks = findViewById(R.id.mClicksTextView);
		textViewDPC = findViewById(R.id.donutPerClickTextView);
		textViewMultiplayerAutoClicks = findViewById(R.id.multiplayerAutoClicksTextView);
		updateTextView();


		currentTime = getIntent().getLongExtra("currentTime", 60000);
		checkingClicks();
		checkingMultiplayerClicks();

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.tempIncrementClick) {
			int tempClicks = saves.getInt("tempClicks", 0);
			saves.edit().putInt("tempClicks", tempClicks + 1).apply();
			updateClicks(10);
			updateTextView();
			checkingClicks();
		} else if (v.getId() == R.id.tempAutoClick) {
			int tempAutoClicks = saves.getInt("tempAutoClicks", 0);
			saves.edit().putInt("tempAutoClicks", tempAutoClicks + 1).apply();
			updateClicks(20);
			updateTextView();
			checkingClicks();
		} else if (v.getId() == R.id.mIncrementClick) {
			updateDonutPerClick(1,100);
		} else if (v.getId() == R.id.mAutoClick) {
			int multiplayerAutoClicks = saves.getInt("mAutoClicks",0);
			saves.edit().putInt("mAutoClicks", multiplayerAutoClicks + 1).apply();
			updateMultiplayerAutoClicks(1,200);
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

	private boolean updateDonutPerClick(int donutPerClick, int mClicks) {
		this.donutPerClick +=  donutPerClick;
		updateMClicks(mClicks);
		updateTextView();
		checkingMultiplayerClicks();
		return true;
	}

	private boolean updateMultiplayerAutoClicks(int mAutoClicksCounter, int mClicks) {
		this.mAutoClicksCounter += mAutoClicksCounter;
		updateMClicks(mClicks);
		updateTextView();
		checkingMultiplayerClicks();
		return  true;
	}


	private void updateMClicks(int mClicks) {
		this.mClicks -= mClicks;
	}

	private void checkingClicks() {
		if (clicks >= 10) {
			temporaryTap.setEnabled(true);
			if (clicks >= 20) {
				temporaryAutoTap.setEnabled(true);

			} else {
				temporaryAutoTap.setEnabled(false);
			}
		} else {
			temporaryTap.setEnabled(false);
			temporaryAutoTap.setEnabled(false);
		}
	}

	private void checkingMultiplayerClicks() {
		if (mClicks >= 100) {
			mAutoClick.setEnabled(true);
			if (mClicks >= 200) {
				mIncrementClick.setEnabled(true);
			} else {
				mIncrementClick.setEnabled(false);
			}
		} else {
			mIncrementClick.setEnabled(false);
			mAutoClick.setEnabled(false);
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
			this.mAutoClicksCounter =  saves.getInt("mAutoClicks", 0);

	}

	private void updateTextView() {
		textViewClicks.setText(String.valueOf(getResources().getString(R.string.clicks) + this.clicks));
		textViewDPC.setText(String.valueOf(getResources().getString(R.string.DPC) + this.donutPerClick));
		textViewMClicks.setText(String.valueOf(getResources().getString(R.string.multiplayer_clicks) + this.mClicks));
		textViewMultiplayerAutoClicks.setText(String.valueOf(getResources().getString(R.string.multiplayer_auto_clicks) + this.mAutoClicksCounter));
	}

}

