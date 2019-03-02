package ru.rienel.clicker.activity.opponents;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import ru.rienel.clicker.common.Preconditions;
import ru.rienel.clicker.service.NetworkService;

public class OpponentsPresenter implements OpponentsContract.Presenter {
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

	}

	@Override
	public NetworkService getNetworkService() {
		return networkService;
	}

	@Override
	public ServiceConnection getServiceConnection() {
		return new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.d(TAG, "onServiceConnected: called");
				NetworkService.NetworkServiceBinder binder = (NetworkService.NetworkServiceBinder) service;
				networkService = binder.getService();
				Log.d(TAG, "onServiceConnected: connected to service");
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.d(TAG, "onServiceDisconnected: called");
			}
		};
	}

	/*
		@Override
	public void updatePeers(List<WifiP2pDevice> wifiP2pDeviceList) {
		List<Opponent> opponentList = new ArrayList<>(wifiP2pDeviceList.size());
		for (WifiP2pDevice device : wifiP2pDeviceList) {
			opponentList.add(OpponentFactory.buildFromWifiP2pDevice(device));
		}
		if (opponentListFragment == null) {
			opponentListFragment = getFragment();
		}
		opponentListFragment.updateOpponentList(opponentList);
	}
	 */
}
