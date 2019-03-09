package ru.rienel.clicker.net;

import java.util.concurrent.ThreadFactory;

import android.support.annotation.NonNull;

import ru.rienel.clicker.common.Preconditions;

public class ServerThreadFactory implements ThreadFactory {
	@Override
	public Thread newThread(@NonNull Runnable serverInstance) {
		Preconditions.checkNotNull(serverInstance);

		Thread serverThread = new Thread(serverInstance);
		serverThread.setName("Server");

		return serverThread;
	}
}
