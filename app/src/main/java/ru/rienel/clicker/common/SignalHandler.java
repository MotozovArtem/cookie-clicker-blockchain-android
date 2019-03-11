package ru.rienel.clicker.common;

import android.os.Handler;
import android.os.Message;

import ru.rienel.clicker.activity.game.GamePresenter;

public class SignalHandler extends Handler {
	private GamePresenter presenter;

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
			case Configuration.MessageConstants.MESSAGE_READ:
				byte[] readBuff = (byte[]) msg.obj;
				String tempMessage = new String(readBuff, 0, msg.arg1);
				break;
		}
	}
}
