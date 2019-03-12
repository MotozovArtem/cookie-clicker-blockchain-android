package ru.rienel.clicker.activity.game;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import ru.rienel.clicker.R;
import ru.rienel.clicker.common.Preconditions;
import ru.rienel.clicker.ui.dialog.EndGameDialogFragment;
import ru.rienel.clicker.ui.dialog.ErrorMultiplayerDialogFragment;

public class GameFragment extends Fragment implements GameContract.View, SoundPool.OnLoadCompleteListener {
	private static final String TIMER_TIME_FORMAT = "%02d:%02d";
	private static final String TAG = GameFragment.class.getName();
	private static final String DIALOG_ERROR = ErrorMultiplayerDialogFragment.class.getName();

	private GameContract.Presenter presenter;

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
	private CountDownTimer countDownTimer;

	private Button incTap;
	private Button autoTap;
	private int tempTap;
	private int tempAutoTap;

	private SoundPool soundPool;
	private int soundId;
	private SoundPool backgroundSound;
	private int backgroundSoundId;
	private SharedPreferences saves;

	private EndGameDialogFragment endGameDialogFragment;
	private ErrorMultiplayerDialogFragment errorDialogFragment;

	public static GameFragment newInstance() {
		return new GameFragment();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_game, container, false);

		saves = root.getContext().getSharedPreferences(getString(R.string.gameSaves), Context.MODE_PRIVATE);
		loadGameSaves();
		incTap = root.findViewById(R.id.btnIncTap);
		incTap.setOnClickListener(newOnIncrementTapClickListener());
		autoTap = root.findViewById(R.id.btnAutoTap);
		autoTap.setOnClickListener(newOnAutoTapClickListener());
		checkPurchasedItem(incTap, autoTap);

		clicks = 0;
		point = root.findViewById(R.id.tvClicks);
		shop = root.findViewById(R.id.btnShop);
		time = root.findViewById(R.id.time);
		progressBar = root.findViewById(R.id.progressBar);
		donutImage = root.findViewById(R.id.donut);
		clock = root.findViewById(R.id.stopwatch);

		newClick = root.findViewById(R.id.newClick);
		newClick.setVisibility(View.INVISIBLE);

		timeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.timer_animation);
		rotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.donut_rotate);
		donutClickAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.donut_on_click_animation);

		//Tap sound
		soundPool = newSoundPool();
		soundId = soundPool.load(getActivity(), R.raw.muda, 1);

		// Background sound
		backgroundSound = new SoundPool.Builder().setMaxStreams(1).build();
		backgroundSound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int i, int i1) {
				backgroundSound.play(backgroundSoundId, 1, 1, 0, -1, 1);

			}
		});

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

		backgroundSoundId = backgroundSound.load(this.getContext(), R.raw.epic_sax_guy_v6, 1);

		donutImage.setOnClickListener(newOnDonutClickListener());

		initializeActivityState(savedInstanceState);

		return root;
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if (savedInstanceState != null) {
			loadInstanceState(savedInstanceState);
		}
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("dpc_state", donutPerClick);
		outState.putInt("clicks_state", clicks);
		outState.putBoolean("flagShop_state", flagShop);
		outState.putLong("currentTime_state", currentTime);
	}

	@Override
	public void onResume() {
		super.onResume();
		backgroundSound.resume(1);
		point.setText(String.format(Locale.ENGLISH, "%d", clicks));
		donutImage.startAnimation(rotateAnimation);
		point.setTextColor(getResources().getColor(R.color.colorPoint));
		if (currentTime != 0) {
			timer = currentTime;
		} else {
			timer = 60000;
		}
		countDownTimer = newCountDownTimer();
	}

	@Override
	public void onPause() {
		super.onPause();
		donutImage.clearAnimation();
		countDownTimer.cancel();
		if (flagShop) {
			countDownTimerBoost.cancel();
		}
		backgroundSound.pause(1);
		saveClicks(this.clicks);
	}

	@Override
	public void setPresenter(GameContract.Presenter presenter) {
		Preconditions.checkNotNull(presenter);
		this.presenter = presenter;
	}

	@Override
	public void showEndGameDialog() {

	}

	@Override
	public Context getActivityContext() {
		return this.getActivity();
	}

	@Override
	public void setClicks(Integer clicks) {
		point.setText(String.format(Locale.ENGLISH, "%d", clicks));
	}

	@Override
	public void setNewClick(Integer donutPerClick) {
		newClick.setText(String.format(Locale.ENGLISH, "+%d", this.donutPerClick));
	}

	@Override
	public void errorMultiplayer(Throwable e) {
		ErrorMultiplayerDialogFragment dialogFragment = ErrorMultiplayerDialogFragment.newInstance(e);
		FragmentManager manager = getFragmentManager();
		if (manager != null) {
			dialogFragment.show(getFragmentManager(), DIALOG_ERROR);
		} else {
			Log.e(TAG, "errorMultiplayer: FRAGMENT MANGER IS NULL!!!");
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null) {
			return;
		}
		donutPerClick = data.getIntExtra("donutPerClick", donutPerClick);
		clicks = data.getIntExtra("clicks", clicks);
		flagShop = data.getBooleanExtra("flagShop", flagShop);
		currentTime = data.getLongExtra("currentTime", currentTime);
	}

	@Override
	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

	}

	private SoundPool newSoundPool() {
		// Click sound
		SoundPool soundPool = new SoundPool.Builder()
				.setMaxStreams(1)
				.build();
		soundPool.setOnLoadCompleteListener((thisSoundPool, sampleId, status) -> {
		});
		return soundPool;
	}

	private void initializeActivityState(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			donutPerClick = 1;
			clicks = 0;
			flagShop = false;
			currentTime = 0;
		} else {
			loadInstanceState(savedInstanceState);
		}
	}

	private void loadInstanceState(Bundle savedInstanceState) {
		donutPerClick = savedInstanceState.getInt("dpc_state");
		clicks = savedInstanceState.getInt("clicks_state");
		flagShop = savedInstanceState.getBoolean("flagShop_state");
		currentTime = savedInstanceState.getLong("currentTime_state");
	}

	public CountDownTimer newCountDownTimer() {
		return new CountDownTimer(timer, 1000) {
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
				/*TODO: get message from End game dialog fragment*/
				String message = String.format(Locale.ENGLISH, "You earned %d clicks", clicks);

				presenter.finishGame(message, 100); // TODO: set level
//				dialog.setMessage(message);
//				dialog.show();
			}
		};
	}


	private void donutClick() {
		this.clicks += this.donutPerClick;
		point.setText(Integer.toString(clicks));
		newClick.setText(String.format(Locale.ENGLISH, "+%d", this.donutPerClick));
	}

	public View.OnClickListener newOnDonutClickListener() {
		return view -> {
			presenter.handleClick();
			soundPool.play(soundId, 1, 1, 0, 0, 1);
			view.startAnimation(donutClickAnimation);
		};
	}

	public View.OnClickListener newOnIncrementTapClickListener() {
		return view -> {
			this.donutPerClick += 2;
			this.tempTap -= 1;
			saves.edit().putInt("tempTap", this.tempTap).apply();
			checkPurchasedItem(incTap, autoTap);
			new CountDownTimer(10000, 1000) {

				@Override
				public void onTick(long l) {

				}

				@Override
				public void onFinish() {
					donutPerClick -= 2;
				}
			}.start();
		};
	}

	public View.OnClickListener newOnAutoTapClickListener() {
		return view -> {
			this.tempAutoTap -= 1;
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
		};
	}

	private void loadGameSaves() {
		this.donutPerClick = saves.getInt("donutPerClick", 0);
		this.tempTap = saves.getInt("tempTap", 0);
		this.tempAutoTap = saves.getInt("tempAutoTap", 0);
	}

	private void checkPurchasedItem(Button incTap, Button autoTap) {
		if (tempTap > 0) {
			incTap.setEnabled(true);
//			incTap.setText(R.string.IncTap + " " + tempTap);
			String message = getResources().getString(R.string.IncClick) + " -" + tempTap;
			incTap.setText(message);
		} else {
			incTap.setEnabled(false);
			incTap.setText(R.string.IncClick);
		}

		if (tempAutoTap > 0) {
			autoTap.setEnabled(true);
//			autoTap.setText(R.string.AutoTap + " " + tempAutoTap);
			String message = getResources().getString(R.string.AutoClick) + " -" + tempAutoTap;
			autoTap.setText(message);
		} else {
			autoTap.setEnabled(false);
			autoTap.setText(R.string.AutoClick);
		}
	}

	private void saveClicks(int clicks) {
		saves = getContext().getSharedPreferences(getString(R.string.gameSaves), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = saves.edit();
		int tempClicks = saves.getInt("clicks", 0);
		editor.putInt("clicks", clicks + tempClicks);
		editor.apply();
	}

}
