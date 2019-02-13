package ru.rienel.clicker.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class WifiAppBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = WifiAppBroadcastReceiver.class.getName();

	private NetworkService service;
	private WifiP2pNetworkServiceListener serviceListener;

	public WifiAppBroadcastReceiver(NetworkService service, WifiP2pNetworkServiceListener serviceListener) {
		super();
		this.service = service;
		this.serviceListener = serviceListener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
				service.setP2pEnabled(true);
				service.discoverPeers();
			} else {
				service.setP2pEnabled(false);
				service.resetPeers();

			}
			Log.d(TAG, "P2P state changed - state:" + state);
		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
			if (service.isWifiP2pAvailable()) {
				service.requestPeers(serviceListener);

			}
			Log.d(TAG, "P2P peers changed");
		} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
			if (!service.isWifiP2pAvailable()) {
				return;
			}

			NetworkInfo networkInfo = intent
					.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

			if (networkInfo.isConnected()) {
				service.requestConnectionInfo(serviceListener);
			} else {
				service.resetPeers();
				service.discoverPeers();
			}
			Log.d(TAG, "P2P connection changed - networkInfo:" + networkInfo.toString());
		} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
			WifiP2pDevice wifiP2pDevice = intent.getParcelableExtra(
					WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
			service.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
					WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
			Log.d(TAG, "P2P this device changed - wifiP2pDevice:" + wifiP2pDevice.toString());
		} else {
			Log.d(TAG, "Unmatched P2P change action - " + action);
		}
	}
}
