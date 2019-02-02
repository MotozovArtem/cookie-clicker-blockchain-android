package ru.rienel.cookie_clicker_blockchain_android;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.klimo.myapplication.R;

import java.util.List;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

	private ListView lvStatistics;
	private TextView tvStatisticTitle;
	private List<Integer> points;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_statistics);

		db = new DatabaseHandler(this);

		lvStatistics = findViewById(R.id.lvStatistics);
		tvStatisticTitle = findViewById(R.id.statisticsTitle);
	}


	@Override
	public void onResume() {
		super.onResume();
		points = db.getAllPoints();
		BaseAdapter adapter = new StatisticsAdapter(this, points);
		lvStatistics.setAdapter(adapter);
		tvStatisticTitle.setText(String.format(Locale.ENGLISH, "Statistics %d games out of 10", db.getPointsCount()));

	}

	private class StatisticsAdapter extends BaseAdapter {
		Context ctx;
		LayoutInflater lInflater;
		List<Integer> objects;

		StatisticsAdapter(Context context, List<Integer> points) {
			ctx = context;
			objects = points;
			lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
				view = lInflater.inflate(R.layout.item, parent, false);
			}

			int point = getPoint(position);

			((TextView) view.findViewById(R.id.statisticsPoints)).setText(String.format(Locale.ENGLISH, "%d) You won: %d points", position + 1, point));

			return view;
		}
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
