package ru.rienel.clicker.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import ru.rienel.clicker.R;
import ru.rienel.clicker.db.domain.Block;
import ru.rienel.clicker.db.domain.dao.DaoException;
import ru.rienel.clicker.db.domain.dao.Repository;
import ru.rienel.clicker.db.domain.dao.impl.BlockDaoImpl;
import ru.rienel.clicker.db.factory.domain.BlockFactory;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, OnLoadCompleteListener {

	private static final String FORMAT = "%02d:%02d";
	private static final String TAG = "GameActivity";

	private TextView point;
	private TextView newClick;
	private TextView clock;
	private ImageView donutImage;
	private TextView time;
	private Button shop;
	private ProgressBar progressBar;
	private Integer points;
	private int donutPerClick;
	private CountDownTimer countDownTimerBoost;
	private boolean flagShop;
	private long timer;
	private long currentTime;
	private Animation rotateAnimation;
	private Animation timeAnimation;
	private Animation donutClickAnimation;
	private long timeBoost;
	private long currentTimeBoost;
	private CountDownTimer countDownTimer;
	private AlertDialog.Builder dialog;
	private Context context;
	private Repository<Block> blockRepository;

	SoundPool soundPool;
	SoundPool backSoundPool;
	int soundId;
	int backSoundPoolId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		point = findViewById(R.id.tvPoints);
		shop = findViewById(R.id.btnShop);
		time = findViewById(R.id.time);
		progressBar = findViewById(R.id.prBar);
		donutImage = findViewById(R.id.imageDonut);
		clock = findViewById(R.id.tvClock);
		newClick = findViewById(R.id.newClick);
		context = GameActivity.this;

		//Звук нажатия
		backSoundPool = new SoundPool.Builder()
				.setMaxStreams(1)
				.setAudioAttributes(new AudioAttributes.Builder()
						.setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_DELAYED)
						.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
						.build())
				.build();
		backSoundPoolId = backSoundPool.load(this,R.raw.ora_vs_muda_v2,1);
		backSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int i, int i1) {
				soundPool.play(backSoundPoolId,1,1,0,-1,1);
			}
		});
		soundPool = new SoundPool.Builder().setMaxStreams(10).build();
		soundPool. setOnLoadCompleteListener(this);
		soundId = soundPool.load(this, R.raw.muda,1);

		blockRepository = new BlockDaoImpl(this);
		if (savedInstanceState == null) {   // приложение запущено впервые
			donutPerClick = 1;
			points = 0;
			flagShop = false;
			currentTime = 0;
			currentTimeBoost = 0;
		} else {
			donutPerClick = savedInstanceState.getInt("dpc_state");
			points = savedInstanceState.getInt("points_state");
			flagShop = savedInstanceState.getBoolean("flagShop_state");
			currentTime = savedInstanceState.getLong("currentTime_state");
			currentTimeBoost = savedInstanceState.getLong("currentTimeBoost_state");
		}


		newClick.setVisibility(View.INVISIBLE);
		timeAnimation = AnimationUtils.loadAnimation(this, R.anim.timer_animation);
		rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.donut_rotate);
		donutClickAnimation = AnimationUtils.loadAnimation(this, R.anim.donut_animation);

		donutClickAnimation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				donutClick();
				newClick.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				newClick.setVisibility(View.INVISIBLE);
				donutImage.startAnimation(rotateAnimation);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

		dialog = new AlertDialog.Builder(context);
		dialog.setTitle(R.string.titleDialog);
		dialog.setIcon(R.drawable.donut_icon);
		dialog.setPositiveButton(R.string.continueGameDialog, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				finish();
			}
		});
		dialog.setCancelable(false);
	}



	@Override
	public void onResume() {
		super.onResume();
		point.setText(String.format(Locale.ENGLISH, "%d", points));
		donutImage.startAnimation(rotateAnimation);
		point.setTextColor(getResources().getColor(R.color.colorPoint));
		if (currentTime != 0) {
			timer = currentTime;
		} else {
			timer = 30000;
		}
		backSoundPool.resume(1);

		countDownTimer = new CountDownTimer(timer, 1000) {
			public void onTick(long millisUntilFinished) {
				if (millisUntilFinished <= 20000) {
					clock.startAnimation(timeAnimation);
				}
				clock.setText("" + String.format(FORMAT,
						TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
								TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
						TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
								TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
				currentTime = millisUntilFinished;
			}

			public void onFinish() {
				if (flagShop) {
					countDownTimerBoost.cancel();
				}
				donutImage.clearAnimation();
				clock.setText(R.string.endGame);
				String message = String.format(Locale.ENGLISH, "Вы набрали %d очков", points);
				dialog.setMessage(message);
				dialog.show();
				Block newBlock = BlockFactory.build(message, 100, new Date(System.currentTimeMillis()), "None",
						"none", "HASH");
				try {
					blockRepository.add(newBlock);
				} catch (DaoException e) {
					Log.i(TAG, "Failed add new block", e);
				}
			}
		};
		if (flagShop) {
			if (currentTimeBoost != 0) {
				timeBoost = currentTimeBoost;
			} else {
				timeBoost = 20000;
			}
			countDownTimerBoost = new CountDownTimer(timeBoost, 1000) {
				@Override
				public void onTick(long millisUntilFinished) {
					time.setVisibility(View.VISIBLE);
					progressBar.setVisibility(View.VISIBLE);
					shop.setEnabled(false);
					time.setText(String.format(Locale.ENGLISH,
							"Осталось: %d секунд", millisUntilFinished / 1000));
					progressBar.setProgress((int) millisUntilFinished / 1000);
					currentTimeBoost = millisUntilFinished;
					if (millisUntilFinished / 1000 <= 5) {
						time.setTextColor(Color.RED);
					}
				}

				@Override
				public void onFinish() {
					shop.setEnabled(true);
					time.setVisibility(View.GONE);
					progressBar.setVisibility(View.GONE);
					currentTimeBoost = 0;
					timeBoost = 20000;
					donutPerClick = 1;
					flagShop = false;
				}
			};
			countDownTimerBoost.start();
		} else {
			time.setVisibility(View.GONE);
			progressBar.setVisibility(View.GONE);
		}
		countDownTimer.start();
	}

	@Override
	public void onPause() {
		super.onPause();
		donutImage.clearAnimation();
		countDownTimer.cancel();
		if (flagShop) {
			countDownTimerBoost.cancel();
		}
		backSoundPool.pause(1);
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("dpc_state", donutPerClick);
		outState.putInt("points_state", points);
		outState.putBoolean("flagShop_state", flagShop);
		outState.putLong("currentTime_state", currentTime);
		outState.putLong("currentTimeBoost_state", currentTimeBoost);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		donutPerClick = savedInstanceState.getInt("dpc_state");
		points = savedInstanceState.getInt("points_state");
		flagShop = savedInstanceState.getBoolean("flagShop_state");
		currentTime = savedInstanceState.getLong("currentTime_state");
		currentTimeBoost = savedInstanceState.getLong("currentTimeBoost_state");
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.imageDonut) {
			soundPool.play(soundId, 1, 1, 0, 0, 1);
			v.startAnimation(donutClickAnimation);
		} else if (v.getId() == R.id.btnShop) {
			showShopFragment();
		}
	}

	private void donutClick() {
		this.points += this.donutPerClick;
		point.setText(Integer.toString(points));
		newClick.setText(String.format(Locale.ENGLISH, "+%d", this.donutPerClick));
	}

	private void showShopFragment() {
		Intent intent = new Intent(this, ShopActivity.class);
		intent.putExtra("points", points);
		intent.putExtra("donutPerClick", donutPerClick);
		intent.putExtra("currentTime", currentTime);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		donutPerClick = data.getIntExtra("donutPerClick", donutPerClick);
		points = data.getIntExtra("points", points);
		flagShop = data.getBooleanExtra("flagShop", flagShop);
		currentTime = data.getLongExtra("currentTime", currentTime);
	}


	@Override
	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

	}
}
