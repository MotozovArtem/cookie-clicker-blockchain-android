package ru.rienel.clicker.net.task;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.rienel.clicker.activity.game.GameContract;
import ru.rienel.clicker.activity.opponents.OpponentsContract;
import ru.rienel.clicker.common.Configuration;
import ru.rienel.clicker.net.Signal;

public class ServerAsyncTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = ServerAsyncTask.class.getName();
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(Signal.SignalType.class, new Signal.SignalTypeDeserializer())
			.create();
	private static final int BUFFER_CAPACITY = 1024;

	private Signal signal;
	private OpponentsContract.Presenter opponentPresenter;
	private GameContract.Presenter gamePresenter;

	public ServerAsyncTask(OpponentsContract.Presenter opponentPresenter,
	                       GameContract.Presenter gamePresenter) {
		this.opponentPresenter = opponentPresenter;
		this.gamePresenter = gamePresenter;
	}

	@Override
	protected Boolean doInBackground(Void... voids) {
		try (ServerSocket serverSocket = new ServerSocket(Configuration.SERVER_PORT)) {
			Socket client = serverSocket.accept();
			InputStream in = client.getInputStream();

			Reader inputStreamReader = new InputStreamReader(in);
			signal = GSON.fromJson(inputStreamReader, Signal.class);
			in.close();

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
