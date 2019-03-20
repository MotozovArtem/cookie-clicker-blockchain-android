package ru.rienel.clicker.net.task;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.rienel.clicker.common.Configuration;
import ru.rienel.clicker.net.Signal;

public class ServerAsyncTask extends AsyncTask<Object, Void, Boolean> {
	private static final String TAG = ServerAsyncTask.class.getName();
	private static final Gson GSON = new GsonBuilder().
			registerTypeAdapter(Signal.SignalType.class, new Signal.SignalTypeDeserializer())
			.create();

	private Context context;
	private Signal signal;

	public ServerAsyncTask(Context context) {
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(Object[] params) {
		try (ServerSocket serverSocket = new ServerSocket(Configuration.SERVER_PORT)) {
			Socket client = serverSocket.accept();

			return true;
		} catch (IOException e) {
			Log.e(TAG, "doInBackground: ", e);
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean aBoolean) {
		super.onPostExecute(aBoolean);
	}

	public Signal getSignal() {
		return signal;
	}
}
