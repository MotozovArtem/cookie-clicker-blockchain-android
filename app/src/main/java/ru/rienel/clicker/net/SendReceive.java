package ru.rienel.clicker.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.util.Log;

import ru.rienel.clicker.common.SignalHandler;

import static ru.rienel.clicker.common.Configuration.MessageConstants.MESSAGE_READ;

public class SendReceive implements Runnable {
	private static final String TAG = SendReceive.class.getName();
	private static final int BUFFER_LENGTH = 1024;

	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private SignalHandler handler;

	public SendReceive(Socket socket, SignalHandler handler) {
		this.socket = socket;
		this.handler = handler;
		try {
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		} catch (IOException e) {
			Log.e(TAG, "SendReceive: IOException", e);
			throw new RuntimeException();
		}
	}

	public void write(byte[] bytes) throws IOException {
		outputStream.write(bytes);
	}

	@Override
	public void run() {
		byte[] buffer = new byte[BUFFER_LENGTH];
		int bytes;

		while (socket != null) {
			try {
				bytes = inputStream.read(buffer);
				if (bytes > 0) {
					handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
				}
			} catch (IOException e) {
				Log.e(TAG, "run: IOException", e);
			}
		}
	}
}
