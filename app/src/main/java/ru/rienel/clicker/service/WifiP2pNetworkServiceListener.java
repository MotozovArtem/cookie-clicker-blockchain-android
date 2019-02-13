package ru.rienel.clicker.service;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;

public interface WifiP2pNetworkServiceListener extends ConnectionInfoListener, PeerListListener {
	void updateThisDevice(WifiP2pDevice device);
}
