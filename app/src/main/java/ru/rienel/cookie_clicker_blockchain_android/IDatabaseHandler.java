package ru.rienel.cookie_clicker_blockchain_android;

import java.util.List;

public interface IDatabaseHandler {
	void addPoints(int points);

	List<Integer> getAllPoints();

	int getPointsCount();

	void deleteAll();
}
