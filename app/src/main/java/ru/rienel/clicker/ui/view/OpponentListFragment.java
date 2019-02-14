package ru.rienel.clicker.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import ru.rienel.clicker.R;
import ru.rienel.clicker.activity.MainActivity;
import ru.rienel.clicker.activity.OpponentsActivity;
import ru.rienel.clicker.common.Preconditions;
import ru.rienel.clicker.db.domain.Opponent;
import ru.rienel.clicker.service.NetworkService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class OpponentListFragment extends Fragment {
	private static final boolean HAS_MENU = true;

	private RecyclerView opponentRecyclerView;
	private OpponentAdapter opponentAdapter;
	private List<Opponent> opponentList;
	private OpponentsActivity opponentsActivity;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
	                         @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_opponent_list, container, false);
		opponentRecyclerView = view.findViewById(R.id.opponent_recycler_view);
		opponentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		if (opponentsActivity == null) {
			opponentsActivity = (OpponentsActivity) getActivity();
		}

		updateUi();
		return view;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_update:
				List<Opponent> newOpponentsList = updateOpponents();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private List<Opponent> updateOpponents() {
		List<Opponent> opponents = new LinkedList<>();
		return new ArrayList<>(opponents);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(HAS_MENU);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_opponent_list, menu);
	}

	private void updateUi() {
//		// TODO Get Opponents after network scan
//		List<Opponent> opponentList = new ArrayList<>();
//		// FIXME: 07.02.2019
//
//		Opponent testOpponent = new Opponent();
//		testOpponent.setName("Nokia");
//		opponentList.add(testOpponent);
//		testOpponent = new Opponent();
//		testOpponent.setName("iPhone");
//		opponentList.add(testOpponent);

		opponentAdapter = new OpponentAdapter(opponentList);
		opponentRecyclerView.setAdapter(opponentAdapter);
	}

	public void clearOpponents() {
		opponentList.clear();
	}

	public void updateOpponent(Opponent opponent) {
		Preconditions.notNull(opponent);

		int size = opponentList.size();
		for (int i = 0; i < size; i++) {
			Opponent newOpponent = opponentList.get(i);
			if (newOpponent.getName().equals(opponent.getName())) {
				opponentList.remove(i);
				opponentList.add(newOpponent);
			}
		}
	}

	public List<Opponent> getOpponentList() {
		return opponentList;
	}

	public void setOpponentList(List<Opponent> opponentList) {
		this.opponentList = opponentList;
	}

	private class OpponentAdapter extends RecyclerView.Adapter<OpponentListFragment.OpponentHolder> {
		private List<Opponent> opponentList;

		public OpponentAdapter(List<Opponent> opponentList) {
			this.opponentList = opponentList;
		}

		@NonNull
		@Override
		public OpponentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
			LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
			View view = layoutInflater.inflate(R.layout.list_item_opponent, viewGroup, false);
			return new OpponentHolder(view);
		}

		@Override
		public void onBindViewHolder(@NonNull OpponentHolder opponentHolder, int i) {
			Opponent opponent = opponentList.get(i);
			opponentHolder.bind(opponent);
		}

		@Override
		public int getItemCount() {
			if (opponentList == null) {
				return 0;
			}
			return opponentList.size();
		}
	}

	private class OpponentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		private Opponent opponent;

		private ImageView thumbnail;
		private TextView name;

		public OpponentHolder(@NonNull View itemView) {
			super(itemView);

			name = itemView.findViewById(R.id.list_item_opponent_name);
			thumbnail = itemView.findViewById(R.id.list_item_opponent_thumbnail);
			itemView.setOnClickListener(this);
		}

		public void bind(Opponent opponent) {
			this.opponent = opponent;
			name.setText(opponent.getName());
		}

		@Override
		public void onClick(View v) {
			// TODO Select opponent
			Toast.makeText(getActivity(), "HELLO", Toast.LENGTH_SHORT).show();
		}
	}
}
