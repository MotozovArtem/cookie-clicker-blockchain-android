package ru.rienel.clicker.net;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
	private static final String TAG = "Server";

	private Socket socket;
	private ServerSocket serverSocket;
	private SendReceiveHelper sendReceiveHelper;

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(8888);
			socket = serverSocket.accept();
			sendReceiveHelper = new SendReceiveHelper(socket);
			sendReceiveHelper.start();
		} catch (IOException e) {
			Log.w(TAG, "Error while run() ", e);
		}


	}
}
