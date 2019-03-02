package ru.rienel.clicker.activity.shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import ru.rienel.clicker.R;
import ru.rienel.clicker.activity.game.GameActivity;

public class ShopActivity extends AppCompatActivity implements View.OnClickListener {

	private int dpc;
	private int points;
	private long currentTime;
	private boolean flagShop = false;

	private Button plus2Donut;
	private Button plus4Donut;
	private Button plus5Donut;


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

		points = getIntent().getIntExtra("points", 0);
		dpc = getIntent().getIntExtra("dpc", 0);
		currentTime = getIntent().getLongExtra("currentTime", 60000);

		if (points >= 10) {
			plus2Donut.setEnabled(true);
			if (points >= 20) {
				plus4Donut.setEnabled(true);
				if (points >= 30) {
					plus5Donut.setEnabled(true);
				}
			}
		}

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_2) {
			flagShop = updateDps(2, 10);
			showDonutFragment();
		} else if (v.getId() == R.id.btn_4) {
			flagShop = updateDps(4, 20);
			showDonutFragment();
		} else if (v.getId() == R.id.btn_5) {
			flagShop = updateDps(5, 30);
			showDonutFragment();
		} else if (v.getId() == R.id.btnBack) {
			showDonutFragment();
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

	private boolean updateDps(int dpc, int points) {
		this.dpc = dpc;
		updatePoints(points);
		return true;
	}

	private void updatePoints(int points) {
		this.points -= points;
	}

	private void showDonutFragment() {
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra("dpc", dpc);
		intent.putExtra("points", points);
		intent.putExtra("flagShop", flagShop);
		intent.putExtra("currentTime", currentTime);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("dpc_state", dpc);
		outState.putInt("points_state", points);
		outState.putBoolean("flagShop_state", flagShop);
		outState.putLong("currentTime_state", currentTime);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		dpc = savedInstanceState.getInt("dpc_state");
		points = savedInstanceState.getInt("points_state");
		flagShop = savedInstanceState.getBoolean("flagShop_state");
		currentTime = savedInstanceState.getLong("currentTime_state");
	}
}

