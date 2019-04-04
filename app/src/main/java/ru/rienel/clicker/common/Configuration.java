package ru.rienel.clicker.common;

public class Configuration {
	public static final Integer SERVER_PORT = 8888;
	public static final Integer SECONDARY_SERVER_PORT = 8889;

	public static final Integer CLIENT_PORT = 8880;
	public static final Integer SECONDARY_CLIENT_PORT = 8881;

	public static final Integer LINGER_TIME = 5000;

	public static final Integer TIMEOUT = 5 * 1000;

	public static final class MessageConstants {
		public static final int STANDARD_BUFFER_SIZE = 1024;
		public static final String MESSAGE_LENGTH_DELIMITER = "###";
		public static final Integer MESSAGE_BODY_INDEX = 1;
	}

	public static final class SharedPreferencesKeys {
		public static final String PREFERENCES_NAME = "cookie_settings";
		public static final String PREFERENCES_DONUT_ID = "donut_id";
		public static final String PREFERENCES_VOLUME_MUSIC  = "volume_music";
		public static final String PREFERENCES_VOLUME_EFFECT  = "volume_effect";
	}
}
