package ru.rienel.clicker.service;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
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
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import ru.rienel.clicker.activity.OpponentsActivity;
import ru.rienel.clicker.common.Preconditions;
import ru.rienel.clicker.common.ThreadPoolManager;
import ru.rienel.clicker.common.Utility;
import ru.rienel.clicker.db.factory.domain.OpponentFactory;
import ru.rienel.clicker.service.runnable.SendFileRunable;
import ru.rienel.clicker.service.runnable.SendPeerInfoRunable;
import ru.rienel.clicker.service.runnable.SendStreamRunable;
import ru.rienel.clicker.service.runnable.SendStringRunable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class NetworkService extends Service implements ChannelListener, WifiP2pNetworkServiceListener {
	private static final String TAG = NetworkService.class.getName();

	private boolean retryChannel;
	private NetworkServiceBinder binder = new NetworkServiceBinder();
	private ThreadPoolManager threadPoolManager;
	private WifiP2pManager manager;
	private WifiP2pInfo wifiP2pInfo;

	private Channel channel;
	private BroadcastReceiver receiver;
	private final IntentFilter intentFilter = new IntentFilter();
	private List<WifiP2pDevice> p2pDevices = new ArrayList<>();
	private List<PeerInfo> peerInfoList = new ArrayList<>();
	private OpponentsActivity activity;
	private NetworkServiceListener serviceListener;
	private boolean isP2pEnabled;
	private boolean verifyReceivedFile;
	private SocketAddress remoteSockAddr;
	private CountDownLatch startRecvFileSignal;
	private WifiP2pDevice localDevice;

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
		if (isWifiP2pManager() && !retryChannel) {
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

	public String hostAddress() {
		return wifiP2pInfo.groupOwnerAddress.getHostAddress();
	}

	public boolean isPeer() {
		return !wifiP2pInfo.isGroupOwner;
	}

	public boolean isGroupOwner() {
		return wifiP2pInfo.isGroupOwner;
	}


	public void bindActivity(Activity activity) {
//		this.activity = activity;
		if (localDevice != null) {
			updateThisDevice(localDevice);
		}
		discoverPeers();
	}

	public boolean discoverPeers() {
		if (activity != null) {
			if (!isP2pEnabled) {
				Toast.makeText(this, "P2P off", Toast.LENGTH_SHORT).show();
			} else {
//				((OpponentsActivity) activity).showDiscoverPeers();
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
		return false;
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

	public void postSendStringResult(int sendBytes) {
		Message msg = new Message();
		msg.what = ConfigInfo.MSG_SEND_STRING;
		msg.arg1 = sendBytes;
		activity.getHandler().sendMessage(msg);
	}

	public void postSendRecvBytes(int sendBytes, int receiveBytes) {
		Message msg = new Message();
		msg.what = ConfigInfo.MSG_SEND_RECV_FILE_BYTES;
		msg.arg1 = sendBytes;
		msg.arg2 = receiveBytes;
		activity.getHandler().sendMessage(msg);
	}

	public void postSendPeerInfoResult(int result) {
		Message msg = new Message();
		msg.what = ConfigInfo.MSG_REPORT_SEND_PEER_INFO_RESULT;
		msg.arg1 = result;
		activity.getHandler().sendMessage(msg);
	}

	public void postRecvPeerInfo(PeerInfo info) {
		Message msg = new Message();
		msg.what = ConfigInfo.MSG_RECV_PEER_INFO;
		activity.getHandler().sendMessage(msg);
	}

	public void postVerifyRecvFile() {
		Message msg = new Message();
		msg.what = ConfigInfo.MSG_VERIFY_RECV_FILE_DIALOG;
		activity.getHandler().sendMessage(msg);
	}

	public void postRecvFileResult(int result) {
		Message msg = new Message();
		msg.what = ConfigInfo.MSG_REPORT_RECV_FILE_RESULT;
		msg.arg1 = result;
		activity.getHandler().sendMessage(msg);
	}

	public void postSendFileResult(int result) {
		Message msg = new Message();
		msg.what = ConfigInfo.MSG_REPORT_SEND_FILE_RESULT;
		msg.arg1 = result;
		activity.getHandler().sendMessage(msg);
	}

	public void postSendStreamResult(int result) {
		Message msg = new Message();
		msg.what = ConfigInfo.MSG_REPORT_SEND_STREAM_RESULT;
		msg.arg1 = result;
		activity.getHandler().sendMessage(msg);
	}

	public void bindListener(NetworkServiceListener listener) {
		serviceListener = listener;
	}

	@Override
	public void updateThisDevice(WifiP2pDevice device) {
		localDevice = device;
		if (activity != null)
			activity.updateThisDevice(OpponentFactory.buildFromWifiP2pDevice(device));
	}

	public void handleSendFile() {

	}

	public String getFileInfo(Uri uri) throws IOException {
//		Pair<String, Integer> pair = Utility.getFileNameAndSize(getActivity(), uri);
//		String name = pair.first;
//		long size = pair.second;
//		getActivity().setSendFileSize(size);
//		getActivity().setSendFileName(name);
		String fileInfo = "size:" + 0 + "name: fadfas";
		return fileInfo;
	}

	public InputStream getInputStream(Uri uri) throws FileNotFoundException {
		ContentResolver cr = getActivity().getContentResolver();
		return cr.openInputStream(uri);
	}

	public void handleRecvFile(InputStream ins) {
		handleRecvFileInfo(ins);

		String extName = ".jpg";
//		if (!activity.recvFileName().isEmpty()) {
//			int dotIndex = activity.recvFileName().lastIndexOf(".");
//			if (dotIndex != -1
//					&& dotIndex != activity.recvFileName().length() - 1) {
//				extName = activity.recvFileName().substring(dotIndex);
//			}
//		}
//		Log.d(TAG, "activity.recvFileName():" + activity.recvFileName()
//				+ " extName:" + extName);

		if (waitForVerifyRecvFile() && isVerifyReceivedFile()) {
			recvFileAndSave(ins, extName);
		} else
			postRecvFileResult(-1);
	}

	public void verifyRecvFile() {
		Preconditions.notNull(startRecvFileSignal);
		startRecvFileSignal.countDown();
	}

	private boolean waitForVerifyRecvFile() {
		try {
			startRecvFileSignal = new CountDownLatch(1);
			boolean res = startRecvFileSignal.await(10, TimeUnit.SECONDS);
			return res;
		} catch (InterruptedException e) {
			Log.e(this.getClass().getName(), "waitForVerifyRecvFile e:", e);
			e.printStackTrace();
			return false;
		}
	}

	public boolean handleRecvPeerList(InputStream ins) {
		try {
			peerInfoList.clear();
			int peerListSize = ins.read();
			for (int i = 0; i < peerListSize; ++i) {
				int bufferLen = ins.read();
				byte[] buffer = new byte[256];
				ins.read(buffer, 0, bufferLen);
				String strBuffer = new String(buffer, 0, bufferLen);
				int offset1 = strBuffer.indexOf("peer:");
				int offset2 = strBuffer.indexOf("port:");
				Log.d(OpponentsActivity.TAG, "recvPeerSockAddr strBuffer:"
						+ strBuffer);
				if (offset1 != -1 && offset2 != -1) {
					Preconditions.isTrue(offset1 < offset2);
					String host = strBuffer.substring(offset1 + 5, offset2);
					int port = Integer.parseInt(strBuffer.substring(offset2 + 5));
					peerInfoList.add(new PeerInfo(host, port));
					Log.d(OpponentsActivity.TAG, "peerInfoList.add(...). size:"
							+ peerInfoList.size());
				}
			}
			postRecvPeerList(peerInfoList.size());
			return true;
		} catch (IOException e) {
			Log.e(OpponentsActivity.TAG, e.getMessage());
			return false;
		}
	}

	public boolean handleRecvFileInfo(InputStream inputStream) {
//		activity.resetRecvFileInfo();
		try {
			int streamSize = inputStream.read();
			byte[] buffer = new byte[streamSize];
			int len = inputStream.read(buffer, 0, streamSize);
			String stringBuffer = new String(buffer, 0, len);
			Preconditions.equals(stringBuffer.length(), streamSize);
			int offset1 = stringBuffer.indexOf("size:");
			int offset2 = stringBuffer.indexOf("name:");
			Log.d(OpponentsActivity.TAG, "recvDistFileInfo buffer:"
					+ stringBuffer);
			if (offset1 != -1 && offset2 != -1) {
				Preconditions.isTrue(offset1 < offset2);
				String strSize = stringBuffer.substring(offset1 + 5, offset2);
//				activity.setRecvFileSize(Long.parseLong(strSize));
//				activity.setRecvFileName(stringBuffer.substring(offset2 + 5));

				Log.d(OpponentsActivity.TAG,
						"iFileSize:" +
								Integer.parseInt(strSize) +
								" strFileName:" +
								stringBuffer.substring(offset2 + 5));
				postVerifyRecvFile();
				return true;
			}
			return false;
		} catch (IOException e) {
			Log.e(OpponentsActivity.TAG, e.getMessage());
			return false;
		}
	}

	public boolean handleRecvPeerInfo(InputStream ins) {
		try {
			StringBuilder strBuffer = new StringBuilder();
			byte[] buffer = new byte[1024];
			int len;
			while ((len = ins.read(buffer)) != -1) {
				strBuffer.append(new String(buffer, 0, len));
			}

			int offset1 = strBuffer.indexOf("peer:");
			int offset2 = strBuffer.indexOf("port:");
			Log.d(OpponentsActivity.TAG, "recvPeerSockAddr strBuffer:"
					+ strBuffer);
			if (offset1 != -1 && offset2 != -1) {
				Preconditions.isTrue(offset1 < offset2);
				String host = strBuffer.substring(offset1 + 5, offset2);
				int port = Integer.parseInt(strBuffer.substring(offset2 + 5
				));
				Log.d(OpponentsActivity.TAG, "new host:" + host);

				PeerInfo info = new PeerInfo(host, port);
				postRecvPeerInfo(info);
				for (Iterator<PeerInfo> iter = peerInfoList.iterator(); iter.hasNext(); ) {
					PeerInfo peer = iter.next();
					Log.d(OpponentsActivity.TAG, "host:" + peer.host
							+ " port:" + peer.port);
					if (peer.host.equals(host))
						return true;
				}
				peerInfoList.add(info);
				Log.d(OpponentsActivity.TAG, "peerInfoList.add(...). size:"
						+ peerInfoList.size());
			}
			return true;
		} catch (IOException e) {
			Log.e(OpponentsActivity.TAG, e.getMessage());
			return false;
		}
	}

	public boolean recvFileAndSave(InputStream ins, String extName) {
		try {
			final File recvFile = new File(
					Environment.getExternalStorageDirectory()
							+ "/wifi-direct/wifip2pshared-"
							+ System.currentTimeMillis() + extName);

			File dirs = new File(recvFile.getParent());
			if (!dirs.exists()) {
				dirs.mkdirs();
			}
			recvFile.createNewFile();

			Log.d(OpponentsActivity.TAG,
					"server: copying files " + recvFile.toString());
			FileOutputStream fileOutS = new FileOutputStream(recvFile);

			byte[] buf = new byte[1024];
			int len;
			while ((len = ins.read(buf)) != -1) {
				fileOutS.write(buf, 0, len);
				postSendRecvBytes(0, len);

			}
			fileOutS.close();
			String strFile = recvFile.getAbsolutePath();
			if (strFile != null) {
				Utility.openFile(activity, recvFile);
			}
			return true;
		} catch (IOException e) {
			Log.e(OpponentsActivity.TAG, "IOException", e);
			e.printStackTrace();
			return false;
		}
	}

	public void handleSendPeerInfo() {
		threadPoolManager.execute(new SendPeerInfoRunable(
				new PeerInfo(hostAddress(), ConfigInfo.LISTEN_PORT),
				this));
	}

	public void handleSendFile(String host, int port, Uri uri) {
		Log.d(this.getClass().getName(), "handleSendFile");
		threadPoolManager.execute(new SendFileRunable(host, port, uri, this));
	}

	public void handleBroadcastPeerList() {
		if (isGroupOwner()) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(peerInfoList.size());
			for (PeerInfo peerInfo : peerInfoList) {
				String tmp = peerInfo.toString();
				outputStream.write(tmp.length());
				try {
					outputStream.write(tmp.getBytes());
				} catch (IOException e) {
					Log.e(TAG, " e:" + e);
					e.printStackTrace();
				}
			}

			Log.d(TAG, " outputStream:" + outputStream);
			ByteArrayInputStream ins = new ByteArrayInputStream(outputStream.toByteArray());
			Log.d(TAG, " ins's length:" + ins.available());
			for (PeerInfo peerInfo : peerInfoList) {
				handleSendStream(peerInfo.host, peerInfo.port, ins);
			}
		}
	}

	public void handleSendStream(String host, int port, InputStream inputStream) {
		threadPoolManager.execute(new SendStreamRunable(host, port, inputStream, this));
	}

	public void handleSendString(String host, int port, String data) {
		threadPoolManager.execute(new SendStringRunable(host, port, data, this));
	}

	public Context getActivity() {
		return activity;
	}

	public List<PeerInfo> getPeerInfoList() {
		return peerInfoList;
	}

	public boolean isVerifyReceivedFile() {
		return verifyReceivedFile;
	}

	public void setVerifyReceivedFile(boolean verifyReceivedFile) {
		this.verifyReceivedFile = verifyReceivedFile;
	}

	public List<WifiP2pDevice> getP2pDevices() {
		return p2pDevices;
	}

	public boolean isWifiP2pAvailable() {
		return manager != null;
	}

	public boolean isWifiP2pManager() {
		return manager != null;
	}

	public boolean isWifiP2pChannel() {
		return channel != null;
	}

	public void setRemoteSockAddress(SocketAddress sockAddr) {
		remoteSockAddr = sockAddr;
	}

	public SocketAddress getRemoteSockAddress() {
		return remoteSockAddr;
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {

	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {

	}

	public class PeerInfo {
		public String host;
		public Integer port;

		public PeerInfo(String host, Integer port) {
			this.host = host;
			this.port = port;
		}

	}

	class NetworkServiceBinder extends Binder {
		NetworkService getService() {
			return NetworkService.this;
		}
	}
}