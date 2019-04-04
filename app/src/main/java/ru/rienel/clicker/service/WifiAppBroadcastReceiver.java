package ru.rienel.clicker.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class WifiAppBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = WifiAppBroadcastReceiver.class.getName();

	private NetworkService networkService;
	private WifiP2pManager.PeerListListener peerListListener;

	public WifiAppBroadcastReceiver(NetworkService service,
	                                WifiP2pManager.PeerListListener peerListListener) {
		super();
		this.networkService = service;
		this.peerListListener = peerListListener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
				networkService.setP2pEnabled(true);
				networkService.discoverPeers();
			} else {
				networkService.setP2pEnabled(false);
				networkService.resetPeers();
			}

			Log.d(TAG, String.format("P2P state changed - state:%d", state));
		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
			if (networkService.isP2pEnabled()) {
				networkService.requestPeers(peerListListener);
			}

			Log.d(TAG, "P2P peers changed");
		} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
			if (!networkService.isP2pEnabled()) {
				return;
			}

			NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
			if (networkInfo.isConnected()) {
				networkService.requestConnectionInfo(newConnectionInfoListener());
			} else {
				networkService.resetPeers();
				networkService.discoverPeers();
			}
			Log.d(TAG, String.format("P2P connection changed - networkInfo:%s", networkInfo.toString()));
		} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
			WifiP2pDevice wifiP2pDevice = intent.getParcelableExtra(
					WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
			networkService.setLocalDeviceName(wifiP2pDevice.deviceName);
			networkService.setLocalDeviceAddress(wifiP2pDevice.deviceAddress);
			Log.d(TAG, String.format("P2P this device changed - wifiP2pDevice:%s", wifiP2pDevice.toString()));
		} else {
			Log.d(TAG, String.format("Unmatched P2P change action - %s", action));
		}
	}

	private WifiP2pManager.ConnectionInfoListener newConnectionInfoListener() {
		return new WifiP2pManager.ConnectionInfoListener() {
			@Override
			public void onConnectionInfoAvailable(WifiP2pInfo info) {
				Log.d(TAG, "onConnectionInfoAvailable: " + info);
			}
		};
	}
}