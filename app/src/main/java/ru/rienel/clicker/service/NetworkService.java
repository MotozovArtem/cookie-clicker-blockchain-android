package ru.rienel.clicker.service;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import ru.rienel.clicker.common.PropertiesUpdatedName;

public class NetworkService extends Service implements ChannelListener, PeerListListener, ConnectionInfoListener {
	private static final String TAG = NetworkService.class.getName();

	private final IntentFilter intentFilter = new IntentFilter();

	private PropertyChangeSupport changeSupport;

	private NetworkServiceBinder binder = new NetworkServiceBinder();
	private WifiP2pManager wifiP2pManager;

	private Channel channel;
	private BroadcastReceiver receiver;
	private List<WifiP2pDevice> p2pDevices;

	private CountDownLatch startRecvFileSignal;

	private boolean isP2pEnabled;
	private boolean retryChannel;

	public static Intent newIntent(Context context) {
		return new Intent(context, NetworkService.class);
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate: started");
		super.onCreate();

		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		channel = initialize(this, getMainLooper(), this);

		receiver = new WifiAppBroadcastReceiver(this, this, this);
		registerReceiver(receiver, intentFilter);

		p2pDevices = new ArrayList<>();

		changeSupport = new PropertyChangeSupport(this);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy: started");
		super.onDestroy();
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind: started");
		return binder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind: started");
		unregisterReceiver(receiver);
		return super.onUnbind(intent);
	}

	@Override
	public void onChannelDisconnected() {

		if (wifiP2pManager != null && !retryChannel) {
			Toast.makeText(this, "Channel lost. Trying again",
					Toast.LENGTH_LONG).show();
			resetPeers();
			retryChannel = true;
			channel = initialize(this, getMainLooper(), this);
		} else {
			Toast.makeText(
					this,
					"Severe! Channel is probably lost permanently. Try Disable/Re-Enable P2P.",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		final InetAddress groupOwnerAddress = info.groupOwnerAddress;

//		changeSupport.firePropertyChange(PropertiesUpdatedName.CONNECTION_INFO, null, OpponentFactory.build(""));
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		int listSize = peers.getDeviceList().size();
		if (listSize == 0) {
			Log.d(TAG, "onPeersAvailable: Devices not found");
			Toast.makeText(getApplicationContext(), "No device found", Toast.LENGTH_LONG).show();
			List<WifiP2pDevice> oldDevices = new ArrayList<>(p2pDevices);

			p2pDevices.clear();

			changeSupport.firePropertyChange(PropertiesUpdatedName.P2P_DEVICES, oldDevices, p2pDevices);
		}

		if (!p2pDevices.equals(peers.getDeviceList())) {
			Log.d(TAG, String.format("onPeersAvailable: Found %d devices", listSize));
			List<WifiP2pDevice> oldDevices = new ArrayList<>(p2pDevices);

			p2pDevices.clear();
			p2pDevices.addAll(peers.getDeviceList());

			changeSupport.firePropertyChange(PropertiesUpdatedName.P2P_DEVICES, oldDevices, p2pDevices);
		}
	}

	private Channel initialize(Context context, Looper looper, ChannelListener listener) {
		return wifiP2pManager.initialize(context, looper, listener);
	}

	public void resetPeers() {
		List<WifiP2pDevice> devices = new ArrayList<>(p2pDevices);
		p2pDevices.clear();

		changeSupport.firePropertyChange("p2pDevices", devices, p2pDevices);
	}

	public void discoverPeers() {
		if (!isP2pEnabled) {
			Log.d(TAG, "discoverPeers: P2P off");
		} else {
			wifiP2pManager.discoverPeers(channel, new ActionListener() {
				@Override
				public void onSuccess() {
					Toast.makeText(NetworkService.this,
							"Discovery Initiated",
							Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onFailure(int reason) {
					Toast.makeText(NetworkService.this,
							String.format(Locale.ENGLISH, "Discovery Failed : %d", reason),
							Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	public void connect(WifiP2pConfig config, ActionListener actionListener) {
		wifiP2pManager.connect(channel, config, actionListener);
	}

	public void cancelDisconnect(ActionListener actionListener) {
		wifiP2pManager.cancelConnect(channel, actionListener);
	}

	public void requestPeers(PeerListListener listListener) {
		wifiP2pManager.requestPeers(channel, listListener);
	}

	public void requestConnectionInfo(ConnectionInfoListener infoListener) {
		wifiP2pManager.requestConnectionInfo(channel, infoListener);
	}

	public void removeGroup() {
		wifiP2pManager.removeGroup(channel, new ActionListener() {
			@Override
			public void onSuccess() {
			}

			@Override
			public void onFailure(int reason) {
				Log.e(TAG, String.format(Locale.ENGLISH, "Disconnect failed. Reason :%d", reason));
			}
		});
	}

	public List<WifiP2pDevice> getP2pDevices() {
		return p2pDevices;
	}

	public boolean isP2pEnabled() {
		return isP2pEnabled;
	}

	public void setP2pEnabled(boolean p2pEnabled) {
		isP2pEnabled = p2pEnabled;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	public class NetworkServiceBinder extends Binder {
		public NetworkService getService() {
			return NetworkService.this;
		}
	}

	public static class WifiAppBroadcastReceiver extends BroadcastReceiver {

		private static final String TAG = WifiAppBroadcastReceiver.class.getName();

		private NetworkService networkService;
		private WifiP2pManager.PeerListListener peerListListener;
		private WifiP2pManager.ConnectionInfoListener connectionInfoListener;

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
				NetworkInfo networkInfo = intent
						.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
				if (networkInfo.isConnected()) {
					networkService.requestConnectionInfo(connectionInfoListener);
				} else {
					networkService.resetPeers();
					networkService.discoverPeers();
				}
				Log.d(TAG, String.format("P2P connection changed - networkInfo:%s", networkInfo.toString()));
			} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
				WifiP2pDevice wifiP2pDevice = intent.getParcelableExtra(
						WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
				Log.d(TAG, String.format("P2P this device changed - wifiP2pDevice:%s", wifiP2pDevice.toString()));
			} else {
				Log.d(TAG, String.format("Unmatched P2P change action - %s", action));
			}
		}

		public WifiAppBroadcastReceiver(NetworkService service,
		                                WifiP2pManager.PeerListListener peerListListener,
		                                WifiP2pManager.ConnectionInfoListener connectionInfoListener) {
			super();
			this.networkService = service;
			this.peerListListener = peerListListener;
			this.connectionInfoListener = connectionInfoListener;
		}
	}
}