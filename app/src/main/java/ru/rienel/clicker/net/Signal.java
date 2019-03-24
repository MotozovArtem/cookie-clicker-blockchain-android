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
import ru.rienel.clicker.net.dto.OpponentDto;

public class Signal {
	@SerializedName("message")
	private String message;

	@SerializedName("signalType")
	private SignalType signalType;

	@SerializedName("opponent")
	private OpponentDto opponent;


	public Signal(String message, SignalType signalType, OpponentDto opponent) {
		this.message = message;
		this.signalType = signalType;
		this.opponent = opponent;
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

	public OpponentDto getOpponent() {
		return opponent;
	}

	public void setOpponent(OpponentDto opponent) {
		this.opponent = opponent;
	}

	public enum SignalType {
		GAME_OVER(0),
		ACCEPT(1),
		INVITE(2),
		DISCARD(3);

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
