package ru.rienel.clicker.net.task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.rienel.clicker.activity.game.GameContract;
import ru.rienel.clicker.activity.opponents.OpponentsContract;
import ru.rienel.clicker.common.Configuration;
import ru.rienel.clicker.common.Preconditions;
import ru.rienel.clicker.net.Signal;

public class ClientAsyncTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = ClientAsyncTask.class.getName();
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(Signal.SignalType.class, new Signal.SignalTypeSerialize())
			.create();

	private InetAddress serverAddress;
	private Signal signal;
	private OpponentsContract.Presenter opponentPresenter;
	private GameContract.Presenter gamePresenter;

	public ClientAsyncTask(InetAddress serverAddress, Signal signal) {
		Preconditions.checkNotNull(signal);
		Preconditions.checkNotNull(serverAddress);

		this.serverAddress = serverAddress;
		this.signal = signal;
	}

	@Override
	protected Boolean doInBackground(Void... voids) {
		InetSocketAddress serverAddress = new InetSocketAddress(this.serverAddress, Configuration.SERVER_PORT);
		try (Socket socket = new Socket()) {
			socket.bind(null);
			socket.connect(serverAddress, Configuration.TIMEOUT);

			OutputStream out = socket.getOutputStream();
			String json = GSON.toJson(signal);
			out.write(json.getBytes());

			out.close();
		} catch (IOException e) {
			Log.e(TAG, "doInBackground: ", e);
			return false;
		}
		return true;
	}

	@Override
	protected void onPreExecute() {
		Log.d(TAG, "onPreExecute: Executed");
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(Boolean aBoolean) {
		super.onPostExecute(aBoolean);
	}

	public OpponentsContract.Presenter getOpponentPresenter() {
		return opponentPresenter;
	}

	public void setOpponentPresenter(OpponentsContract.Presenter opponentPresenter) {
		this.opponentPresenter = opponentPresenter;
	}

	public GameContract.Presenter getGamePresenter() {
		return gamePresenter;
	}

	public void setGamePresenter(GameContract.Presenter gamePresenter) {
		this.gamePresenter = gamePresenter;
	}
}
