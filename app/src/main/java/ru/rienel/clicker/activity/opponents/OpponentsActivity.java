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
import ru.rienel.clicker.net.Server;
import ru.rienel.clicker.service.NetworkService;

public class OpponentsActivity extends AppCompatActivity {
	public static final String TAG = OpponentsActivity.class.getName();

	private OpponentListFragment opponentListFragment;
	private OpponentsPresenter presenter;
	private Server server;
	private ServiceConnection connection;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.opponents_activity);

		Toolbar opponentToolbar = findViewById(R.id.opponent_toolbar);
		opponentToolbar.setNavigationIcon(R.drawable.ic_back);
		setSupportActionBar(opponentToolbar);

		FragmentManager fragmentManager = getSupportFragmentManager();
		opponentListFragment =
				(OpponentListFragment)fragmentManager.findFragmentById(R.id.opponent_fragment_container);

		if (opponentListFragment == null) {
			opponentListFragment = OpponentListFragment.newInstance();
			fragmentManager.beginTransaction()
					.add(R.id.opponent_fragment_container, opponentListFragment)
					.commit();
		}

		server = Server.getInstance();

		presenter = new OpponentsPresenter(opponentListFragment, server);

		connection = presenter.newServiceConnection();
		Intent serviceIntent = NetworkService.newIntent(this);
		startService(serviceIntent);
		bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(connection);
		Log.d(TAG, "onDestroy: Service unbinded");
	}
}
