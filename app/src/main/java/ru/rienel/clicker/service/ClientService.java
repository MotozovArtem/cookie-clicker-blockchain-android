package ru.rienel.clicker.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.rienel.clicker.common.Configuration;
import ru.rienel.clicker.common.Preconditions;
import ru.rienel.clicker.net.Signal;


public class ClientService extends IntentService {
	private static final String TAG = ClientService.class.getName();
	private static final String EXTRAS_GROUP_OWNER_ADDRESS = "serverAddress";
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(Signal.SignalType.class, new Signal.SignalTypeSerialize())
			.create();

	private Context context;
	private SocketChannel channel;
	private Signal sendSignal;

	public static Intent newIntent(Context context, InetAddress targetAddress) {
		Intent serviceIntent = new Intent(context, ClientService.class);
		serviceIntent.putExtra(EXTRAS_GROUP_OWNER_ADDRESS, targetAddress);
		return serviceIntent;
	}

	public ClientService(String name) {
		super(name);
	}

	public ClientService() {
		super("ClientService");
	}

	@Override
	protected void onHandleIntent(@Nullable Intent intent) {
		Preconditions.checkNotNull(intent);

		Context context = getApplicationContext();
		String intentAction = intent.getAction();
		if (intentAction != null && intentAction.equals(Actions.ACTION_SEND_INVITE)) {
			InetAddress host = (InetAddress)intent.getSerializableExtra(EXTRAS_GROUP_OWNER_ADDRESS);
			try (Socket socket = new Socket()) {
				Log.d(TAG, "Opening client socket - ");
				try {
					SocketAddress address = new InetSocketAddress(host, Configuration.SERVER_PORT);
					SocketChannel client = SocketChannel.open(address);
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					String json = GSON.toJson(sendSignal);

				} catch (IOException ex) {
					ex.printStackTrace();
				}


			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}

	private static class Actions {
		private static String ACTION_SEND_INVITE;
	}
}
