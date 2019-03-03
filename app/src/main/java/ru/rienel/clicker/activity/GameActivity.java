package ru.rienel.clicker.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.CountDownTimer;
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
	private int donutPerTap;
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
	private Button incTap;
	private Button autoTap;
	private int tempTap;
	private int tempAutoTap;

	private SoundPool soundPool;
	private int soundId;
	private MediaPlayer mediaPlayer;

	private SharedPreferences saves;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		saves = getSharedPreferences(getString(R.string.gameSaves), Context.MODE_PRIVATE);
		loadGameSaves();
		incTap = findViewById(R.id.btnIncTap);
		autoTap = findViewById(R.id.btnAutoTap);
		checkPurchasedItem(incTap, autoTap);

		points = 0;
		point = findViewById(R.id.tvPoints);
		shop = findViewById(R.id.btnShop);
		time = findViewById(R.id.time);
		progressBar = findViewById(R.id.prBar);
		donutImage = findViewById(R.id.imageDonut);
		clock = findViewById(R.id.tvClock);
		newClick = findViewById(R.id.newClick);
		context = GameActivity.this;

		// Background sound
		mediaPlayer = MediaPlayer.create(this,R.raw.epic_sax_guy_v3);
		mediaPlayer.setLooping(true);
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mediaPlayer) {
				mediaPlayer.stop();
				mediaPlayer.release();
			}
		});


		//Tap sound
		soundPool = new SoundPool.Builder().setMaxStreams(1).build();
		soundPool. setOnLoadCompleteListener(this);
		soundId = soundPool.load(this, R.raw.muda,1);

		blockRepository = new BlockDaoImpl(this);
//		if (savedInstanceState == null) {   // app started at first
//			donutPerTap = 1;
//			points = 0;
//			flagShop = false;
//			currentTime = 0;
//			currentTimeBoost = 0;
//		} else {
//			donutPerTap = savedInstanceState.getInt("dpc_state");
//			points = savedInstanceState.getInt("points_state");
//			flagShop = savedInstanceState.getBoolean("flagShop_state");
//			currentTime = savedInstanceState.getLong("currentTime_state");
//			currentTimeBoost = savedInstanceState.getLong("currentTimeBoost_state");
//		}


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
			timer = 60000;
		}
		mediaPlayer.start();
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
					donutPerTap = 1;
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
		mediaPlayer.pause();
		savePoints(this.points);
	}




	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("dpc_state", donutPerTap);
		outState.putInt("points_state", points);
		outState.putBoolean("flagShop_state", flagShop);
		outState.putLong("currentTime_state", currentTime);
		outState.putLong("currentTimeBoost_state", currentTimeBoost);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		donutPerTap = savedInstanceState.getInt("dpc_state");
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
		} else if (v.getId() == R.id.btnIncTap) {
			this.donutPerTap += 2;
			this.tempTap-= 1;
			saves.edit().putInt("tempTap", this.tempTap).apply();
			checkPurchasedItem(incTap, autoTap);
			new CountDownTimer(10000, 1000) {

				@Override
				public void onTick(long l) {

				}

				@Override
				public void onFinish() {
					donutPerTap -= 2;
				}
			}.start();
		} else if (v.getId() == R.id.btnAutoTap) {
			this.tempAutoTap-= 1;
			saves.edit().putInt("tempAutoTap", this.tempAutoTap).apply();
			checkPurchasedItem(incTap, autoTap);
			new CountDownTimer(10000, 1000) {

				@Override
				public void onTick(long l) {
					donutClick();
				}

				@Override
				public void onFinish() {

				}
			}.start();
		}
	}

	private void donutClick() {
		this.points += this.donutPerTap;
		point.setText(Integer.toString(points));
		newClick.setText(String.format(Locale.ENGLISH, "+%d", this.donutPerTap));
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		donutPerTap = data.getIntExtra("donutPerTap", donutPerTap);
		points = data.getIntExtra("points", points);
		flagShop = data.getBooleanExtra("flagShop", flagShop);
		currentTime = data.getLongExtra("currentTime", currentTime);
	}


	@Override
	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

	}

	private void savePoints(int points) {
		saves = getSharedPreferences(getString(R.string.gameSaves), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = saves.edit();
		int tempPoints = saves.getInt("points",0);
		editor.putInt("points", points + tempPoints);
		editor.apply();
	}

	private void loadGameSaves() {
		this.donutPerTap = saves.getInt("donutPerTap", 0);
		this.tempTap = saves.getInt("tempTap", 0);
		this.tempAutoTap = saves.getInt("tempAutoTap", 0);
	}

	private void checkPurchasedItem(Button incTap, Button autoTap) {
		if (tempTap > 0) {
			incTap.setEnabled(true);
//			incTap.setText(R.string.IncTap + " " + tempTap);
			String message = getResources().getString(R.string.IncTap) + " -" + tempTap;
			incTap.setText(message);
		} else {
			incTap.setEnabled(false);
			incTap.setText(R.string.IncTap);
		}

		if (tempAutoTap > 0) {
			autoTap.setEnabled(true);
//			autoTap.setText(R.string.AutoTap + " " + tempAutoTap);
			String message = getResources().getString(R.string.AutoTap) + " -" + tempAutoTap;
			autoTap.setText(message);
		} else {
			autoTap.setEnabled(false);
			autoTap.setText(R.string.AutoTap);
		}
	}


}
