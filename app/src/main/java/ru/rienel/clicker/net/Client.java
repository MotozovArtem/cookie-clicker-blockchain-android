package ru.rienel.clicker.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.Queue;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.rienel.clicker.common.Configuration.MessageConstants;
import ru.rienel.clicker.common.Preconditions;
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
	private final Queue<ByteBuffer> messageToSend = new ArrayDeque<>();
	private List<EventListener> listeners = new ArrayList<>();
	private Selector selector;
	private SocketChannel client;
	private InetSocketAddress serverAddress;
	private MessageProcessor messageProcessor = new MessageProcessor();

	private boolean connected;
	private boolean timeToSend;

	private String address;
	private Integer port;

	public Client() {
	}

	@Override
	public void run() {
		serverAddress = new InetSocketAddress(address, port);
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

	private void initConnection() throws IOException {
		client = SocketChannel.open();
		client.configureBlocking(false);
		client.connect(serverAddress);
		connected = true;
	}

	private void initSelector() throws IOException {
		selector = Selector.open();
		client.register(selector, SelectionKey.OP_CONNECT);
	}

	private void completeConnection(SelectionKey key) throws IOException {
		client.finishConnect();
		key.interestOps(SelectionKey.OP_READ);
	}

	private void receive(SelectionKey key) throws IOException {
		buffer.clear();
		int numberOfReadBytes = client.read(buffer);
		if (numberOfReadBytes == 1) {
			Log.e(TAG, "receive: " + FATAL_COMMUNICATION_MSG);
			throw new IOException(FATAL_COMMUNICATION_MSG);
		}

		String received = extractMessageFromBuffer();
		messageProcessor.appendReceivedMessage(received);
		while (messageProcessor.hasNext()) {
			String msg = messageProcessor.nextMsg();
			Signal signal = GSON.fromJson(MessageProcessor.getMessageBody(msg), Signal.class);
			notifyMessageReceived(signal);
		}
	}

	private String extractMessageFromBuffer() {
		buffer.flip();
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes);
		return new String(bytes);
	}

	private void send(SelectionKey key) throws IOException {
		ByteBuffer buffer;
		synchronized (messageToSend) {
			while ((buffer = messageToSend.peek()) != null) {
				client.write(buffer);
				if (buffer.hasRemaining()) {
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	// Notifications
	public void notifyMessageReceived(Signal signal) {
		ClientNetworkEvent event = new ClientNetworkEvent(this, signal);
		for (EventListener eventListener : listeners) {
			if (eventListener instanceof Server.ServerNetworkListener) {
				try {
					((ClientNetworkListener)eventListener).receivedSignal(event);
				} catch (Exception e) {
					Log.e(TAG, "notifyMessageReceived: listener error", e);
				}
			}
		}
	}

	public void notifyConnectionDone(Signal signal) {
		ClientNetworkEvent event = new ClientNetworkEvent(this, signal);
		for (EventListener eventListener : listeners) {
			if (eventListener instanceof Server.ServerNetworkListener) {
				try {
					((ClientNetworkListener)eventListener).connected(event);
				} catch (Exception e) {
					Log.e(TAG, "notifyConnectionDone: listener error", e);
				}
			}
		}
	}

	public void notifyDisconnectionDone() {
		ClientNetworkEvent event = new ClientNetworkEvent(this, null);
		for (EventListener eventListener : listeners) {
			if (eventListener instanceof Server.ServerNetworkListener) {
				try {
					((ClientNetworkListener)eventListener).disconnected(event);
				} catch (Exception e) {
					Log.e(TAG, "notifyDisconnectionDone: listener error", e);
				}
			}
		}
	}

	// events
	public void addListener(EventListener listener) {
		Preconditions.checkNotNull(listener);
		listeners.add(listener);
	}

	public void removeListener(EventListener listener) {
		Preconditions.checkNotNull(listener);
		listeners.remove(listener);
	}


	public static class ClientNetworkEvent extends EventObject {
		private final Signal signal;

		public ClientNetworkEvent(Object source, Signal signal) {
			super(source);
			Preconditions.checkNotNull(signal);
			this.signal = signal;
		}

		public Signal getSignal() {
			return signal;
		}
	}

	public interface ClientNetworkListener extends EventListener {
		void connected(ClientNetworkEvent event);

		void disconnected(ClientNetworkEvent event);

		void receivedSignal(ClientNetworkEvent event);
	}
}
