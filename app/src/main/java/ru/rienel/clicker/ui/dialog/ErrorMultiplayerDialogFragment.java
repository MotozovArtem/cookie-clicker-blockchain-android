package ru.rienel.clicker.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import ru.rienel.clicker.R;
import ru.rienel.clicker.activity.main.MainActivity;

public class ErrorMultiplayerDialogFragment extends DialogFragment {
	private static final String TAG = ErrorMultiplayerDialogFragment.class.getName();

	private TextView dialogTitle;
	private ScrollView message;
	private Throwable causedByException;

	public static ErrorMultiplayerDialogFragment newInstance(Throwable e) {
		ErrorMultiplayerDialogFragment dialogFragment = new ErrorMultiplayerDialogFragment();
		dialogFragment.setCausedByException(e);
		return dialogFragment;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity())
				.inflate(R.layout.dialog_error_multiplayer, null);

		TextView messageText = new TextView(getActivity());
		messageText.setText(causedByException.getMessage());

		message = view.findViewById(R.id.dialog_error_message);
		message.addView(messageText);

		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.warning_title)
				.setView(view)
				.setPositiveButton(android.R.string.ok, getOnClickListener())
				.create();
	}

	private DialogInterface.OnClickListener getOnClickListener() {
		return (dialog, which) -> {
			Intent intent = new Intent(getContext(), MainActivity.class);
			startActivity(intent);
		};
	}

	public Throwable getCausedByException() {
		return causedByException;
	}

	public void setCausedByException(Throwable causedByException) {
		this.causedByException = causedByException;
	}
}
