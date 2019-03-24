package ru.rienel.clicker.net.dto;

import ru.rienel.clicker.db.domain.Opponent;

public class OpponentDto {
	private String name;
	private String address;
	private String ipAddress;

	public static OpponentDto newFromOpponent(Opponent opponent) {
		OpponentDto opponentDto = new OpponentDto();
		opponentDto.setName(opponent.getName());
		opponentDto.setAddress(opponent.getAddress());
		opponentDto.setIpAddress(opponent.getIpAddress().getHostAddress());
		return opponentDto;
	}

	public OpponentDto() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
}
