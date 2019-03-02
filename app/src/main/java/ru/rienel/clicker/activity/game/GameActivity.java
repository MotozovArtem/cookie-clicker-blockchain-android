package ru.rienel.clicker.activity.game;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentManager;
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
import ru.rienel.clicker.activity.shop.ShopActivity;
import ru.rienel.clicker.db.domain.Block;
import ru.rienel.clicker.db.domain.dao.DaoException;
import ru.rienel.clicker.db.domain.dao.Repository;
import ru.rienel.clicker.db.domain.dao.impl.BlockDaoImpl;
import ru.rienel.clicker.db.factory.domain.BlockFactory;
import ru.rienel.clicker.ui.dialog.EndGameDialogFragment;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, GameView, OnLoadCompleteListener {

	private static final String TIMER_TIME_FORMAT = "%02d:%02d";
	private static final String TAG = "GameActivity";

	private GamePresenter presenter;

	private TextView point;
	private TextView newClick;
	private TextView clock;
	private ImageView donutImage;
	private TextView time;
	private Button shop;
	private ProgressBar progressBar;
	private Integer clicks;
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
	private EndGameDialogFragment dialogFragment;
	private FragmentManager fragmentManager;

	private SoundPool soundPool;
	private SoundPool backSoundPool;
	private int soundId;
	private int backSoundPoolId;
	private MediaPlayer mediaPlayer;

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

		// фоновая музыка
		mediaPlayer = MediaPlayer.create(this, R.raw.epic_sax_guy_v3);
		mediaPlayer.setLooping(true);
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mediaPlayer) {
				mediaPlayer.stop();
				mediaPlayer.release();
			}
		});


		//Звук нажатия
		soundPool = new SoundPool.Builder().setMaxStreams(1).build();
		soundPool.setOnLoadCompleteListener(this);
		soundId = soundPool.load(this, R.raw.muda, 1);

		blockRepository = new BlockDaoImpl(this);

		initializeActivityState(savedInstanceState);

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
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				finish();
			}
		});
		dialog.setCancelable(false);
	}

	private void initializeActivityState(Bundle savedInstanceState) {
		if (savedInstanceState == null) {   // приложение запущено впервые
			donutPerClick = 1;
			clicks = 0;
			flagShop = false;
			currentTime = 0;
			currentTimeBoost = 0;
		} else {
			loadInstanceState(savedInstanceState);
		}
	}

	private void loadInstanceState(Bundle savedInstanceState) {
		donutPerClick = savedInstanceState.getInt("dpc_state");
		clicks = savedInstanceState.getInt("points_state");
		flagShop = savedInstanceState.getBoolean("flagShop_state");
		currentTime = savedInstanceState.getLong("currentTime_state");
		currentTimeBoost = savedInstanceState.getLong("currentTimeBoost_state");
	}

	@Override
	public void onResume() {
		super.onResume();
		point.setText(String.format(Locale.ENGLISH, "%d", clicks));
		donutImage.startAnimation(rotateAnimation);
		point.setTextColor(getResources().getColor(R.color.colorPoint));
		if (currentTime != 0) {
			timer = currentTime;
		} else {
			timer = 60000;
		}
		mediaPlayer.start();
		countDownTimer = new CountDownTimer(timer, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				if (millisUntilFinished <= 20000) {
					clock.startAnimation(timeAnimation);
				}
				clock.setText(String.format(Locale.getDefault(), TIMER_TIME_FORMAT,
						TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
								TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
						TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
								TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
				);
				currentTime = millisUntilFinished;
			}

			@Override
			public void onFinish() {
				if (flagShop) {
					countDownTimerBoost.cancel();
				}
				donutImage.clearAnimation();
				clock.setText(R.string.endGame);
				String message = String.format(Locale.ENGLISH, "Вы набрали %d очков", clicks);
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
		mediaPlayer.pause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("dpc_state", donutPerClick);
		outState.putInt("points_state", clicks);
		outState.putBoolean("flagShop_state", flagShop);
		outState.putLong("currentTime_state", currentTime);
		outState.putLong("currentTimeBoost_state", currentTimeBoost);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		loadInstanceState(savedInstanceState);
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
		this.clicks += this.donutPerClick;
		point.setText(String.format(Locale.ENGLISH, "%d", clicks));
		newClick.setText(String.format(Locale.ENGLISH, "+%d", this.donutPerClick));
	}

	private void showShopFragment() {
		Intent intent = new Intent(this, ShopActivity.class);
		intent.putExtra("clicks", clicks);
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
		clicks = data.getIntExtra("clicks", clicks);
		flagShop = data.getBooleanExtra("flagShop", flagShop);
		currentTime = data.getLongExtra("currentTime", currentTime);
	}

	@Override
	public void showWin() {

	}

	@Override
	public void hideWin() {

	}

	@Override
	public void stopGame() {

	}

	@Override
	public void startGame() {

	}

	@Override
	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

	}


}
