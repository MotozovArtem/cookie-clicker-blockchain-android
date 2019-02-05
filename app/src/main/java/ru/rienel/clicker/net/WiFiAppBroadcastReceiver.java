package ru.rienel.clicker.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;
import ru.rienel.clicker.activity.PeersListListenable;

public class WiFiAppBroadcastReceiver extends BroadcastReceiver {
	private WifiP2pManager manager;
	private WifiP2pManager.Channel channel;
	private PeersListListenable activity;

	public WiFiAppBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, PeersListListenable activity) {
		this.manager = manager;
		this.channel = channel;
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
				Toast.makeText(context, "Wifi is ON", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "Wifi is OFF", Toast.LENGTH_SHORT).show();
			}
		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
			if (manager != null) {
				manager.requestPeers(channel, activity.getPeerListListener());
			}
		} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
			if (manager == null) {
				return;
			}

			NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

			if (networkInfo.isConnected()) {
				manager.requestConnectionInfo(channel, activity.getConnectionInfoListener());
			}
		} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

		}
	}
}
