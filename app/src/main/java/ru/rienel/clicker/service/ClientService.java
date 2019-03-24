package ru.rienel.clicker.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import ru.rienel.clicker.common.Configuration;
import ru.rienel.clicker.common.Preconditions;


public class ClientService extends IntentService {
	private static final String TAG = ClientService.class.getName();
	private static final String EXTRAS_GROUP_OWNER_ADDRESS = "serverAddress";


	private Context context;

	public static Intent newIntent(Context context) {
		return new Intent(context, ClientService.class);
	}

	public ClientService(String name) {
		super(name);
	}

	public ClientService() {
		super("ClientService");
	}

	@Override
	protected void onHandleIntent(@Nullable Intent intent) {
		Preconditions.checkNotNull(intent);

		Context context = getApplicationContext();
		String intentAction = intent.getAction();
		if (intentAction != null && intentAction.equals(Actions.ACTION_SEND_INVITE)) {
			String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
			try (Socket socket = new Socket()) {
				Log.d(TAG, "Opening client socket - ");
				socket.bind(null);
				socket.connect((new InetSocketAddress(host, Configuration.SERVER_PORT)), Configuration.TIMEOUT);
				Log.d(TAG, "Client socket - " + socket.isConnected());

				OutputStream out = socket.getOutputStream();
				InputStream in = null;
				Log.d(TAG, "Client: Signal sent");
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}

	private static class Actions {
		private static String ACTION_SEND_INVITE;
	}
}
