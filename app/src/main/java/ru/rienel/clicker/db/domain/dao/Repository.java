package ru.rienel.clicker.db.domain.dao;

import java.util.List;

public interface Repository<T> {
	void add(T model) throws DaoException;

	T findById(Integer id) throws DaoException;

	List<T> findAll() throws DaoException;

	void deleteById(Integer id) throws DaoException;

	void delete(T model) throws DaoException;

	void update(T model) throws DaoException;
}
