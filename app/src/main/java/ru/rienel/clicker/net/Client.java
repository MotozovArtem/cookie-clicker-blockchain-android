package ru.rienel.clicker.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.rienel.clicker.common.Configuration;
import ru.rienel.clicker.common.Configuration.MessageConstants;
import ru.rienel.clicker.net.model.Signal;

public class Client implements Runnable {
	private static final String TAG = Client.class.getName();
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(Signal.SignalType.class, Signal.SignalTypeSerializer.class)
			.registerTypeAdapter(Signal.SignalType.class, Signal.SignalTypeDeserializer.class)
			.create();

	private SocketChannel client;
	private ByteBuffer buffer = ByteBuffer.allocate(MessageConstants.STANDARD_BUFFER_SIZE);
	private static Client instance;

	public static Client start() {
		if (instance == null)
			instance = new Client();

		return instance;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (client.isOpen()) {
			client.close();
		}
	}

	public void connect(String address) throws IOException {
		client = SocketChannel.open(new InetSocketAddress("localhost", Configuration.SERVER_PORT));
	}

	public String sendSignal(Signal signal) {
		String json = GSON.toJson(signal);
		String messageWithLengthHeader = MessageProcessor.prependLengthHeader(json);
		buffer = ByteBuffer.wrap(messageWithLengthHeader.getBytes());
		String response = null;
		try {
			client.write(buffer);
			buffer.clear();
			client.read(buffer);
			response = new String(buffer.array()).trim();
			System.out.println("response=" + response);
			buffer.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;

	}

	@Override
	public void run() {

	}

	public static class ConnectionEvent extends EventObject {
		private final Path imagePath;
		private final String flightId;

		public ConnectionEvent(Object source, Path imagePath, String flightId) {
			super(source);
			Objects.requireNonNull(imagePath);
			Objects.requireNonNull(flightId);
			this.imagePath = imagePath;
			this.flightId = flightId;
		}

		public Path getImagePath() {
			return imagePath;
		}

		public String getFlightId() {
			return flightId;
		}
	}

	public interface ConnectionListener extends EventListener {
		void connected();

		void disconnected();

		void receivedSignal();
	}
}
