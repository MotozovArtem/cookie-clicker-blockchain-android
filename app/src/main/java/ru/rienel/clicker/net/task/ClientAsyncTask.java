package ru.rienel.clicker.net.task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.rienel.clicker.common.Configuration;
import ru.rienel.clicker.net.Signal;

public class ClientAsyncTask extends AsyncTask<Object, Void, Boolean> {
	private static final String TAG = ClientAsyncTask.class.getName();
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(Signal.SignalType.class, new Signal.SignalTypeDeserializer())
			.create();

	private Context context;
	private InetSocketAddress serverAddress;
	private Signal signal;

	public ClientAsyncTask(Context context, InetAddress serverAddress, Signal signal) {
		this.context = context;
		this.serverAddress = new InetSocketAddress(serverAddress, Configuration.SERVER_PORT);
	}

	@Override
	protected Boolean doInBackground(Object... objects) {
		byte[] buffer = new byte[2048];
		try (Socket socket = new Socket()) {
			socket.bind(null);
			socket.connect(this.serverAddress, Configuration.TIMEOUT);

			String json = GSON.toJson(signal);
			OutputStream outputStream = socket.getOutputStream();
			InputStream inputStream = socket.getInputStream();

//			outputStream.write();

			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
			Log.e(TAG, "doInBackground: ", e);
			return false;
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
