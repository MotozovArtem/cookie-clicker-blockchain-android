package ru.rienel.clicker.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import ru.rienel.clicker.db.domain.Opponent;
import ru.rienel.clicker.db.factory.domain.OpponentFactory;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static android.os.Looper.getMainLooper;

public class WifiP2pHelper {
	private static final int MESSAGE_READ = 1;

	private Context context;
	private WifiManager wifiManager;
	private WifiP2pManager.Channel channel;
	private WifiP2pManager wifiP2pManager;

	private List<WifiP2pDevice> peers = new ArrayList<>();
	private List<Opponent> opponents = new ArrayList<>();

	private WiFiAppBroadcastReceiver receiver;
	private IntentFilter intentFilter;

	public WifiP2pHelper(Context context) {
		this.context = context;
		initialWork();
	}

	public List<Opponent> getOpponents() {
		return opponents;
	}

	public void setOpponents(List<Opponent> opponents) {
		this.opponents = opponents;
	}

	public WiFiAppBroadcastReceiver getReceiver() {
		return receiver;
	}

	public IntentFilter getIntentFilter() {
		return intentFilter;
	}

	private void initialWork() {

		wifiManager =
				(WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		wifiP2pManager =
				(WifiP2pManager) context.getApplicationContext().getSystemService(Context.WIFI_P2P_SERVICE);

		channel = wifiP2pManager.initialize(context, getMainLooper(), null);

		receiver = new WiFiAppBroadcastReceiver(wifiP2pManager, channel);

		intentFilter = new IntentFilter();
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	}

	private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
		@Override
		public void onPeersAvailable(WifiP2pDeviceList peersList) {
			if (!peersList.getDeviceList().equals(peers)) {
				peers.clear();
				peers.addAll(peersList.getDeviceList());
				for (WifiP2pDevice device : peersList.getDeviceList()) {
					opponents.add(OpponentFactory.buildFromWifiP2pDevice(device));
				}
			}
		}
	};

	private WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
		@Override
		public void onConnectionInfoAvailable(WifiP2pInfo info) {
			final InetAddress groupOwnerAddress = info.groupOwnerAddress;

			if (info.groupFormed && info.isGroupOwner) {
//				connectionStatus.setText(R.string.host);
//				serverClass = new ServerClass();
//				serverClass.start();
			} else if (info.groupFormed) {
//				connectionStatus.setText(R.string.client);
//				clientClass = new ClientClass(groupOnwerAddress);
//				clientClass.start();

			}
		}
	};


	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGE_READ:
					byte[] readBuff = (byte[]) msg.obj;
					String tempMessage = new String(readBuff, 0, msg.arg1);
//					readMsgBox.setText(tempMessage);
					break;
			}
			return true;
		}
	});

	public void discoverPeers() {
		wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
			@Override
			public void onSuccess() {
				Toast.makeText(context, "Discovery started", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int reason) {
				Toast.makeText(context, "Discovery failed", Toast.LENGTH_SHORT).show();
			}
		});
	}


	public class WiFiAppBroadcastReceiver extends BroadcastReceiver {
		private WifiP2pManager manager;
		private WifiP2pManager.Channel channel;

		public WiFiAppBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel) {
			this.manager = manager;
			this.channel = channel;
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
					manager.requestPeers(channel, peerListListener);
				}
			} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
				if (manager == null) {
					return;
				}

				NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

				if (networkInfo.isConnected()) {
					manager.requestConnectionInfo(channel, connectionInfoListener);
				}
			} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

			}
		}
	}

}