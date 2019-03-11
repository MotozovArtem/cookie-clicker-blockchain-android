package ru.rienel.clicker.db.factory.domain;

import java.net.InetAddress;

import android.net.wifi.p2p.WifiP2pDevice;

import ru.rienel.clicker.db.domain.Opponent;

public class OpponentFactory {
	public static Opponent build(String name, String macAddress, InetAddress ipAddress) {
		Opponent opponent = new Opponent();
		opponent.setName(name);
		opponent.setMacAddress(macAddress);
		opponent.setIpAddress(ipAddress);
		return opponent;
	}
}
