package ru.rienel.clicker.net.factory;

import java.util.concurrent.ThreadFactory;

import android.support.annotation.NonNull;

import ru.rienel.clicker.common.Preconditions;

public class SendReceiveThreadFactory implements ThreadFactory {
	@Override
	public Thread newThread(@NonNull Runnable sendReceiveInstance) {
		Preconditions.checkNotNull(sendReceiveInstance);

		Thread sendReceiveThread = new Thread(sendReceiveInstance);
		sendReceiveThread.setName("SendReceive");

		return sendReceiveThread;
	}
}
