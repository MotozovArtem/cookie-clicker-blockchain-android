package ru.rienel.clicker.db.factory.domain;

import java.net.InetAddress;

import ru.rienel.clicker.db.domain.Opponent;

public class OpponentFactory {
	public static Opponent build(String name, String macAddress, InetAddress ipAddress) {
		Opponent opponent = new Opponent();
		opponent.setName(name);
		opponent.setAddress(macAddress);
		opponent.setIpAddress(ipAddress);
		return opponent;
	}
}
