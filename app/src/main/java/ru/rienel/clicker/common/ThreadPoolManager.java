package ru.rienel.clicker.common;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import ru.rienel.clicker.service.ConfigInfo;
import ru.rienel.clicker.service.HandleAcceptSocket;
import ru.rienel.clicker.service.NetworkService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager extends HandlerThread {
	private final static String TAG = ThreadPoolManager.class.getName();

	private final ServerSocket serverSocket;
	private final ExecutorService pool;
	private final NetworkService netService;
	private boolean isServiceRun = true;

	private Handler handler = null;

	public ThreadPoolManager(NetworkService netService, int port, int poolSize) throws IOException {
		super(TAG, Process.THREAD_PRIORITY_FOREGROUND);
		if (poolSize > 0) {
			throw new IllegalArgumentException("PoolSize should be greater than 0");
		}
		this.netService = netService;
		serverSocket = new ServerSocket(port);
		pool = Executors.newFixedThreadPool(poolSize);
		Log.d(TAG, "ThreadPoolManager: constructor finished");
	}

	final void setServiceRun(boolean isRun) {
		this.isServiceRun = isRun;
	}

	final boolean isServiceRun() {
		return isServiceRun;
	}

	public Handler getHandler() {
		return handler;
	}

	static private class ServiceThreadHandler extends Handler {
		private ThreadPoolManager thread;

		ServiceThreadHandler(ThreadPoolManager service) {
			super(service.getLooper());
			this.thread = service;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case ConfigInfo.MSG_SERVICE_POOL_START:
					while (thread.isServiceRun()) {
						try {
							Log.d(TAG, "run ...");
							Socket sock = thread.serverSocket.accept();
							thread.pool.execute(new HandleAcceptSocket(thread.netService, sock));
						} catch (IOException ex) {
							Log.e(TAG, "IOException ex:" + ex);
							thread.pool.shutdown();
							break;
						}
					}
					break;
			}
			super.handleMessage(msg);
		}
	}

	public void init() {
		Log.d(this.getName(), "init - isAlive " + isAlive());
		setServiceRun(true);
		if (!this.isAlive()) {
			this.start();
			handler = new ServiceThreadHandler(this);
		}
		Message msg = new Message();
		msg.what = ConfigInfo.MSG_SERVICE_POOL_START;
		getHandler().sendMessage(msg);
	}

	public void cleanup() {
		Log.d(this.getName(), "cleanup");
		setServiceRun(false);
	}

	public void execute(Runnable command) {
		pool.execute(command);
	}

	public void myDestroy() {
		setServiceRun(false);
		shutdownAndAwaitTermination();
		this.quit();
	}

	private void shutdownAndAwaitTermination() {
		pool.shutdown();
		try {
			if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
				pool.shutdownNow();
				if (!pool.awaitTermination(60, TimeUnit.SECONDS))
					System.err.println("Pool did not terminate");
			}
			if (!serverSocket.isClosed()) {
				serverSocket.close();
			}
		} catch (InterruptedException ie) {
			pool.shutdownNow();
			Thread.currentThread().interrupt();
			Log.e(TAG, "InterruptedException ie:", ie);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "IOException e:", e);
		}
	}


}


