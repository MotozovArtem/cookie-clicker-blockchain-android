package ru.rienel.clicker.service.task;

import android.os.AsyncTask;
import android.util.Log;
import ru.rienel.clicker.common.Utility;

public class RequestSendFileAsyncTask extends AsyncTask<Void, Void, Boolean> {
	private String fileName;
	private int fileSize;
	private String host;
	private int port;

	public RequestSendFileAsyncTask(String name, int size, String host, int port) {
		this.fileName = name;
		this.fileSize = size;
		this.host = host;
		this.port = port;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		if (Utility.sendFileInfo(fileName, fileSize, host, port)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		Log.d(this.getClass().getName(), "onPostExecute result:" + result);
	}
}