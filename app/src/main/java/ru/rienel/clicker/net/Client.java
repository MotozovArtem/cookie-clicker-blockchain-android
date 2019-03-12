package ru.rienel.clicker.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.util.Log;

import ru.rienel.clicker.common.Configuration;
import ru.rienel.clicker.common.Preconditions;

public class Client implements Runnable {
	private static final String TAG = Client.class.getName();

	private Socket clientSocket;
	private InetSocketAddress serverAddress;
	private boolean connected;

	public static Client newInstanceTo(InetAddress serverAddress) {
		Preconditions.checkNotNull(serverAddress);

		Client client = new Client();
		client.setServerAddress(serverAddress);

		return client;
	}

	private Client() {
		this.clientSocket = new Socket();
		this.connected = false;
	}

	private void setServerAddress(InetAddress serverAddress) {
		this.serverAddress = new InetSocketAddress(serverAddress, Configuration.SERVER_PORT);
	}

	public boolean isConnected() {
		return connected;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	@Override
	public void run() {
		if (clientSocket == null) {
			clientSocket = new Socket();
		}

		try {
			clientSocket.connect(this.serverAddress);
			connected = true;
		} catch (IOException e) {
			Log.e(TAG, "Runtime: ", e);
			throw new RuntimeException("Client thread run failed", e);
		}
	}
}
