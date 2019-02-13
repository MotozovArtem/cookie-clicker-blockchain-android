package ru.rienel.clicker.service.task;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import ru.rienel.clicker.common.Preconditions;
import ru.rienel.clicker.service.ConfigInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


public class SendFileAsyncTask extends AsyncTask<Activity, Void, Boolean> {

	private String fileUri;
	private String host;
	private int port;
	private Context context;

	public SendFileAsyncTask(String fileUri, String host, int port) {
		this.fileUri = fileUri;
		this.host = host;
		this.port = port;
	}

	@Override
	protected Boolean doInBackground(Activity... activities) {
		Preconditions.equals(activities.length, 1);

		Boolean result = true;
//		WiFiDirectActivity activity = (WiFiDirectActivity) activities[0];
//		this.context = activity;
		Socket socket = new Socket();
		try {
			Log.d(this.getClass().getName(), "Opening client socket - ");
			socket.bind(null);
			socket.connect((new InetSocketAddress(host, port)), ConfigInfo.SOCKET_TIMEOUT);
			Log.d(this.getClass().getName(),
					"Client socket - " + socket.isConnected());
			OutputStream outs = socket.getOutputStream();
			outs.write(ConfigInfo.COMMAND_ID_SEND_FILE);// id
			Uri uri = Uri.parse(fileUri);

			String strSend = "size:" + 0 + "name:fadsf";
			outs.write(strSend.length());
			outs.write(strSend.getBytes(), 0, strSend.length());



		} catch (IOException e) {
			Log.e(this.getClass().getName(),
					"send file exception " + e.getMessage());
			result = false;
		} finally {
			if (socket != null) {
				if (socket.isConnected()) {
					try {
						socket.close();
						Log.d(this.getClass().getName(), "socket.close()");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return result;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		Log.d("SendFileAsyncTask", "onPostExecute end. result " + result);

		String tips = "";
		if (result) {
			tips = "Send file ok.";
		} else {
			tips = "Send file failed.";
		}
		Toast toast = Toast.makeText(context, tips, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}