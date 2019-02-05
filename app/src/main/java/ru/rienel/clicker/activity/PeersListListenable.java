package ru.rienel.clicker.activity;

import android.net.wifi.p2p.WifiP2pManager;

public interface PeersListListenable {
	WifiP2pManager.PeerListListener getPeerListListener();

	WifiP2pManager.ConnectionInfoListener getConnectionInfoListener();
}
