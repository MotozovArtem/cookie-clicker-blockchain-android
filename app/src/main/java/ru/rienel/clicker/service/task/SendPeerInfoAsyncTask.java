package ru.rienel.clicker.service.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import ru.rienel.clicker.common.Preconditions;
import ru.rienel.clicker.common.Utility;

public class SendPeerInfoAsyncTask extends AsyncTask<String, Void, Boolean> {

	private Context context;

	public SendPeerInfoAsyncTask(Context context) {
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		Preconditions.equals(params.length, 2);
		String host = params[0];
		int port = Integer.parseInt(params[1]);
		if (Utility.sendPeerInfo(host, port)){
			return true;

		} else {
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		Log.d("SendPeerInfoAsyncTask", "onPostExecute end.");
		String tips = "";
		if (result) {
			tips = "Send peer's info ok.";
		} else {

			tips = "Send peer's info failed.";
		}
		Toast toast = Toast.makeText(context, tips, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}