package ru.rienel.clicker.net.factory;

import java.util.concurrent.ThreadFactory;

import android.support.annotation.NonNull;

import ru.rienel.clicker.common.Preconditions;

public class ClientThreadFactory implements ThreadFactory {
	@Override
	public Thread newThread(@NonNull Runnable clientInstance) {
		Preconditions.checkNotNull(clientInstance);

		Thread clientThread = new Thread(clientInstance);
		clientThread.setName("Client");

		return clientThread;
	}
}
