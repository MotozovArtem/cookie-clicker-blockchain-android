package ru.rienel.clicker.net.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import ru.rienel.clicker.common.Configuration;

public class ServerAsyncTask extends AsyncTask<Object, Void, Boolean> {
	private static final String TAG = ServerAsyncTask.class.getName();

	private Context context;

	public ServerAsyncTask(Context context) {
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(Object[] params) {
		try (ServerSocket serverSocket = new ServerSocket(Configuration.SERVER_PORT)) {
			Socket client = serverSocket.accept();
			/*TODO Send signals and*/
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
}
