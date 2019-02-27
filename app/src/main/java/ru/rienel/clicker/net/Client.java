package ru.rienel.clicker.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.util.Log;

public class Client extends Thread {
	private static final String TAG = "Client";

	private Socket socket;
	private String hostAddr;
	private InetAddress serverAddress;
	private boolean isConnected;
	private SendReceiveHelper sendReceiver;

	public Client(InetAddress hostAddress) {
		hostAddr = hostAddress.getHostAddress();
		socket = new Socket();
	}

	@Override
	public void run() {
		try {
			socket.connect(new InetSocketAddress(hostAddr, 8888), 500);
			sendReceiver = new SendReceiveHelper(socket);
			sendReceiver.start();
		} catch (IOException e) {
			Log.d(TAG, String.format("Could't connect to %s", serverAddress), e);
		}
	}

	public boolean isConnected() {
		return isConnected;
	}
}
