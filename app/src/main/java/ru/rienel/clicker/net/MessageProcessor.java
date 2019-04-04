package ru.rienel.clicker.net;

import java.util.ArrayDeque;
import java.util.Queue;

import ru.rienel.clicker.common.Configuration.MessageConstants;
import ru.rienel.clicker.common.Preconditions;
import ru.rienel.clicker.common.StringJoiner;

public class MessageProcessor {
	public static final int MESSAGE_LENGTH_INDEX = 1;
	public static final int MESSAGE_BODY_INDEX = 2;
	private StringBuilder receivedChars = new StringBuilder();
	private final Queue<String> messages = new ArrayDeque<>();

	public synchronized String nextMsg() {
		return messages.poll();
	}

	public synchronized boolean hasNext() {
		return !messages.isEmpty();
	}

	public synchronized void appendReceivedMessage(String receivedMessage) {
		receivedChars.append(receivedMessage);
		while (extractMessage()) ;
	}

	private boolean extractMessage() {
		String allReceivedChars = receivedChars.toString();
		String[] splitAtHeader = allReceivedChars.split(MessageConstants.MESSAGE_LENGTH_DELIMITER);
		if (splitAtHeader.length < 2) {
			return false;
		}
		String lengthHeader = splitAtHeader[MESSAGE_LENGTH_INDEX];
		int lengthOfFirstMessage = Integer.parseInt(lengthHeader);
		if (hasCompleteMessage(lengthOfFirstMessage, splitAtHeader[MESSAGE_BODY_INDEX])) {
			String completeMsg = splitAtHeader[MESSAGE_BODY_INDEX].substring(0, lengthOfFirstMessage);
			messages.add(completeMsg);
			receivedChars.delete(0, allReceivedChars.length());
			return true;
		}
		return false;
	}

	private boolean hasCompleteMessage(int lengthHeader, String message) {
		return message.length() >= lengthHeader;
	}

	public static String getMessageBody(String message) {
		Preconditions.checkNotNull(message);

		String[] messageParts = message.split(MessageConstants.MESSAGE_LENGTH_DELIMITER);
		return messageParts[MessageConstants.MESSAGE_BODY_INDEX];
	}

	public static String prependLengthHeader(String messageWithoutHeader) {
		Preconditions.checkNotNull(messageWithoutHeader);

		StringJoiner joiner = new StringJoiner(MessageConstants.MESSAGE_LENGTH_DELIMITER);
		joiner.add(Integer.toString(messageWithoutHeader.length()));
		joiner.add(messageWithoutHeader);
		return joiner.toString();
	}
}
