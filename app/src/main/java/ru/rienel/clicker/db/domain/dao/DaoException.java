package ru.rienel.clicker.db.domain.dao;

public class DaoException extends Exception {
	public DaoException(String message) {
		super(message);
	}

	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}
}
