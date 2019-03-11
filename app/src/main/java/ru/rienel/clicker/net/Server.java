package ru.rienel.clicker.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ru.rienel.clicker.common.Configuration;
import ru.rienel.clicker.common.SignalHandler;
import ru.rienel.clicker.net.factory.SendReceiveThreadFactory;

public class Server implements Runnable {
	private ServerSocket serverSocket;
	private Socket socket;
	private SendReceive sendReceiver;
	private SignalHandler signalHandler;
	private Thread sendReceiveThread;
	private SendReceiveThreadFactory factory;

	public Server(SignalHandler signalHandler) throws IOException {
		this.signalHandler = signalHandler;

		this.serverSocket = new ServerSocket(Configuration.SERVER_PORT);
		this.factory = new SendReceiveThreadFactory();
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(8888);
			socket = serverSocket.accept();

			sendReceiver = new SendReceive(socket, signalHandler);

			sendReceiveThread = factory.newThread(sendReceiver);
			sendReceiveThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
