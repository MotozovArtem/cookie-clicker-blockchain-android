package ru.rienel.clicker.common;

public class Preconditions {
	public static void checkNotNull(Object o) {
		if (o == null) {
			throw new IllegalArgumentException("Argument should be not null");
		}
	}

	public static void checkNull(Object o) {
		if (o != null) {
			throw new IllegalArgumentException("Argument should be null");
		}
	}

	public static void checkEquals(Object left, Object right) {
		if (!left.equals(right)) {
			throw new IllegalArgumentException("Arguments should be equals");
		}
	}

	public static void checkExpression(Boolean expression) {
		if (!expression) {
			throw new IllegalArgumentException("Expression should be TRUE");
		}
	}
}
