package ru.rienel.clicker.activity.opponents;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.IBinder;
import android.util.Log;

import ru.rienel.clicker.common.Preconditions;
import ru.rienel.clicker.common.PropertiesUpdatedName;
import ru.rienel.clicker.db.domain.Opponent;
import ru.rienel.clicker.service.NetworkService;

public class OpponentsPresenter implements OpponentsContract.Presenter, PropertyChangeListener {
	private static final String TAG = OpponentsPresenter.class.getName();

	private NetworkService networkService;
	private OpponentsContract.View opponentsView;

	public OpponentsPresenter(OpponentsContract.View opponentsView) {
		Preconditions.checkNotNull(opponentsView);

		this.opponentsView = opponentsView;

		opponentsView.setPresenter(this);
	}

	@Override
	public void start() {
	}

	@Override
	public void scanNetwork() {
		networkService.discoverPeers();
	}

	@Override
	public ServiceConnection newServiceConnection() {
		return new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.d(TAG, "onServiceConnected: called");
				NetworkService.NetworkServiceBinder binder = (NetworkService.NetworkServiceBinder) service;
				networkService = binder.getService();
				networkService.addPropertyChangeListener(OpponentsPresenter.this);
				Log.d(TAG, "onServiceConnected: connected to service");
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.d(TAG, "onServiceDisconnected: called");
			}
		};
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
			case PropertiesUpdatedName.P2P_DEVICES:
				this.updateOpponents();
				break;
			case PropertiesUpdatedName.CONNECTION_INFO:
				// TODO Update concrete opponent IP address (comes from WifiP2pInfo)
				break;
		}
	}

	private void updateOpponents() {
		List<WifiP2pDevice> devices = networkService.getP2pDevices();

		List<Opponent> opponentList = new ArrayList<>(devices.size());
		for (WifiP2pDevice device : devices) {
			Opponent opponent = new Opponent();
			opponent.setName(device.deviceName);
			opponent.setMacAddress(device.deviceAddress);
			networkService.requestConnectionInfo(info -> {
				opponent.setIpAddress(info.groupOwnerAddress);
			});
			opponentList.add(opponent);
		}
		opponentsView.updateOpponentsList(opponentList);
		opponentsView.showOpponents();
	}

	@Override
	public void handleOnOpponentListClick(WifiP2pConfig config) {
		Preconditions.checkNotNull(config);

		networkService.connect(config);
	}
}
