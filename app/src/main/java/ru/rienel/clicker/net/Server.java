package ru.rienel.clicker.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.rienel.clicker.common.Configuration;
import ru.rienel.clicker.common.Configuration.MessageConstants;
import ru.rienel.clicker.net.model.Signal.SignalType;
import ru.rienel.clicker.net.model.Signal.SignalTypeDeserializer;
import ru.rienel.clicker.net.model.Signal.SignalTypeSerializer;

public class Server implements Runnable {
	private static final String TAG = Server.class.getName();
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(SignalType.class, SignalTypeDeserializer.class)
			.registerTypeAdapter(SignalType.class, SignalTypeSerializer.class)
			.create();

	private Selector selector;
	private ServerSocketChannel serverSocketChannel;
	private ByteBuffer buffer = ByteBuffer.allocateDirect(MessageConstants.STANDARD_BUFFER_SIZE);

	@Override
	public void run() {
		selector = null;
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(Configuration.SERVER_PORT));
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			while (true) {
				selector.select();
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> iter = selectedKeys.iterator();
				while (iter.hasNext()) {
					SelectionKey key = iter.next();
					iter.remove();

					if (key.isAcceptable()) {
						register(selector, serverSocketChannel);
					} else if (key.isReadable()) {
						SocketChannel client = (SocketChannel)key.channel();
						client.read(buffer);
						System.out.println(new String(buffer.array()));


					} else if (key.isWritable()) {
						SocketChannel client = (SocketChannel)key.channel();
						buffer.flip();
						client.write(buffer);
						buffer.clear();
					}
				}
			}
		} catch (IOException e) {
			Log.e(TAG, "run: ", e);
		}
	}

	private static void answerWithEcho(ByteBuffer buffer, SelectionKey key)
			throws IOException {
	}

	private void register(Selector selector, ServerSocketChannel serverSocket)
			throws IOException {

		SocketChannel client = serverSocket.accept();
		client.configureBlocking(false);
		client.register(selector, SelectionKey.OP_READ);
	}
}