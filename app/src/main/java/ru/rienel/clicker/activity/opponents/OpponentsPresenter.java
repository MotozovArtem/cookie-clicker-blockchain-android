package ru.rienel.clicker.activity.opponents;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import android.util.Log;

import ru.rienel.clicker.common.Configuration;
import ru.rienel.clicker.common.Preconditions;
import ru.rienel.clicker.common.PropertiesUpdatedName;
import ru.rienel.clicker.db.domain.Opponent;
import ru.rienel.clicker.db.factory.domain.OpponentFactory;
import ru.rienel.clicker.net.Client;
import ru.rienel.clicker.net.Server;
import ru.rienel.clicker.net.model.OpponentDto;
import ru.rienel.clicker.net.model.Signal;
import ru.rienel.clicker.service.NetworkService;

public class OpponentsPresenter implements OpponentsContract.Presenter, PropertyChangeListener {
	private static final String TAG = OpponentsPresenter.class.getName();

	private NetworkService networkService;
	private OpponentsContract.View opponentsView;

	private Server server;
	private Client client;
	private ExecutorService executor = Executors.newFixedThreadPool(2);

	public OpponentsPresenter(OpponentsContract.View opponentsView, Server server) {
		Preconditions.checkNotNull(opponentsView);

		this.opponentsView = opponentsView;

		this.server = server;
		server.addListener(new ConnectionListener());
		executor.execute(server);

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
		OpponentDto opponentDto = OpponentDto.newFromOpponent(opponent);
		Signal signal = new Signal("connect", Signal.SignalType.INVITE, opponentDto);
		try {
			client = new Client(opponent.getIpAddress().getHostAddress(), Configuration.SERVER_PORT);
			executor.execute(client);
			client.sendSignal(signal);
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
	public void showAcceptanceDialog(Opponent opponent) {
		opponentsView.showAcceptanceDialog(opponent);
	}

	@Override
	public void discardInvitation() {

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

	public class ConnectionListener implements Server.ServerConnectionListener {
		@Override
		public void connected(Server.ServerConnectionEvent event) {
			Signal signal = event.getSignal();
			OpponentDto dto = signal.getOpponent();
			try {
				opponentsView.showAcceptanceDialog(OpponentFactory.buildFromDto(dto));
			} catch (UnknownHostException e) {
				Log.e(TAG, "connected:", e);
			}
		}

		@Override
		public void disconnected(Server.ServerConnectionEvent event) {
			// TODO: Add disconnect event logic
		}

		@Override
		public void receivedSignal(Server.ServerConnectionEvent event) {
			Signal signal = event.getSignal();
			OpponentDto dto = signal.getOpponent();
			try {
				opponentsView.showAcceptanceDialog(OpponentFactory.buildFromDto(dto));
			} catch (UnknownHostException e) {
				Log.e(TAG, "connected:", e);
			}
		}
	}
}
