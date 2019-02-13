package ru.rienel.clicker.db.domain;

import java.util.Objects;

public class Opponent {
	private String name;
	private String address;

	public Opponent() {
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


	@Override
	public boolean equals(Object o) {
		if (this == o){
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Opponent opponent = (Opponent) o;
		return Objects.equals(name, opponent.name) &&
				Objects.equals(address, opponent.address);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, address);
	}
}
