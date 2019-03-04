package ru.rienel.clicker.activity.opponents;

import java.util.List;

import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pConfig;

import ru.rienel.clicker.activity.BasePresenter;
import ru.rienel.clicker.activity.BaseView;
import ru.rienel.clicker.db.domain.Opponent;

public interface OpponentsContract {
	interface View extends BaseView<Presenter> {
		void showOpponents();

		void updateOpponentsList(List<Opponent> opponentList);
	}

	interface Presenter extends BasePresenter {
		void scanNetwork();

		ServiceConnection newServiceConnection();

		void handleOnOpponentListClick(WifiP2pConfig config);
	}
}
