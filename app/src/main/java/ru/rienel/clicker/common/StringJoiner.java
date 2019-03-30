package ru.rienel.clicker.common;

public class StringJoiner {
	private StringBuilder result;
	private String delimiter;

	public StringJoiner(String delimiter) {
		this.result = new StringBuilder();
		this.delimiter = delimiter;
	}

	public void add(String str) {
		result.append(delimiter);
		result.append(str);
	}

	@Override
	public String toString() {
		return result.toString();
	}
}
