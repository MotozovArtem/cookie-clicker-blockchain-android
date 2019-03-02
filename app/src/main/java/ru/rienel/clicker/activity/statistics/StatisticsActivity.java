package ru.rienel.clicker.activity.statistics;

import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import ru.rienel.clicker.R;
import ru.rienel.clicker.db.domain.Block;
import ru.rienel.clicker.db.domain.dao.Repository;
import ru.rienel.clicker.db.domain.dao.impl.BlockDaoImpl;

public class StatisticsActivity extends AppCompatActivity {

	private static final String TAG = StatisticsActivity.class.getName();
	private Repository<Block> blockRepository;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics_activity);

		blockRepository = new BlockDaoImpl(this);

		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(R.id.block_fragment_container);
		if (fragment == null) {
			fragment = new BlockListFragment();

			fragmentManager.beginTransaction()
					.add(R.id.block_fragment_container, fragment)
					.commit();
		}
	}


	@Override
	public void onResume() {
		super.onResume();
		TextView statisticTitle;
		statisticTitle = findViewById(R.id.statisticsTitle);

		Integer blocksCount = blockRepository.count();
		statisticTitle.setText(
				String.format(Locale.ENGLISH, "Statistics: %d games played",
						blocksCount - 1)); // -1 genesis block
	}
}
