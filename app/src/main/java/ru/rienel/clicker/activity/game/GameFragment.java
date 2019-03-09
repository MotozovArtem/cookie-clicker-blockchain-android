package ru.rienel.clicker.activity.game;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class GameFragment extends Fragment implements GameContract.View, SoundPool.OnLoadCompleteListener {
	private static final String TIMER_TIME_FORMAT = "%02d:%02d";
	private static final String TAG = GameFragment.class.getName();

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

	private SoundPool soundPool;
	private int soundId;
	private MediaPlayer mediaPlayer;

	private EndGameDialogFragment dialogFragment;

	public static GameFragment newInstance() {
		return new GameFragment();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_game, container, false);
		point = root.findViewById(R.id.tvPoints);
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

		soundPool = newSoundPool();
		soundId = soundPool.load(getActivity(), R.raw.muda, 1);

		mediaPlayer = newMediaPlayer();

		donutImage.setOnClickListener(newOnDonutClickListener());

		initializeActivityState(savedInstanceState);

		return root;
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

	private MediaPlayer newMediaPlayer() {
		// Background sound
		MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.epic_sax_guy_v3);
		mediaPlayer.setLooping(true);
		mediaPlayer.setOnCompletionListener(thisMediaPlayer -> {
			thisMediaPlayer.stop();
			thisMediaPlayer.release();
		});
		return mediaPlayer;
	}

	private void initializeActivityState(Bundle savedInstanceState) {
		if (savedInstanceState == null) {   // приложение запущено впервые
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
		clicks = savedInstanceState.getInt("points_state");
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
		outState.putInt("points_state", clicks);
		outState.putBoolean("flagShop_state", flagShop);
		outState.putLong("currentTime_state", currentTime);
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

		countDownTimer = newCountDownTimer();

		mediaPlayer.start();
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
	public void setPoints(Integer clicks) {
		point.setText(String.format(Locale.ENGLISH, "%d", clicks));
	}

	@Override
	public void setNewClick(Integer donutPerClick) {
		newClick.setText(String.format(Locale.ENGLISH, "+%d", this.donutPerClick));
	}

	public View.OnClickListener newOnDonutClickListener() {
		return view -> {
			presenter.handleClick();
			soundPool.play(soundId, 1, 1, 0, 0, 1);
			view.startAnimation(donutClickAnimation);
		};
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
}
