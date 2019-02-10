package ru.rienel.clicker.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.support.annotation.Nullable;
import android.util.Log;


public class NetworkService extends IntentService {
	private static final String TAG = NetworkService.class.getName();

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 */
	public NetworkService() {
		super(TAG);
	}

	public static Intent newIntent(Context context) {
		return new Intent(context, NetworkService.class);
	}

	@Override
	protected void onHandleIntent(@Nullable Intent intent) {
		if (!isNetworkAvailable()) {
			return;
		}
		Log.i(TAG, "Received an intent: " + intent);
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		Network activeNetwork = null;
		if (connectivityManager != null) {
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
				activeNetwork = connectivityManager.getActiveNetwork(); // Min 23 SDK
			}
		} else {
			return true;
		}

		return activeNetwork != null;
	}
}
