package ru.rienel.clicker.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.rienel.clicker.common.Configuration.MessageConstants;
import ru.rienel.clicker.net.model.Signal;
import ru.rienel.clicker.net.model.Signal.SignalType;
import ru.rienel.clicker.net.model.Signal.SignalTypeDeserializer;
import ru.rienel.clicker.net.model.Signal.SignalTypeSerializer;

public class LocalClientHandler {
	private static final String TAG = LocalClientHandler.class.getName();
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(SignalType.class, new SignalTypeDeserializer())
			.registerTypeAdapter(SignalType.class, new SignalTypeSerializer())
			.create();

	private final Server server;
	private final SocketChannel clientChannel;
	private MessageProcessor messageProcessor = new MessageProcessor();
	private ByteBuffer buffer = ByteBuffer.allocateDirect(MessageConstants.STANDARD_BUFFER_SIZE);

	public LocalClientHandler(Server server, SocketChannel clientChannel) {
		this.server = server;
		this.clientChannel = clientChannel;
	}

	public void send(ByteBuffer msg) throws IOException {
		clientChannel.write(msg);
	}

	public void disconnect() throws IOException {
		clientChannel.close();
	}

	public void receive() throws IOException {
		buffer.clear();
		int numOfReadBytes;
		numOfReadBytes = clientChannel.read(buffer);
		if (numOfReadBytes == -1) {
			Log.e(TAG, "receive: client has close connection");
			throw new IOException("Client has close connection");
		}
		String receivedMessage = extractMessageFromBuffer();
		messageProcessor.appendReceivedMessage(receivedMessage);
		while (messageProcessor.hasNext()) {
			String message = messageProcessor.nextMsg();
			Signal signal = GSON.fromJson(message, Signal.class);
			server.notifyMessageReceived(signal);
		}
	}

	private String extractMessageFromBuffer() {
		buffer.flip();
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get();
		return new String(bytes);
	}
}
