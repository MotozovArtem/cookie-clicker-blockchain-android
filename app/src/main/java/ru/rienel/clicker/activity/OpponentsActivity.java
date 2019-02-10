package ru.rienel.clicker.activity;

import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import ru.rienel.clicker.R;
import ru.rienel.clicker.net.WifiP2pHelper;
import ru.rienel.clicker.service.NetworkService;
import ru.rienel.clicker.ui.view.OpponentListFragment;

public class OpponentsActivity extends AppCompatActivity {

	private NetworkService networkService;
	private WifiP2pHelper wifiP2pHelper;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.opponents_activity);

		wifiP2pHelper = new WifiP2pHelper(this);

		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(R.id.opponent_fragment_container);

		if (fragment == null) {
			fragment = new OpponentListFragment();
			wifiP2pHelper.discoverPeers();
			((OpponentListFragment) fragment).setOpponentList(wifiP2pHelper.getOpponents());
			fragmentManager.beginTransaction()
					.add(R.id.opponent_fragment_container, fragment)
					.commit();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(wifiP2pHelper.getReceiver(), wifiP2pHelper.getIntentFilter());
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(wifiP2pHelper.getReceiver());
	}
}
