package ru.rienel.clicker.net.task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import ru.rienel.clicker.common.Configuration;

public class ClientAsyncTask extends AsyncTask<Object, Void, Boolean> {

	private Context context;
	private InetSocketAddress serverAddress;

	public ClientAsyncTask(Context context, InetAddress serverAddress) {
		this.context = context;
		this.serverAddress = new InetSocketAddress(serverAddress, Configuration.SERVER_PORT);
	}

	@Override
	protected Boolean doInBackground(Object... objects) {
		int len;
		Socket socket = new Socket();
		byte[] buffer = new byte[1024];
		try {
			socket.bind(null);
			socket.connect(this.serverAddress, Configuration.TIMEOUT);


			OutputStream outputStream = socket.getOutputStream();
			ContentResolver contentResolver = context.getContentResolver();
			InputStream inputStream = null;
			inputStream = contentResolver.openInputStream(Uri.parse("path/to/picture.jpg"));
			while ((len = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}
			outputStream.close();
			inputStream.close();
		} catch (FileNotFoundException e) {
			//catch logic
			return false;
		} catch (IOException e) {
			//catch logic
			return false;
		} finally {
			if (socket != null) {
				if (socket.isConnected()) {
					try {
						socket.close();
					} catch (IOException e) {
						//catch logic
					}
				}
			}
		}
		return true;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(Boolean aBoolean) {
		super.onPostExecute(aBoolean);
	}
}
