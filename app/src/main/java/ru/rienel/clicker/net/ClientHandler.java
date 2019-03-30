package ru.rienel.clicker.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import ru.rienel.clicker.common.Configuration.MessageConstants;

public class ClientHandler implements Runnable {
	private static final String JOIN_MESSAGE = " joined to conversation";
	private static final String USERNAME_DELIMITER = ": ";
	private static final String LEAVE_MESSAGE = " left conversation";

	private final Server server;
	private final SocketChannel clientChannel;
	private MessageProcessor msgSplitter = new MessageProcessor();
	private String username = "anonymous";
	private ByteBuffer msgFromClient = ByteBuffer.allocateDirect(MessageConstants.STANDARD_BUFFER_SIZE);

	public ClientHandler(Server server, SocketChannel clientChannel) {
		this.server = server;
		this.clientChannel = clientChannel;
	}

	public void sendMsg(ByteBuffer msg) throws IOException {

	}

	public void disconnectClient() throws IOException {
		clientChannel.close();
	}

	public void recvMsg() throws IOException {
		msgFromClient.clear();
		int numOfReadBytes;
		numOfReadBytes = clientChannel.read(msgFromClient);
		if (numOfReadBytes == -1) {
			throw new IOException("Client has close connection");
		}
		String receivedMessage = extractMessageFromBuffer();
		msgSplitter.appendReceivedMessage(receivedMessage);
	}

	private String extractMessageFromBuffer() {
		msgFromClient.flip();
		byte[] bytes = new byte[msgFromClient.remaining()];
		msgFromClient.get();
		return new String(bytes);
	}

	@Override
	public void run() {
	}

	private class Message {
		private String msgBody;
		private String receivedString;

		private Message(String receivedString) {
			parse(receivedString);
			this.receivedString = receivedString;
		}

		private void parse(String stringToParse) {
		}

		private boolean hasBody(String[] msgTokens) {
			return msgTokens.length > 1;
		}
	}
}
