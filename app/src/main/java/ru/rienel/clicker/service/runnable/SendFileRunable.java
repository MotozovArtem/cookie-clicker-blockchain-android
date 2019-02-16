package ru.rienel.clicker.service.runnable;

import android.net.Uri;
import android.util.Log;
import ru.rienel.clicker.service.ConfigInfo;
import ru.rienel.clicker.service.NetworkService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SendFileRunable implements Runnable {
	private String host;
	private int port;
	private Uri uri;
	private NetworkService networkService;

	public SendFileRunable(String host, int port, Uri uri, NetworkService networkService) {
		this.host = host;
		this.port = port;
		this.uri = uri;
		this.networkService = networkService;
	}

	@Override
	public void run() {
		if (sendFile()) {
//			networkService.postSendFileResult(0);
		} else {
//			networkService.postSendFileResult(-1);
		}
	}

	private boolean sendFile() {
		Boolean result = true;
		Socket socket = new Socket();
		try {
			Log.d(this.getClass().getName(), "Opening client socket - ");
			socket.bind(null);
			socket.connect((new InetSocketAddress(host, port)), ConfigInfo.SOCKET_TIMEOUT);
			Log.d(this.getClass().getName(),
					"Client socket - " + socket.isConnected());
			OutputStream outputStream = socket.getOutputStream();
			outputStream.write(ConfigInfo.COMMAND_ID_SEND_FILE);// id
			String fileInfo = networkService.getFileInfo(uri);
			Log.d(this.getClass().getName(), "fileInfo:" + fileInfo);
			outputStream.write(fileInfo.length());
			outputStream.write(fileInfo.getBytes(), 0, fileInfo.length());
//			InputStream inputStream = networkService.getInputStream(uri);
			byte[] buffer = new byte[1024];
			int len;
//			while ((len = inputStream.read(buffer)) != -1) {
//				outputStream.write(buffer, 0, len);
//				networkService.postSendRecvBytes(len, 0);
//			}

//			inputStream.close();
//			outputStream.close();
			Log.d(this.getClass().getName(), "Client: Data written");
		} catch (FileNotFoundException e) {
			Log.d(this.getClass().getName(), "send file exception " + e.toString());
		} catch (IOException e) {
			Log.e(this.getClass().getName(),
					"send file exception " + e.getMessage());
			result = false;
		} finally {
			if (socket != null) {
				if (socket.isConnected()) {
					try {
						socket.close();
						Log.d(this.getClass().getName(), "socket.close()");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return result;
	}
}
