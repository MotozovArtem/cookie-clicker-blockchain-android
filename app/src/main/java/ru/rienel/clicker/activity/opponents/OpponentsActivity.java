package ru.rienel.clicker.activity.opponents;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import ru.rienel.clicker.R;
import ru.rienel.clicker.service.NetworkService;

public class OpponentsActivity extends AppCompatActivity {
	public static final String TAG = OpponentsActivity.class.getName();

	private OpponentListFragment opponentListFragment;
	private OpponentsPresenter presenter;

	private ServiceConnection connection;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.opponents_activity);

		Toolbar opponentToolbar = findViewById(R.id.opponent_toolbar);
		opponentToolbar.setNavigationIcon(R.drawable.ic_back);
		setSupportActionBar(opponentToolbar);

		FragmentManager fragmentManager = getSupportFragmentManager();
		OpponentListFragment fragment =
				(OpponentListFragment) fragmentManager.findFragmentById(R.id.opponent_fragment_container);

		if (fragment == null) {
			fragment = new OpponentListFragment();
			fragmentManager.beginTransaction()
					.add(R.id.opponent_fragment_container, fragment)
					.commit();
		}

		presenter = new OpponentsPresenter(opponentListFragment);

		connection = presenter.getServiceConnection();

		Intent serviceIntent = NetworkService.newIntent(this);
		startService(serviceIntent);
		bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
	}

	private OpponentListFragment getFragment() {
		if (opponentListFragment == null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			opponentListFragment = (OpponentListFragment) fragmentManager
					.findFragmentById(R.id.opponent_fragment_container);
		}
		return opponentListFragment;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(connection);
		Log.d(TAG, "onDestroy: Service unbinded");
	}

//	static private class ActivityHandler extends Handler {
//		private static final String TAG = "ActivityHandler";
//		private OpponentsActivity activity;
//
//		ActivityHandler(OpponentsActivity activity) {
//			this.activity = activity;
//		}
//
//		@Override
//		public void handleMessage(Message msg) {
//			Log.d(TAG, "handleMessage()  msg.what:" + msg.what);
//			switch (msg.what) {
//				case ConfigInfo.MSG_RECV_PEER_INFO:
//					break;
//				case ConfigInfo.MSG_REPORT_SEND_PEER_INFO_RESULT:
//					break;
//				case ConfigInfo.MSG_REPORT_RECV_PEER_LIST:
//				default:
//			}
//			super.handleMessage(msg);
//		}
//	}
//
//	private Handler handler = new ActivityHandler(this);
}
