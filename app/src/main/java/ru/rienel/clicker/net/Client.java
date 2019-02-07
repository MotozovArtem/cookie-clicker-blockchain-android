package ru.rienel.clicker.net;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client extends Thread {
	private Socket socket;
	private InetAddress clientAddress;
	private static final String TAG = "Client";

	public Client(InetAddress clientAddress) {
		this.clientAddress = clientAddress;
		socket = new Socket();
	}

	@Override
	public void run() {
		super.run();
		try {
			socket.connect(new InetSocketAddress(clientAddress, 10000), 3000);
		} catch (IOException e) {
			Log.d(TAG, String.format("Could't connect to %s", clientAddress), e);
		}
	}
}
