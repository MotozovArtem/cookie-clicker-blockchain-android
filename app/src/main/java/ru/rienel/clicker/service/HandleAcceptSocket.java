package ru.rienel.clicker.service;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.locks.ReentrantLock;

public class HandleAcceptSocket implements Runnable {
	private final Socket socket;
	private final NetworkService networkService;
	private static final ReentrantLock lockRecvFile = new ReentrantLock();

	public HandleAcceptSocket(NetworkService service, Socket socket) {
		this.networkService = service;
		this.socket = socket;
	}

	public void closeSocket() {
		if (!socket.isClosed()) {
			try {
				socket.close();
			} catch (IOException e) {
				Log.e(this.getClass().getName(), "exception e:" + e);
				e.printStackTrace();
			}
		}
	}
	public void run() {
		SocketAddress socketAddress = socket.getRemoteSocketAddress();
		Log.d(this.getClass().getName(), "sockAddr:" + socketAddress);

		try {
			InputStream inputStream = socket.getInputStream();
			int command = inputStream.read();
			Log.d(this.getClass().getName(), "Run command:" + command);
			if (command == ConfigInfo.COMMAND_ID_SEND_PEER_INFO) {
//				networkService.handleRecvPeerInfo(inputStream);
			} else if (command == ConfigInfo.COMMAND_ID_SEND_FILE) {
				lockRecvFile.lock();
				try {
//					networkService.setRemoteSockAddress(socketAddress);
//					networkService.handleRecvFile(inputStream);
				} finally {
					lockRecvFile.unlock();
				}
			} else if (command == ConfigInfo.COMMAND_ID_REQUEST_SEND_FILE) {
//				networkService.handleRecvFileInfo(inputStream);
			} else if (command == ConfigInfo.COMMAND_ID_RESPONSE_SEND_FILE) {

			} else if (command == ConfigInfo.COMMAND_ID_BROADCAST_PEER_LIST) {
//				networkService.handleRecvPeerList(inputStream);
			}  else if (command == ConfigInfo.COMMAND_ID_SEND_STRING) {

			}

			inputStream.close();
		} catch (IOException e) {
			Log.e(this.getClass().getName(), e.getMessage());
			return;
		}
	}
}
