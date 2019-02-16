package ru.rienel.clicker.service.runnable;

import android.util.Log;
import ru.rienel.clicker.service.ConfigInfo;
import ru.rienel.clicker.service.NetworkService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SendStreamRunable implements Runnable {
	private String host;
	private int port;
	private InputStream ins;
	private NetworkService networkService;

	public SendStreamRunable(String host, int port, InputStream ins, NetworkService networkService) {
		this.host = host;
		this.port = port;
		this.ins = ins;
		this.networkService = networkService;
	}

	@Override
	public void run() {
		if (sendStream()) {

//			networkService.postSendStreamResult(0);
		} else {

		}
//			networkService.postSendStreamResult(-1);
	}

	private boolean sendStream() {
		Socket socket = new Socket();
		boolean result = true;

		try {
			Log.d(this.getClass().getName(), "Opening client socket - ");
			socket.bind(null);
			socket.connect((new InetSocketAddress(host, port)), ConfigInfo.SOCKET_TIMEOUT);

			Log.d(this.getClass().getName(), "Client socket - " + socket.isConnected());
			OutputStream outputStream = socket.getOutputStream();

			byte[] buffer = new byte[1024];
			int len;
			while ((len = ins.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}
			ins.close();
			outputStream.close();
			Log.d(this.getClass().getName(), "send stream ok.");

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
