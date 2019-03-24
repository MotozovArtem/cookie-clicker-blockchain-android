package ru.rienel.clicker.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ru.rienel.clicker.common.Configuration;

public class ServerService extends IntentService {
	private static final String TAG = ServerService.class.getName();

	public static final String ACTION_SEND_FILE = "com.example.android.wifidirect.SEND_FILE";
	public static final String EXTRAS_FILE_PATH = "file_url";
	public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
	public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";

	public ServerService(String name) {
		super(name);
	}

	public ServerService() {
		super("ServerService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Context context = getApplicationContext();
		if (intent.getAction().equals(ACTION_SEND_FILE)) {
			String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
			String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);

			int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);

			try (Socket socket = new Socket()) {
				Log.d(TAG, "Opening client socket - ");
				socket.bind(null);
				socket.connect((new InetSocketAddress(host, port)), Configuration.TIMEOUT);
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}
}

