package ru.rienel.clicker.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import ru.rienel.clicker.ActivityWithNetwork;
import ru.rienel.clicker.common.ThreadPoolManager;
import ru.rienel.clicker.db.factory.domain.OpponentFactory;
import ru.rienel.clicker.service.runnable.SendStreamRunable;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class NetworkService extends Service implements ChannelListener, WifiP2pNetworkServiceListener {
	private static final String TAG = NetworkService.class.getName();

	private final IntentFilter intentFilter = new IntentFilter();

	private NetworkServiceBinder binder = new NetworkServiceBinder();
	private boolean retryChannel;
	private ThreadPoolManager threadPoolManager;
	private WifiP2pManager manager;

	private Channel channel;
	private BroadcastReceiver receiver;
	private List<WifiP2pDevice> p2pDevices = new ArrayList<>();
	private WifiP2pDevice localDevice;

	private ActivityWithNetwork activity;
	private NetworkServiceListener serviceListener;
	private CountDownLatch startRecvFileSignal;

	private boolean isP2pEnabled;

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

		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		channel = initialize(this, getMainLooper(), this);

		receiver = new WifiAppBroadcastReceiver(this, this);
		registerReceiver(receiver, intentFilter);

		try {
			threadPoolManager = new ThreadPoolManager(this, ConfigInfo.LISTEN_PORT, 5);
		} catch (IOException e) {
			Log.e(TAG, "onCreate: ", e);
		}
	}

	private Channel initialize(Context context, Looper looper, ChannelListener listener) {
		return manager.initialize(context, looper, listener);
	}

	private void initServiceThread() {
		Log.d(TAG, "initServiceThread: ");
		threadPoolManager.init();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy: started");
		super.onDestroy();
		threadPoolManager.myDestroy();
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

		if (manager != null && !retryChannel) {
			Toast.makeText(this, "Channel lost. Trying again",
					Toast.LENGTH_LONG).show();
			resetPeers();
			retryChannel = true;
			channel = initialize(this, getMainLooper(), this);
		} else {
			Toast.makeText(
					this,
					"Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
					Toast.LENGTH_LONG).show();
		}
	}

	public void resetPeers() {
		p2pDevices.clear();
		if (activity != null) {
			activity.resetPeers();
		}
	}

	public void registerActivity(ActivityWithNetwork activity) {
		this.activity = activity;
	}

	public void discoverPeers() {
		if (!isP2pEnabled) {
			Toast.makeText(this, "P2P off", Toast.LENGTH_SHORT).show();
		} else {
			manager.discoverPeers(channel, new ActionListener() {
				@Override
				public void onSuccess() {
					Toast.makeText(NetworkService.this,
							"Discovery Initiated",
							Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onFailure(int reason) {
					Toast.makeText(NetworkService.this,
							"Discovery Failed : " + reason,
							Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);

	}

	public void connect(WifiP2pConfig config) {
		manager.connect(channel, config, new ActionListener() {
			@Override
			public void onSuccess() {

			}

			@Override
			public void onFailure(int reason) {
				Toast.makeText(NetworkService.this, "Connect failed. Retry.",
						Toast.LENGTH_SHORT).show();
				Log.d(TAG, "onFailure: connect failed" + reason);
			}
		});
	}

	public void cancelDisconnect() {
		manager.cancelConnect(channel, new ActionListener() {
			@Override
			public void onSuccess() {
				Toast.makeText(NetworkService.this, "Aborting connection",
						Toast.LENGTH_SHORT).show();
				Log.d(TAG, "onSuccess: Aborting connection");
			}

			@Override
			public void onFailure(int reason) {
				Toast.makeText(
						NetworkService.this,
						"Connect abort request failed. Reason Code: "
								+ reason, Toast.LENGTH_SHORT).show();
				Log.d(TAG, "onFailure: connect abort request failed. Reason code" + reason);
			}
		});
	}

	public void requestPeers(PeerListListener listListener) {
		manager.requestPeers(channel, listListener);
	}

	public void requestConnectionInfo(ConnectionInfoListener infoListener) {
		manager.requestConnectionInfo(channel, infoListener);
	}

	public void removeGroup() {
		manager.removeGroup(channel, new ActionListener() {

			@Override
			public void onSuccess() {
//				((OpponentsActivity) activity).onDisconnect();
			}

			@Override
			public void onFailure(int reason) {
				Log.e(TAG, "Disconnect failed. Reason :" + reason);
			}
		});
	}

	public void postRecvPeerList(int count) {
		Message msg = new Message();
		msg.what = ConfigInfo.MSG_REPORT_RECV_PEER_LIST;
		msg.arg1 = count;
		activity.getHandler().sendMessage(msg);
	}

	public void postSendPeerInfoResult(int result) {
		Message msg = new Message();
		msg.what = ConfigInfo.MSG_REPORT_SEND_PEER_INFO_RESULT;
		msg.arg1 = result;
		activity.getHandler().sendMessage(msg);
	}

	public void bindListener(NetworkServiceListener listener) {
		serviceListener = listener;
	}

	@Override
	public void updateThisDevice(WifiP2pDevice device) {
		localDevice = device;
		if (activity != null) {
			activity.updateThisDevice(OpponentFactory.buildFromWifiP2pDevice(device));
		}
	}

	public String getFileInfo(Uri uri) throws IOException {
		String fileInfo = "size:" + 0 + "name: fadfas";
		return fileInfo;
	}


	public void handleSendStream(String host, int port, InputStream inputStream) {
		threadPoolManager.execute(new SendStreamRunable(host, port, inputStream, this));
	}

	public ActivityWithNetwork getActivity() {
		return activity;
	}

	public List<WifiP2pDevice> getP2pDevices() {
		return p2pDevices;
	}

	public WifiP2pManager getManager() {
		return manager;
	}

	public void setManager(WifiP2pManager manager) {
		this.manager = manager;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public BroadcastReceiver getReceiver() {
		return receiver;
	}

	public void setReceiver(BroadcastReceiver receiver) {
		this.receiver = receiver;
	}

	public IntentFilter getIntentFilter() {
		return intentFilter;
	}

	private void cleanupServiceThread() {
		threadPoolManager.cleanup();
	}

	public boolean isP2pEnabled() {
		return isP2pEnabled;
	}

	public void setP2pEnabled(boolean p2pEnabled) {
		isP2pEnabled = p2pEnabled;
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		final InetAddress groupOnwerAddress = info.groupOwnerAddress;

		if (info.groupFormed && info.isGroupOwner) {
//			connectionStatus.setText(R.string.host);
//			serverClass = new ServerClass();
//			serverClass.start();
		} else if (info.groupFormed) {
//			connectionStatus.setText(R.string.client);
//			clientClass = new ClientClass(groupOnwerAddress);
//			clientClass.start();
		}
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		int listSize = peers.getDeviceList().size();
		if (listSize == 0) {
			Log.d(TAG, "onPeersAvailable: Devices not found");
			Toast.makeText(getApplicationContext(), "No device found", Toast.LENGTH_LONG).show();
			p2pDevices.clear();
		}

		if (!p2pDevices.equals(peers.getDeviceList())) {
			Log.d(TAG, String.format("onPeersAvailable: Found %d devices", listSize));
			p2pDevices.clear();
			p2pDevices.addAll(peers.getDeviceList());
		}
		activity.updatePeers(
				new ArrayList<>(peers.getDeviceList()));
	}

	public class NetworkServiceBinder extends Binder {

		public NetworkService getService() {
			return NetworkService.this;
		}
	}
}