package ru.rienel.clicker.net;

import java.util.ArrayDeque;
import java.util.Queue;

import ru.rienel.clicker.common.Configuration.MessageConstants;
import ru.rienel.clicker.common.Preconditions;
import ru.rienel.clicker.common.StringJoiner;

public class MessageProcessor {
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
		String lengthHeader = splitAtHeader[0];
		int lengthOfFirstMessage = Integer.parseInt(lengthHeader);
		if (hasCompleteMessage(lengthOfFirstMessage, splitAtHeader[1])) {
			String completeMsg = splitAtHeader[1].substring(0, lengthOfFirstMessage);
			messages.add(completeMsg);
			receivedChars.delete(0, lengthHeader.length() + MessageConstants.MESSAGE_LENGTH_DELIMITER.length() + lengthOfFirstMessage);
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
