package ru.rienel.clicker.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Objects;
import java.util.Queue;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.rienel.clicker.common.Configuration.MessageConstants;
import ru.rienel.clicker.net.model.Signal;
import ru.rienel.clicker.net.model.Signal.SignalType;
import ru.rienel.clicker.net.model.Signal.SignalTypeDeserializer;
import ru.rienel.clicker.net.model.Signal.SignalTypeSerializer;

public class Client implements Runnable {
	private static final String TAG = Client.class.getName();
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(SignalType.class, new SignalTypeDeserializer())
			.registerTypeAdapter(SignalType.class, new SignalTypeSerializer())
			.create();
	private static final String FATAL_COMMUNICATION_MSG = "Lost connection...";

	private final ByteBuffer buffer = ByteBuffer.allocateDirect(MessageConstants.STANDARD_BUFFER_SIZE);
	private Selector selector;
	private SocketChannel client;
	private InetSocketAddress serverAddress;
	private final Queue<ByteBuffer> messageToSend = new ArrayDeque<>();
	private MessageProcessor messageProcessor;

	private boolean connected;
	private boolean timeToSend;

	private Client(String address, Integer port) {
		serverAddress = new InetSocketAddress(address, port);
	}

	@Override
	public void run() {
		try {
			initConnection();
			initSelector();

			while (connected || !messageToSend.isEmpty()) {
				if (timeToSend) {
					client.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
					timeToSend = false;
				}

				selector.select();
				for (SelectionKey key : selector.selectedKeys()) {
					selector.selectedKeys().remove(key);
					if (!key.isValid()) {
						continue;
					}

					if (key.isConnectable()) {
						completeConnection(key);
					} else if (key.isReadable()) {
						receive(key);
					} else if (key.isWritable()) {
						send(key);
					}
				}
			}
		} catch (IOException e) {
			Log.e(TAG, "run: ", e);
		}
	}

	private void initSelector() throws IOException {
		selector = Selector.open();
		client.register(selector, SelectionKey.OP_CONNECT);
	}

	private void initConnection() throws IOException {
		client = SocketChannel.open();
		client.configureBlocking(false);
		client.connect(serverAddress);
		connected = true;
	}

	private void completeConnection(SelectionKey key) throws IOException {
		client.finishConnect();
		key.interestOps(SelectionKey.OP_READ);
	}

	private void receive(SelectionKey key) throws IOException {
		buffer.clear();
		int numOfReadBytes = client.read(buffer);
		if (numOfReadBytes == 1) {
			Log.e(TAG, "receive: " + FATAL_COMMUNICATION_MSG);
			throw new IOException(FATAL_COMMUNICATION_MSG);
		}

		String received = extractMessageFromBuffer();
		messageProcessor.appendReceivedMessage(received);
		while (messageProcessor.hasNext()) {
			String msg = messageProcessor.nextMsg();
			notifyMessageReceived(MessageProcessor.getMessageBody(msg));
		}
	}

	private String extractMessageFromBuffer() {
		buffer.flip();
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes);
		return new String(bytes);
	}

	private void send(SelectionKey key) throws IOException {
		ByteBuffer msg;
		synchronized (messageToSend) {
			while ((msg = messageToSend.peek()) != null) {
				client.write(msg);
				if (msg.hasRemaining()) {
					return;
				}
				messageToSend.remove();
			}
			key.interestOps(SelectionKey.OP_READ);
		}
	}

	public void disconnect() {
		connected = false;
		sendSignal(null);
	}

	public void sendSignal(Signal signal) {
		String signalJson = GSON.toJson(signal);
		String messageWithLengthHeader = MessageProcessor.prependLengthHeader(signalJson);
		synchronized (messageToSend) {
			messageToSend.add(ByteBuffer.wrap(messageWithLengthHeader.getBytes()));
		}
		timeToSend = true;
		selector.wakeup();
	}


	private void notifyMessageReceived(String message) {
//		Executor pool = ForkJoinPool.commonPool();
//		for (CommunicationListener listener : listeners) {
//			pool.execute(() -> {
//				listener.recvMsg(message);
//			});
//		}
	}

	private void notifyConnectionDone(InetSocketAddress remoteAddress) {
//		Executor pool = ForkJoinPool.commonPool();
//		for (CommunicationListener listener : listeners) {
//			pool.execute(() -> listener.connected(remoteAddress));
//		}
	}

	private void notifyDisconnectionDone() {
//		Executor pool = ForkJoinPool.commonPool();
//		for (CommunicationListener listener : listeners) {
//			pool.execute(listener::disconnected);
//		}
	}


	public static class Builder {
		public String address;
		private Integer port;

		public Builder setAddress(String address) {
			this.address = address;
			return this;
		}

		public Builder setPort(Integer port) {
			this.port = port;
			return this;
		}

		public Client create() {
			return new Client(address, port);
		}
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
