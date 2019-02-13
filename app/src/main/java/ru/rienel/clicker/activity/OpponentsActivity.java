package ru.rienel.clicker.activity;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ru.rienel.clicker.R;
import ru.rienel.clicker.db.domain.Opponent;
import ru.rienel.clicker.net.WifiP2pHelper;
import ru.rienel.clicker.service.ConfigInfo;
import ru.rienel.clicker.service.DeviceActionListener;
import ru.rienel.clicker.service.NetworkService;
import ru.rienel.clicker.ui.view.OpponentListFragment;

import java.util.List;

public class OpponentsActivity extends AppCompatActivity implements DeviceActionListener {
	public static final String TAG = OpponentsActivity.class.getName();

	private NetworkService networkService;
	private WifiP2pHelper wifiP2pHelper;

//	private OpponentDetailFragment opponentDetailFragment;
	private OpponentListFragment opponentListFragment;

	private String selectHost = "";


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.opponents_activity);

		wifiP2pHelper = new WifiP2pHelper(this);

		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(R.id.opponent_fragment_container);

		if (fragment == null) {
			fragment = new OpponentListFragment();
			fragmentManager.beginTransaction()
					.add(R.id.opponent_fragment_container, fragment)
					.commit();
		}
	}

	public NetworkService getNetworkService() {
		return networkService;
	}

	public void setNetworkService(NetworkService networkService) {
		this.networkService = networkService;
	}

//	private OpponentDetailFragment getDetailFragment() {
//		if (opponentDetailFragment == null) {
//			FragmentManager fragmentManager = getSupportFragmentManager();
//			opponentDetailFragment = (OpponentDetailFragment) fragmentManager
//					.findFragmentById(R.id.opponent_fragment_container);
//		}
//		return opponentDetailFragment;
//	}

	private OpponentListFragment getListFragment() {
		if (opponentListFragment == null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			opponentListFragment = (OpponentListFragment) fragmentManager
					.findFragmentById(R.id.opponent_fragment_container);
		}
		return opponentListFragment;
	}

	public List<WifiP2pDevice> getPeersList() {
		return this.getNetworkService().getP2pDevices();
	}

	public void updateThisDevice(Opponent device) {
		getListFragment().updateOpponent(device);
	}

	@Override
	protected void onStart() {
		super.onStart();


		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(R.id.opponent_fragment_container);
		if (fragment != null) {
			wifiP2pHelper.discoverPeers();
			((OpponentListFragment) fragment).setOpponentList(wifiP2pHelper.getOpponents());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(wifiP2pHelper.getReceiver(), wifiP2pHelper.getIntentFilter());
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(wifiP2pHelper.getReceiver());
	}

	static private class ActivityHandler extends Handler {
		private static final String TAG = "ActivityHandler";
		private OpponentsActivity activity;

		ActivityHandler(OpponentsActivity activity) {
			this.activity = activity;
		}

		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, "handleMessage()  msg.what:" + msg.what);
			switch (msg.what) {
				case ConfigInfo.MSG_RECV_PEER_INFO:
//					activity.getDetailFragment().showSendFileVeiw();
					break;
				case ConfigInfo.MSG_SEND_RECV_FILE_BYTES:
//					activity.sendBytes = activity.sendBytes + msg.arg1;
//					activity.recvBytes = activity.recvBytes + msg.arg2;
//					int progress1 = 0;
//					int progress2 = 0;
//					if (activity.sendFileSize != 0)
//						progress1 = (int) (activity.sendBytes * 100 / (activity.sendFileSize));
//					if (activity.recvFileSize != 0)
//						progress2 = (int) (activity.recvBytes * 100 / (activity.recvFileSize));
//
//					String tips = "\n send:" + progress1 + "(%) data(kb):"
//							+ activity.sendBytes / 1024 + "\n recv:" + progress2
//							+ "(%) data(kb):" + activity.recvBytes / 1024;
//
//					activity.getDetailFragment().showStatus(tips);
					break;
				case ConfigInfo.MSG_REPORT_SEND_PEER_INFO_RESULT:
//					if (msg.arg1 == 0)
//						activity.showToastTips("send peer's info successed.");
//					else
//						activity.showToastTips("send peer's info failed.");
					break;
				case ConfigInfo.MSG_SEND_STRING:
//					if (msg.arg1 == -1)
//						activity.showToastTips("send string failed.");
//					else
//						activity.showToastTips("send string successed, length " + msg.arg1 + ".");
					break;
				case ConfigInfo.MSG_REPORT_RECV_PEER_LIST:
//					activity.showToastTips("receive peer list.");
				case ConfigInfo.MSG_REPORT_SEND_STREAM_RESULT:
//					if (msg.arg1 == 0)
//						activity.showToastTips("send stream successed.");
//					else
//						activity.showToastTips("send stream failed.");
					break;
				default:
//					activity.showToastTips("error msg id.");
			}
			super.handleMessage(msg);
		}
	}

	private Handler handler = new ActivityHandler(this);

	public Handler getHandler() {
		return handler;
	}

	public void resetPeers() {
		getListFragment().clearOpponents();
	}

	@Override
	public void showDetails(WifiP2pDevice device) {
	}

	@Override
	public void cancelDisconnect() {
		Log.e(TAG, "cancelDisconnect.");
		if (networkService.isWifiP2pManager()) {
			final OpponentListFragment fragment = getListFragment();
//			if (fragment.getDevice() == null
//					|| fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
//				disconnect();
//			} else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
//					|| fragment.getDevice().status == WifiP2pDevice.INVITED) {
//				networkService.cancelDisconnect();
//			}
		}
	}

	@Override
	public void connect(WifiP2pConfig config) {
		networkService.connect(config);
	}

	@Override
	public void disconnect() {
		resetPeers();
		networkService.removeGroup();
		networkService.discoverPeers();
	}
}
