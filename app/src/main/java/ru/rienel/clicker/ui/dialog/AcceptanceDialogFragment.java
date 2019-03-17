package ru.rienel.clicker.ui.dialog;

import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ru.rienel.clicker.R;
import ru.rienel.clicker.activity.game.GameActivity;
import ru.rienel.clicker.activity.game.GameType;
import ru.rienel.clicker.db.domain.Opponent;

public class AcceptanceDialogFragment extends DialogFragment {
	public static final String TAG = AcceptanceDialogFragment.class.getName();

	private static final int TIME_FOR_ANSWER = 10_000;
	private static final int COUNT_DOWN_INTERVAL = 1000;

	private TextView opponentNameLabel;
	private TextView remainingAcceptanceTime;
	private CountDownTimer timer;

	private Opponent opponent;

	public static AcceptanceDialogFragment newInstance(Opponent opponent) {
		AcceptanceDialogFragment dialogFragment = new AcceptanceDialogFragment();
		dialogFragment.setOpponent(opponent);
		return dialogFragment;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		View root = LayoutInflater.from(getActivity())
				.inflate(R.layout.dialog_acceptance, null);

		opponentNameLabel = root.findViewById(R.id.dialog_opponent_name);
		opponentNameLabel.setText(opponent.getName());
		remainingAcceptanceTime = root.findViewById(R.id.dialog_acceptance_remaining_time);

		timer = newTimerForDialog();
		timer.start();

		return new AlertDialog.Builder(getActivity())
				.setView(root)
				.setCancelable(true)
				.setPositiveButton(R.string.ok, newOnOkClickListener())
				.create();
	}

	private DialogInterface.OnClickListener newOnOkClickListener() {
		return (dialog, which) -> {
			Intent goToGameIntent = new Intent(getActivity(), GameActivity.class);
			goToGameIntent.putExtra(GameActivity.INTENT_GAME_TYPE, GameType.MULTIPLAYER);
			goToGameIntent.putExtra(GameActivity.INTENT_ADDRESS, this.opponent.getIpAddress());
			startActivity(goToGameIntent);
		};
	}

	private CountDownTimer newTimerForDialog() {
		return new CountDownTimer(TIME_FOR_ANSWER, COUNT_DOWN_INTERVAL) {
			private String timePattern = "%tS";

			@Override
			public void onTick(long millisUntilFinished) {
				remainingAcceptanceTime.setText(String.format(Locale.ENGLISH, timePattern, millisUntilFinished / 1000));
			}

			@Override
			public void onFinish() {
				AcceptanceDialogFragment.this.dismiss();
			}
		};
	}

	private Opponent getOpponent() {
		return opponent;
	}

	private void setOpponent(Opponent opponentName) {
		this.opponent = opponentName;
	}
}
