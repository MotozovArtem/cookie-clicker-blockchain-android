package ru.rienel.clicker.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import ru.rienel.clicker.common.Configuration;
import ru.rienel.clicker.common.Configuration.MessageConstants;

public class ServerService extends IntentService {
	private static final String TAG = ServerService.class.getName();

	public static final String ACTION_SEND_FILE = "ru.rienel.clicker.SEND_FILE";
	public static final String EXTRAS_FILE_PATH = "file_url";
	public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
	public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";

	private ServerSocketChannel serverChannel;
	private SocketChannel clientChannel;

	public static Intent newIntent(Context context) {
		return new Intent(context, ServerService.class);
	}

	public ServerService(String name) {
		super(name);
	}

	public ServerService() {
		super("ServerService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Context context = getApplicationContext();
		System.out.println("Listening for connections on port " + Configuration.SERVER_PORT);
		ServerSocketChannel serverChannel;
		Selector selector;
		try {
			serverChannel = ServerSocketChannel.open();
			ServerSocket ss = serverChannel.socket();
			InetSocketAddress address = new InetSocketAddress(Configuration.SERVER_PORT);
			ss.bind(address);
			serverChannel.configureBlocking(false);
			selector = Selector.open();
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		while (true) {
			try {
				selector.select();
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
			Set<SelectionKey> readyKeys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = readyKeys.iterator();
			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
				iterator.remove();
				try {
					if (key.isAcceptable()) {
						ServerSocketChannel server = (ServerSocketChannel)key.channel();
						SocketChannel client = server.accept();
						System.out.println("Accepted connection from " + client);
						client.configureBlocking(false);
						SelectionKey key2 = client.register(selector, SelectionKey.OP_WRITE);
						ByteBuffer buffer = ByteBuffer.allocate(MessageConstants.STANDARD_BUFFER_SIZE);
//						buffer.put(rotation, 0, 72);
//						buffer.put((byte)'\r');
//						buffer.put((byte)'\n');
						buffer.flip();
						key2.attach(buffer);
					} else if (key.isWritable()) {
						SocketChannel client = (SocketChannel)key.channel();
						ByteBuffer buffer = (ByteBuffer)key.attachment();
						if (!buffer.hasRemaining()) {
							// Refill the buffer with the next line
							buffer.rewind();
							// Get the old first character
							int first = buffer.get();
							// Get ready to change the data in the buffer
							buffer.rewind();
							// Find the new first characters position in rotation
							int position = first - ' ' + 1;
							// copy the data from rotation into the buffer
//							buffer.put(rotation, position, 72);
							// Store a line break at the end of the buffer
//							buffer.put((byte)'\r');
//							buffer.put((byte)'\n');
							// Prepare the buffer for writing
							buffer.flip();
						}
						client.write(buffer);
					}
				} catch (IOException e) {
					key.cancel();
					try {
						key.channel().close();
					} catch (IOException ex) {
					}
				}
			}
		}
	}


}