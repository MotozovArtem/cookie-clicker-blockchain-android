package ru.rienel.clicker.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.util.Log;

public class Client extends Thread {
	private static final String TAG = "Client";

	private Socket socket;
	private InetAddress serverAddress;
	private boolean isConnected;

	public Client(InetAddress serverAddress) {
		this.serverAddress = serverAddress;
		socket = new Socket();

	}

	@Override
	public void run() {
		super.run();
		try {
			socket.connect(new InetSocketAddress(serverAddress, 10000), 3000);
			isConnected = true;
		} catch (IOException e) {
			Log.d(TAG, String.format("Could't connect to %s", serverAddress), e);
		}
	}

	public boolean isConnected() {
		return this.isConnected;
	}
}
