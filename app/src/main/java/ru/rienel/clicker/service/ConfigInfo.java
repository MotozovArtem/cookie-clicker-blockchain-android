package ru.rienel.clicker.service;

public class ConfigInfo {

	public static final int MSG_RECV_PEER_INFO = 21;
	public static final int MSG_REPORT_SEND_PEER_INFO_RESULT = 22;
	public static final int MSG_SERVICE_POOL_START = 27;
	public static final int MSG_REPORT_RECV_PEER_LIST = 29;

	public static final int COMMAND_ID_SEND_PEER_INFO = 100;
	public static final int COMMAND_ID_SEND_FILE = 101;
	public static final int COMMAND_ID_REQUEST_SEND_FILE = 102;
	public static final int COMMAND_ID_RESPONSE_SEND_FILE = 103;
	public static final int COMMAND_ID_BROADCAST_PEER_LIST = 104;
	public static final int COMMAND_ID_SEND_STRING = 105;

	public static final int LISTEN_PORT = 8988;
	public static final int SOCKET_TIMEOUT = 5000;

}