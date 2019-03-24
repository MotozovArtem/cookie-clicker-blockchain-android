package ru.rienel.clicker.activity.opponents;

import java.util.List;

import android.content.Context;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;

import ru.rienel.clicker.activity.BasePresenter;
import ru.rienel.clicker.activity.BaseView;
import ru.rienel.clicker.db.domain.Opponent;

public interface OpponentsContract {
	interface View extends BaseView<Presenter> {
		void updateUi();

		Context getContext();

		void showAcceptanceDialog(String opponentAddress);

		void updateOpponent(Opponent opponent);

		void updateOpponentList(List<Opponent> opponentList);

		List<Opponent> getOpponentList();

		void setOpponentList(List<Opponent> opponentList);

		void showErrorDialog(IllegalArgumentException error);
	}

	interface Presenter extends BasePresenter {
		void scanNetwork();

		ServiceConnection newServiceConnection();

		void handleOnOpponentListClick(Opponent opponent);

		void connect(WifiP2pConfig config);

		void handleCancelConnection(WifiP2pManager.ActionListener actionListener);

		void showAcceptanceDialog(String from);
	}
}
