package ru.rienel.clicker.common;

public class Preconditions {
	public static void notNull(Object o) {
		if (o == null) {
			throw new IllegalArgumentException("Argument should be noy null");
		}
	}

	public static void isNull(Object o) {
		if (o != null) {
			throw new IllegalArgumentException("Argument should be null");
		}
	}

	public static void equals(Object left, Object right) {
		if (!left.equals(right)) {
			throw new IllegalArgumentException("Arguments should be equals");
		}
	}

	public static void isTrue(Boolean expression) {
		if (!expression) {
			throw new IllegalArgumentException("Expression should be TRUE");
		}
	}
}
