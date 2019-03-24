package ru.rienel.clicker.net;

import java.lang.reflect.Type;
import java.util.Objects;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

public class Signal {
	@SerializedName("message")
	private String message;

	@SerializedName("signalType")
	private SignalType signalType;

	@SerializedName("ipAddress")
	private String ipAddress;

	public Signal(String message, SignalType signalType, String ipAddress) {
		this.message = message;
		this.signalType = signalType;
		this.ipAddress = ipAddress;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public SignalType getSignalType() {
		return signalType;
	}

	public void setSignalType(SignalType signalType) {
		this.signalType = signalType;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public enum SignalType {
		GAME_OVER(0),
		INVITE(1),
		DISCARD(2);

		int code;

		SignalType(int code) {
			this.code = code;
		}
	}

	public static class SignalTypeDeserializer implements JsonDeserializer<SignalType> {

		@Override
		public SignalType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			SignalType[] types = SignalType.values();
			for (SignalType type : types) {
				if (Objects.equals(type.code, json.getAsInt())) {
					return type;
				}
			}
			return null;
		}
	}

	public static class SignalTypeSerialize implements JsonSerializer<SignalType> {
		@Override
		public JsonElement serialize(SignalType src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.code);
		}
	}
}
