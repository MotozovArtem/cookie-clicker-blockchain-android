package ru.rienel.clicker.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.rienel.clicker.common.Configuration;
import ru.rienel.clicker.common.Configuration.MessageConstants;
import ru.rienel.clicker.net.model.Signal;
import ru.rienel.clicker.net.model.Signal.SignalType;
import ru.rienel.clicker.net.model.Signal.SignalTypeDeserializer;
import ru.rienel.clicker.net.model.Signal.SignalTypeSerializer;

public class Server implements Runnable {
	private static final String TAG = Server.class.getName();
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(SignalType.class, new SignalTypeDeserializer())
			.registerTypeAdapter(SignalType.class, new SignalTypeSerializer())
			.create();

	private Selector selector;
	private ServerSocketChannel serverSocketChannel;
	private ByteBuffer buffer = ByteBuffer.allocateDirect(MessageConstants.STANDARD_BUFFER_SIZE);

	private static Server instance;

	public static Server getInstance() {
		if (instance == null) {
			instance = new Server();
		}
		return instance;
	}

	private Server() {
		// do nothing
	}

	@Override
	public void run() {
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(Configuration.SERVER_PORT));
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			while (true) {
				selector.select();
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> iter = selectedKeys.iterator();
				while (iter.hasNext()) {
					SelectionKey key = iter.next();
					iter.remove();

					if (key.isAcceptable()) {
						register(selector, serverSocketChannel);
						Log.i(TAG, "run: device registered and connected");
					} else if (key.isReadable()) {
						receive(key);
						Log.i(TAG, "run: message received");
					} else if (key.isWritable()) {
						send(key);
						Log.i(TAG, "run: send message to client");
					}
				}
			}
		} catch (IOException e) {
			Log.e(TAG, "run: ", e);
		}
	}

	private void send(SelectionKey key) throws IOException {
		LocalClient client = (LocalClient)key.attachment();
		try {
			client.sendAll();
			key.interestOps(SelectionKey.OP_READ);
		} catch (IOException e) {
			removeClient(key);
		}
	}

	private void receive(SelectionKey key) throws IOException {
		LocalClient client = (LocalClient)key.attachment();
		try {
			client.handler.receive();
		} catch (IOException e) {
			removeClient(key);
		}
	}

	private void removeClient(SelectionKey key) throws IOException {
		LocalClient client = (LocalClient)key.attachment();
		client.handler.disconnect();
		key.cancel();
	}

	private void register(Selector selector, ServerSocketChannel serverSocket)
			throws IOException {
		SocketChannel client = serverSocket.accept();
		client.configureBlocking(false);
		client.register(selector, SelectionKey.OP_READ);
	}

	private ByteBuffer createMessage(Signal signal) {
		String signalJson = GSON.toJson(signal);
		String messageWithLengthHeader = MessageProcessor.prependLengthHeader(signalJson);
		return ByteBuffer.wrap(messageWithLengthHeader.getBytes());
	}

	public class LocalClient {
		private final LocalClientHandler handler;
		private final Queue<ByteBuffer> messagesToSend = new ArrayDeque<>();

		public LocalClient(LocalClientHandler handler, Signal signal) {
			this.handler = handler;
			messagesToSend.add(createMessage(signal));
		}

		public void addMessageToQueue(ByteBuffer msg) {
			synchronized (messagesToSend) {
				messagesToSend.add(msg.duplicate());
			}
		}

		public void addMessageToQueue(Signal signal) {
			String signalJson = GSON.toJson(signal);
			ByteBuffer buffer = ByteBuffer.wrap(signalJson.getBytes());
			synchronized (messagesToSend) {
				messagesToSend.add(buffer);
			}
		}

		private void sendAll() throws IOException {
			ByteBuffer msg = null;
			synchronized (messagesToSend) {
				try {
					while ((msg = messagesToSend.peek()) != null) {
						handler.send(msg);
						messagesToSend.remove();
					}
				} catch (IOException e) {

				}
			}
		}

	}
}