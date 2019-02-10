package ru.rienel.clicker.activity;

import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;
import ru.rienel.clicker.R;

public class NetworkActivity extends AppCompatActivity implements PeersListListenable {
//	private WiFiAppBroadcastReceiver receiver;
	private WifiP2pManager manager;
	private WifiP2pManager.Channel channel;

	private final IntentFilter intentFilter = new IntentFilter();

	private ListView listView;
	private Button onOff;
	private Button discovery;
	private Button send;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network);

		intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

//		receiver = new WiFiAppBroadcastReceiver(manager, channel, this);
//		registerReceiver(receiver, intentFilter);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		receiver = new WiFiAppBroadcastReceiver(manager, channel, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
//		unregisterReceiver(receiver);
	}

	@Override
	public WifiP2pManager.PeerListListener getPeerListListener() {
		return null;
	}

	@Override
	public WifiP2pManager.ConnectionInfoListener getConnectionInfoListener() {
		return null;
	}
}
