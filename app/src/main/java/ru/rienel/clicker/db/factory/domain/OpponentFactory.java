package ru.rienel.clicker.db.factory.domain;

import android.net.wifi.p2p.WifiP2pDevice;
import ru.rienel.clicker.db.domain.Opponent;

public class OpponentFactory {
	public static Opponent build(String name, String address) {
		Opponent opponent = new Opponent();
		opponent.setName(name);
		opponent.setAddress(address);
		return opponent;
	}

	public static Opponent buildFromWifiP2pDevice(WifiP2pDevice device) {
		if (device == null) {
			throw new IllegalArgumentException("Device cannot be null");
		}
		Opponent opponent = new Opponent();
		opponent.setName(device.deviceName);
		opponent.setAddress(device.deviceAddress);
		return opponent;
	}
}
