package ru.rienel.clicker.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import ru.rienel.clicker.R;
import ru.rienel.clicker.ui.view.OpponentListFragment;

public class OpponentsActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.opponents_activity);

		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(R.id.opponent_fragment_container);

		if (fragment == null) {
			fragment = new OpponentListFragment();
			fragmentManager.beginTransaction()
					.add(R.id.opponent_fragment_container, fragment)
					.commit();
		}
	}
}
