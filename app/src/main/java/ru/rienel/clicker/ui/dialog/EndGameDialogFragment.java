package ru.rienel.clicker.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import ru.rienel.clicker.R;

public class EndGameDialogFragment extends DialogFragment {
	private static final String ARG_TITLE_TYPE = "title_type";

	private TextView dialogTitle;
	private EditText dialogMessage;

	public static EndGameDialogFragment newInstance(Boolean isWin) {
		Bundle args = new Bundle();
		args.putSerializable(ARG_TITLE_TYPE, isWin);
		EndGameDialogFragment fragment = new EndGameDialogFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		Boolean state = (Boolean) getArguments().getSerializable(ARG_TITLE_TYPE);

		View view = LayoutInflater.from(getActivity())
				.inflate(R.layout.dialog_end_game, null);

		dialogTitle = view.findViewById(R.id.dialog_title);
		int resourceId = getResourceIdByState(state);
		dialogTitle.setText(resourceId);

		return new AlertDialog.Builder(getActivity())
				.setView(view)
				.setTitle(R.string.round_over)
				.setPositiveButton(android.R.string.ok, getOnClickListener())
				.create();
	}

	private int getResourceIdByState(Boolean isWin) {
		if (isWin) {
			return R.string.you_win;
		} else {
			return R.string.you_lose;
		}
	}

	private DialogInterface.OnClickListener getOnClickListener() {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO: send message to opponent
			}
		};
	}


}
