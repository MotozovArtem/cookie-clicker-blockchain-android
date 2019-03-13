package ru.rienel.clicker.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ru.rienel.clicker.R;
import ru.rienel.clicker.activity.opponents.OpponentsContract;

public class WaitingAcceptanceDialogFragment extends DialogFragment {
	public static final String TAG = WaitingAcceptanceDialogFragment.class.getName();

	private ImageView donutIcon;
	private TextView waitingMessage;
	private OpponentsContract.Presenter presenter;
	private WifiP2pConfig p2pConfig;

	public static WaitingAcceptanceDialogFragment newInstance(OpponentsContract.Presenter presenter, WifiP2pConfig p2pConfig) {
		WaitingAcceptanceDialogFragment dialog = new WaitingAcceptanceDialogFragment();
		dialog.setPresenter(presenter);
		dialog.setWifiP2pConfig(p2pConfig);
		return dialog;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		View root = LayoutInflater.from(getActivity())
				.inflate(R.layout.dialog_waiting_acceptance, null);

		donutIcon = root.findViewById(R.id.dialog_donut);
		waitingMessage = root.findViewById(R.id.dialog_waiting_message);

		donutIcon.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.dialog_donut_rotate));

		presenter.handleOnOpponentListClick(p2pConfig, newConnectionActionListener());


		return new AlertDialog.Builder(getActivity())
				.setView(root)
				.setCancelable(true)
				.setNegativeButton(R.string.cancel, (dialog, which) -> {
					presenter.handleCancelConnection(newCancelConnectionActionListener());
					dialog.cancel();
				})
				.create();
	}

	private void setPresenter(OpponentsContract.Presenter presenter) {
		this.presenter = presenter;
	}

	private void setWifiP2pConfig(WifiP2pConfig p2pConfig) {
		this.p2pConfig = p2pConfig;
	}

	private WifiP2pManager.ActionListener newConnectionActionListener() {
		return new WifiP2pManager.ActionListener() {
			@Override
			public void onSuccess() {
				Toast.makeText(WaitingAcceptanceDialogFragment.this.getContext(),
						"Connection successful", Toast.LENGTH_SHORT).show();
				Log.i(TAG, "onSuccess: Connection successful");
			}

			@Override
			public void onFailure(int reason) {
				Toast.makeText(WaitingAcceptanceDialogFragment.this.getContext(),
						"Connection failed", Toast.LENGTH_SHORT).show();
				Log.i(TAG, "onSuccess: Connection failed");
			}
		};
	}

	private WifiP2pManager.ActionListener newCancelConnectionActionListener() {
		return new WifiP2pManager.ActionListener() {
			@Override
			public void onSuccess() {
				Log.i(TAG, "onSuccess: Cancel connection successful");
			}

			@Override
			public void onFailure(int reason) {
				Log.i(TAG, "onSuccess: Cancel connection failed");
			}
		};
	}
}
