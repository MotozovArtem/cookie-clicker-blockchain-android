package ru.rienel.clicker.common;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import ru.rienel.clicker.activity.OpponentsActivity;
import ru.rienel.clicker.service.ConfigInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class Utility {
	private static final String TAG = Utility.class.getName();
	private static final int BUFFER_SIZE = 1024;

	public static final String[][] MIME_MAP_TABLE = {
			{".3gp", "video/3gpp"},
			{".apk", "application/vnd.android.package-archive"},
			{".asf", "video/x-ms-asf"},
			{".avi", "video/x-msvideo"},
			{".bin", "application/octet-stream"},
			{".bmp", "image/bmp"},
			{".c", "text/plain"},
			{".class", "application/octet-stream"},
			{".conf", "text/plain"},
			{".cpp", "text/plain"},
			{".doc", "application/msword"},
			{".exe", "application/octet-stream"},
			{".gif", "image/gif"},
			{".gtar", "application/x-gtar"},
			{".gz", "application/x-gzip"},
			{".h", "text/plain"},
			{".htm", "text/html"},
			{".html", "text/html"},
			{".jar", "application/java-archive"},
			{".java", "text/plain"},
			{".jpeg", "image/jpeg"},
			{".jpg", "image/jpeg"},
			{".js", "application/x-javascript"},
			{".log", "text/plain"},
			{".m3u", "audio/x-mpegurl"},
			{".m4a", "audio/mp4a-latm"},
			{".m4b", "audio/mp4a-latm"},
			{".m4p", "audio/mp4a-latm"},
			{".m4u", "video/vnd.mpegurl"},
			{".m4v", "video/x-m4v"},
			{".mov", "video/quicktime"},
			{".mp2", "audio/x-mpeg"},
			{".mp3", "audio/x-mpeg"},
			{".mp4", "video/mp4"},
			{".mpc", "application/vnd.mpohun.certificate"},
			{".mpe", "video/mpeg"},
			{".mpeg", "video/mpeg"},
			{".mpg", "video/mpeg"},
			{".mpg4", "video/mp4"},
			{".mpga", "audio/mpeg"},
			{".msg", "application/vnd.ms-outlook"},
			{".ogg", "audio/ogg"},
			{".pdf", "application/pdf"},
			{".png", "image/png"},
			{".pps", "application/vnd.ms-powerpoint"},
			{".ppt", "application/vnd.ms-powerpoint"},
			{".prop", "text/plain"},
			{".rar", "application/x-rar-compressed"},
			{".rc", "text/plain"},
			{".rmvb", "audio/x-pn-realaudio"},
			{".rtf", "application/rtf"},
			{".sh", "text/plain"},
			{".tar", "application/x-tar"},
			{".tgz", "application/x-compressed"},
			{".txt", "text/plain"},
			{".wav", "audio/x-wav"},
			{".wma", "audio/x-ms-wma"},
			{".wmv", "audio/x-ms-wmv"},
			{".wps", "application/vnd.ms-works"},
			{".xml", "text/plain"},
			{".z", "application/x-compress"},
			{".zip", "application/zip"},
			{"", "*/*"}
	};

	private static String getMIMEType(File file) {
		String type = "*/*";
		String fileName = file.getName();
		int pointIndex = fileName.lastIndexOf(".");
		if (pointIndex < 0) {
			return type;
		}
		String end = fileName.substring(pointIndex, fileName.length()).toLowerCase();
		if (end.equals("")) {
			return type;
		}
		for (int i = 0; i < MIME_MAP_TABLE.length; i++) {
			if (end.equals(MIME_MAP_TABLE[i][0]))
				type = MIME_MAP_TABLE[i][1];
		}
		return type;
	}

	public static void openFile(Activity activity, File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		String type = getMIMEType(file);
		intent.setDataAndType(Uri.fromFile(file), type);
		activity.startActivity(intent);
	}

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements(); ) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (inetAddress instanceof Inet4Address)
						if (!inetAddress.isLoopbackAddress()) {
							return inetAddress.getHostAddress();
						}
				}
			}
		} catch (SocketException | NullPointerException e) {
			Log.e(TAG, "getLocalIPAddress()", e);
		}
		return null;
	}

	public static boolean sendPeerInfo(String host, int port) {
		Socket socket = new Socket();
		String strIP = getLocalIpAddress();
		boolean result = true;
		Log.d(OpponentsActivity.TAG, "peer:" + strIP);
		try {
			Log.d(OpponentsActivity.TAG, "Opening client socket - ");
			socket.bind(null);
			socket.connect((new InetSocketAddress(host, port)), ConfigInfo.SOCKET_TIMEOUT);// host

			Log.d(OpponentsActivity.TAG,
					"Client socket - " + socket.isConnected());
			OutputStream outputStream = socket.getOutputStream();
			outputStream.write(ConfigInfo.COMMAND_ID_SEND_PEER_INFO);// id
			String strSend = "peer:" + strIP + "port:" + port;
			outputStream.write(strSend.getBytes(), 0, strSend.length());
			Log.d(OpponentsActivity.TAG, "Client: Data written strSend:"
					+ strSend);

		} catch (IOException e) {
			Log.e(OpponentsActivity.TAG, e.getMessage());
			result = false;
		} finally {
			if (socket != null) {
				if (socket.isConnected()) {
					try {
						socket.close();
						Log.d(OpponentsActivity.TAG, "socket.close();");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return result;
	}

	public static boolean sendFileInfo(String name, int size, String host, int port) {
		Socket socket = new Socket();
		try {
			Log.d(OpponentsActivity.TAG, "Opening client socket - ");
			socket.bind(null);
			socket.connect((new InetSocketAddress(host, port)), ConfigInfo.SOCKET_TIMEOUT);

			Log.d(OpponentsActivity.TAG,
					"Client socket - " + socket.isConnected());
			OutputStream stream = socket.getOutputStream();
			String strSend = "size:" + size + "name:" + name;
			stream.write(ConfigInfo.COMMAND_ID_REQUEST_SEND_FILE);// id
			stream.write(strSend.length());
			stream.write(strSend.getBytes(), 0, strSend.length());
			Log.d(OpponentsActivity.TAG, "Client: Data written strSend:"
					+ strSend);
			return true;

		} catch (IOException e) {
			Log.e(OpponentsActivity.TAG, e.getMessage());
			return false;
		} finally {
			if (socket != null) {
				if (socket.isConnected()) {
					try {
						socket.close();
						Log.d(OpponentsActivity.TAG, "socket.close();");
					} catch (IOException e) {
						// Give up
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static Pair<String, Integer> getFileNameAndSize(Activity activaty, Uri uri)
			throws IOException {
		String[] proj = {MediaStore.Images.Media.DATA, MediaStore.Video.Media.DATA,
				MediaStore.Audio.Media.DATA, MediaStore.Files.FileColumns.DATA};
		Cursor actualimagecursor = activaty.managedQuery(uri,
				proj, null, null, null);
		int actual_image_column_index = actualimagecursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		String img_path = actualimagecursor
				.getString(actual_image_column_index);
		File file = new File(img_path);
		FileInputStream fis = new FileInputStream(file);
		int fileLen = fis.available();
		fis.close();
		return new Pair<>(file.getName(), fileLen);
	}

	public static boolean sendStream(String host, int port, InputStream data) {
		Socket socket = new Socket();
		boolean result = true;

		try {
			Log.d(OpponentsActivity.TAG, "Opening client socket - ");
			socket.bind(null);
			socket.connect((new InetSocketAddress(host, port)), ConfigInfo.SOCKET_TIMEOUT);// host

			Log.d(OpponentsActivity.TAG,
					"Client socket - " + socket.isConnected());
			OutputStream stream = socket.getOutputStream();
			copyStream(data, stream);
			Log.d(OpponentsActivity.TAG, "Client: Data written data's length:" + data.available());

		} catch (IOException e) {
			Log.e(OpponentsActivity.TAG, e.getMessage());
			result = false;
		} finally {
			if (socket != null) {
				if (socket.isConnected()) {
					try {
						socket.close();
						Log.d(OpponentsActivity.TAG, "socket.close();");
					} catch (IOException e) {
						// Give up
						e.printStackTrace();
					}
				}
			}
		}
		return result;
	}

	public static long copyStream(InputStream inputStream, OutputStream outputStream) {
		long copyLength = 0;
		byte buffer[] = new byte[BUFFER_SIZE];
		int len;
		try {
			while ((len = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
				copyLength += len;
			}
		} catch (IOException e) {
			Log.d(OpponentsActivity.TAG, e.toString());
			return 0;
		}
		return copyLength;
	}

}
