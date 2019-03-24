package ru.rienel.clicker.activity.opponents;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import ru.rienel.clicker.common.Preconditions;
import ru.rienel.clicker.common.PropertiesUpdatedName;
import ru.rienel.clicker.db.domain.Opponent;
import ru.rienel.clicker.net.Signal;
import ru.rienel.clicker.net.task.ClientAsyncTask;
import ru.rienel.clicker.net.task.ServerAsyncTask;
import ru.rienel.clicker.service.NetworkService;

public class OpponentsPresenter implements OpponentsContract.Presenter, PropertyChangeListener {
	private static final String TAG = OpponentsPresenter.class.getName();

	private NetworkService networkService;
	private OpponentsContract.View opponentsView;
	private ServerAsyncTask server;
	private ClientAsyncTask client;

	public OpponentsPresenter(OpponentsContract.View opponentsView) {
		Preconditions.checkNotNull(opponentsView);

		this.opponentsView = opponentsView;
		opponentsView.setPresenter(this);

		this.server = new ServerAsyncTask(this, null);
		server.execute();
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
				NetworkService.NetworkServiceBinder binder = (NetworkService.NetworkServiceBinder)service;
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
	public void propertyChange(PropertyChangeEvent event) {
		if (PropertiesUpdatedName.P2P_DEVICES.equals(event.getPropertyName())) {
			this.updateOpponents();
		}
	}

	@Override
	public void handleOnOpponentListClick(Opponent opponent) {
		Preconditions.checkNotNull(opponent);
		Signal signal = new Signal("connect", Signal.SignalType.INVITE, opponent.getAddress());

		// TODO catch Illegal argument exception and show dialog
		try {
			client = new ClientAsyncTask(opponent.getIpAddress(), signal);
			client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} catch (IllegalArgumentException e) {
			opponentsView.showErrorDialog(e);
		}
	}

	@Override
	public void connect(WifiP2pConfig config) {
		Preconditions.checkNotNull(config);

		networkService.connect(config, newConnectionListener());
	}

	private WifiP2pManager.ActionListener newConnectionListener() {
		return new WifiP2pManager.ActionListener() {
			@Override
			public void onSuccess() {
				Log.d(TAG, "onSuccess: Connection successful");
			}

			@Override
			public void onFailure(int reason) {
				Log.d(TAG, "onFailure: Connection failed");
			}
		};
	}

	@Override
	public void handleCancelConnection(WifiP2pManager.ActionListener actionListener) {
		networkService.cancelDisconnect(actionListener);
	}

	@Override
	public void showAcceptanceDialog(String from) {
		opponentsView.showAcceptanceDialog(from);
	}

	private void updateOpponents() {
		List<WifiP2pDevice> devices = networkService.getP2pDevices();

		List<Opponent> opponentList = new ArrayList<>(devices.size());
		for (WifiP2pDevice device : devices) {
			Opponent opponent = new Opponent();
			opponent.setName(device.deviceName);
			opponent.setAddress(device.deviceAddress);
			networkService.requestConnectionInfo(info -> {
				opponent.setIpAddress(info.groupOwnerAddress);
				opponentsView.updateUi();
			});
			opponentList.add(opponent);
		}

		opponentsView.updateOpponentList(opponentList);
	}
}
