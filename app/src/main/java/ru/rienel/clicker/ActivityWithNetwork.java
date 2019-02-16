package ru.rienel.clicker;

import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Handler;
import ru.rienel.clicker.db.domain.Opponent;

import java.util.List;

public interface ActivityWithNetwork {
	void resetPeers();

	void updatePeers(List<WifiP2pDevice> wifiP2pDevice);

	Handler getHandler();

	void updateThisDevice(Opponent opponent);
}
