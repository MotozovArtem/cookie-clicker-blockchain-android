package ru.rienel.clicker.service.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import ru.rienel.clicker.common.Utility;

import java.io.InputStream;

public class SendStreamAsyncTask extends AsyncTask<InputStream, Void, Boolean> {
	private Context context;
	private String host;
	private int port;

	public SendStreamAsyncTask(Context context, String host, int port) {
		this.context = context;
		this.host = host;
		this.port = port;
	}

	@Override
	protected Boolean doInBackground(InputStream... params) {
		assert (params.length == 1);
		if (Utility.sendStream(host, port, params[0]))
			return Boolean.TRUE;
		else
			return Boolean.FALSE;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		Log.d("SendStringAsyncTask", "onPostExecute end.");
		String tips = "";
		if (result) {
			tips = "Send ok.";
		} else {
			tips = "Send failed.";
		}
		Toast toast = Toast.makeText(context, tips, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}