package ru.rienel.clicker.activity.opponents;

import android.content.ServiceConnection;

import ru.rienel.clicker.activity.BasePresenter;
import ru.rienel.clicker.activity.BaseView;
import ru.rienel.clicker.service.NetworkService;

public interface OpponentsContract {
	interface View extends BaseView<Presenter> {
		void showOpponents();
	}

	interface Presenter extends BasePresenter {
		void scanNetwork();

		NetworkService getNetworkService();

		ServiceConnection getServiceConnection();
	}
}
