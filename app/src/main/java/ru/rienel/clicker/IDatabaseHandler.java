package ru.rienel.clicker;

import java.util.List;

public interface IDatabaseHandler {
	void addPoints(int points);

	List<Integer> getAllPoints();

	int getPointsCount();

	void deleteAll();
}
