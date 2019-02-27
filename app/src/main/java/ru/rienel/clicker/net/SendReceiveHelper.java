package ru.rienel.clicker.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SendReceiveHelper extends Thread {
	private static final String TAG = "SendReceiveHelper";
	private static final int MESSAGE_READ = 1;

	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;

	public SendReceiveHelper(Socket socket) {
		this.socket = socket;
		try {
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		byte[] buffer = new byte[1024];
		int bytes;
		while (socket != null) {
			try {
				bytes = inputStream.read(buffer);
				if (bytes > 0) {
					handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
				}
			} catch (IOException e) {
				Log.e(TAG, "Error", e);
			}
		}
	}

	public void write(byte[] bytes) {
		try {
			outputStream.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGE_READ:
					byte[] readBuff = (byte[]) msg.obj;
					String tempMessage = new String(readBuff, 0, msg.arg1);
//					readMsgBox.setText(tempMessage);
					// TODO handle message receive
					break;
			}
			return true;
		}
	});
}
