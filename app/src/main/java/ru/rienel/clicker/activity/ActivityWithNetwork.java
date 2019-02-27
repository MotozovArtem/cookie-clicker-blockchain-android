package ru.rienel.clicker.activity;

import java.util.List;

import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Handler;

import ru.rienel.clicker.db.domain.Opponent;

public interface ActivityWithNetwork {
	void resetPeers();

	void updatePeers(List<WifiP2pDevice> wifiP2pDevice);

	Handler getHandler();

	void updateThisDevice(Opponent opponent);
}
