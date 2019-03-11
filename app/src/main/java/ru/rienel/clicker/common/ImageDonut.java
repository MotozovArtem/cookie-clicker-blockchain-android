package ru.rienel.clicker.common;

import ru.rienel.clicker.R;

public enum ImageDonut {
    PINK_DONUT(R.drawable.pink_donut_dialog, 1),
    APPLE(R.drawable.apple_dialog, 2),
    BURGER(R.drawable.burger_dialog, 3),
    YELLOW_DONUT(R.drawable.yellow_donut_dialog, 4),
    BLUE_DONUT(R.drawable.blue_donut_dialog, 5);


    public int resourceId;
    public int keyForDialog;

    ImageDonut(int resourceId, int keyForDialog) {
        this.resourceId = resourceId;
        this.keyForDialog = keyForDialog;
   }
}
