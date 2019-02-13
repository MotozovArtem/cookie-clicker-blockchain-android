package ru.rienel.clicker.service;

import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;

public class NetworkServiceListener implements ConnectionInfoListener, PeerListListener {

	public ConnectionInfoListener connectionInfoListener;
	public PeerListListener peersListener;

	public NetworkServiceListener(ConnectionInfoListener connectionInfoListener, PeerListListener peersListener) {
		this.connectionInfoListener = connectionInfoListener;
		this.peersListener = peersListener;
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		connectionInfoListener.onConnectionInfoAvailable(info);
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		peersListener.onPeersAvailable(peers);
	}
}
