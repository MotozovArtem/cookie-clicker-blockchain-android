package ru.rienel.clicker.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import ru.rienel.clicker.R;
import ru.rienel.clicker.db.domain.Block;
import ru.rienel.clicker.db.domain.dao.DaoException;
import ru.rienel.clicker.db.domain.dao.DataAccessObject;
import ru.rienel.clicker.db.domain.dao.impl.BlockDaoImpl;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

	private static final String TAG = StatisticsActivity.class.getName();

	private DataAccessObject<Block> blockAccess;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_statistics);
		blockAccess = new BlockDaoImpl(this);
	}


	@Override
	public void onResume() {
		super.onResume();
		ListView statistics;
		TextView statisticTitle;
		statistics = findViewById(R.id.lvStatistics);
		statisticTitle = findViewById(R.id.statisticsTitle);
		List<Block> blocks = null;
		try {
			blocks = blockAccess.findAll();
		} catch (DaoException e) {
			Log.v(TAG, e.getMessage());
			blocks = Collections.emptyList();
		}

		BaseAdapter adapter = new StatisticsAdapter(this, blocks);

		statistics.setAdapter(adapter);
		statisticTitle.setText(String.format(Locale.ENGLISH, "Statistics %d games out of 10", blocks.size()));

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private class StatisticsAdapter extends BaseAdapter {
		Context context;
		LayoutInflater inflater;

		List<Block> objects;

		StatisticsAdapter(Context context, List<Block> blocks) {
			this.context = context;
			objects = blocks;
			inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		// элемент по позиции
		@Override
		public Object getItem(int position) {
			return objects.get(position);
		}

		// кол-во элементов
		@Override
		public int getCount() {
			return objects.size();
		}

		// id по позиции
		@Override
		public long getItemId(int position) {
			return position;
		}

		// point по позиции
		int getPoint(int position) {
			return ((int) getItem(position));
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = inflater.inflate(R.layout.item, parent, false);
			}

			int point = getPoint(position);

			((TextView) view.findViewById(R.id.statisticsPoints)).
					setText(String.format(Locale.ENGLISH, "%d) You won: %d blocks", position + 1, point));
			return view;
		}

	}

}
