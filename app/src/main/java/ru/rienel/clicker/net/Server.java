package ru.rienel.clicker.net;

import java.io.IOException;
import java.net.ServerSocket;

import ru.rienel.clicker.common.Configuration;

public class Server implements Runnable {
	private ServerSocket serverSocket;

	public Server() throws IOException {
		serverSocket = new ServerSocket(Configuration.SERVER_PORT);
	}

	@Override
	public void run() {

	}
}
