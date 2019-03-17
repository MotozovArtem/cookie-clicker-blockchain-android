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
		void showOpponents();

		void updateOpponentsList(List<Opponent> opponentList);

		Context getContext();

		void showAcceptanceDialog(String opponentName);
	}

	interface Presenter extends BasePresenter {
		void scanNetwork();

		ServiceConnection newServiceConnection();

		void handleOnOpponentListClick(WifiP2pConfig config, WifiP2pManager.ActionListener actionListener);

		void handleCancelConnection(WifiP2pManager.ActionListener actionListener);

		void showAcceptanceDialog(String deviceName);
	}
}
