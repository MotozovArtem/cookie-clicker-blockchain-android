package ru.rienel.clicker.common;

import ru.rienel.clicker.R;

public enum ImageDonut {
	PINK_DONUT(R.drawable.pink_donut_menu, 0),
	APPLE(R.drawable.apple_menu, 1),
	BURGER(R.drawable.burger_menu, 2),
	YELLOW_DONUT(R.drawable.yellow_donut_menu, 3),
	BLUE_DONUT(R.drawable.blue_donut_menu, 4);


	public int resourceId;
	public int keyForDialog;

	ImageDonut(int resourceId, int keyForDialog) {
		this.resourceId = resourceId;
		this.keyForDialog = keyForDialog;
	}
}
