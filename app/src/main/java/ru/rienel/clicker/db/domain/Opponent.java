package ru.rienel.clicker.db.domain;

import java.net.InetAddress;
import java.util.Objects;

public class Opponent {
	private String name;
	private String macAddress;
	private InetAddress ipAddress;

	public Opponent() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public InetAddress getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(InetAddress ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Opponent opponent = (Opponent)o;
		return Objects.equals(name, opponent.name) &&
				Objects.equals(macAddress, opponent.macAddress);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, macAddress);
	}
}
