package ru.rienel.clicker.db.factory.domain;

import java.net.InetAddress;
import java.net.UnknownHostException;

import ru.rienel.clicker.db.domain.Opponent;
import ru.rienel.clicker.net.model.OpponentDto;

public class OpponentFactory {
	public static Opponent build(String name, String macAddress, InetAddress ipAddress) {
		Opponent opponent = new Opponent();
		opponent.setName(name);
		opponent.setAddress(macAddress);
		opponent.setIpAddress(ipAddress);
		return opponent;
	}

	public static Opponent buildFromDto(OpponentDto opponentDto) throws UnknownHostException {
		Opponent opponent = new Opponent();
		opponent.setName(opponentDto.getName());
		opponent.setAddress(opponentDto.getAddress());
		opponent.setIpAddress(InetAddress.getByName(opponentDto.getIpAddress()));
		return opponent;
	}
}
