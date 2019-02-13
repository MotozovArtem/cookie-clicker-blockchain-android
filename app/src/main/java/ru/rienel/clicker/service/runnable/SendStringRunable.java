package ru.rienel.clicker.service.runnable;

import android.util.Log;
import ru.rienel.clicker.service.ConfigInfo;
import ru.rienel.clicker.service.NetworkService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SendStringRunable implements Runnable {
	private String host;
	private int port;
	private String data;
	private NetworkService networkService;

	public SendStringRunable(String host, int port, String data, NetworkService netService) {
		this.host = host;
		this.port = port;
		this.data = data;
		this.networkService = netService;
	}

	@Override
	public void run() {
		if (sendString())
			networkService.postSendStringResult(data.length());
		else
			networkService.postSendStringResult(-1);
	}

	private boolean sendString() {
		Socket socket = new Socket();
		boolean result = true;

		try {
			Log.d(this.getClass().getName(), "Opening client socket - ");
			socket.bind(null);
			socket.connect((new InetSocketAddress(host, port)), ConfigInfo.SOCKET_TIMEOUT);

			Log.d(this.getClass().getName(), "Client socket - " + socket.isConnected());
			OutputStream outputStream = socket.getOutputStream();
			outputStream.write(ConfigInfo.COMMAND_ID_SEND_STRING);
			outputStream.write(data.length());
			outputStream.write(data.getBytes());
			outputStream.close();
			Log.d(this.getClass().getName(), "send string ok.");

		} catch (IOException e) {
			Log.e(this.getClass().getName(), e.getMessage());
			result = false;
		} finally {
			if (socket != null) {
				if (socket.isConnected()) {
					try {
						socket.close();
						Log.d(this.getClass().getName(), "socket.close();");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return result;
	}
}
