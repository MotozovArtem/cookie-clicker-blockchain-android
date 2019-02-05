package ru.rienel.clicker.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class NetworkService extends Service {
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
