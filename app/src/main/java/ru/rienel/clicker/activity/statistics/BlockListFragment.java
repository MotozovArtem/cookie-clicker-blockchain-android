package ru.rienel.clicker.activity.statistics;

import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import ru.rienel.clicker.R;
import ru.rienel.clicker.db.domain.Block;
import ru.rienel.clicker.db.domain.dao.Repository;
import ru.rienel.clicker.db.domain.dao.impl.BlockDaoImpl;

public class BlockListFragment extends Fragment implements StatisticsContract.View{
	private RecyclerView blockRecyclerView;
	private BlockAdapter blockAdapter;
	private Repository<Block> blockRepository;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_block_list, container, false);
		blockRecyclerView = view.findViewById(R.id.block_recycler_view);
		blockRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		if (blockRepository == null) {
			blockRepository = new BlockDaoImpl(getContext());
		}
		updateUi();
		return view;
	}

	private void updateUi() {
		List<Block> blockList = blockRepository.findAll();
		blockAdapter = new BlockAdapter(blockList);
		blockRecyclerView.setAdapter(blockAdapter);
	}

	@Override
	public void setPresenter(StatisticsContract.Presenter presenter) {

	}

	private class BlockAdapter extends RecyclerView.Adapter<BlockListFragment.BlockHolder> {
		private List<Block> blockList;

		public BlockAdapter(List<Block> blockList) {
			this.blockList = blockList;
		}

		@NonNull
		@Override
		public BlockHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
			LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
			View view = layoutInflater.inflate(R.layout.list_item_block, viewGroup, false);
			return new BlockHolder(view);
		}

		@Override
		public void onBindViewHolder(@NonNull BlockHolder blockHolder, int i) {
			Block block = blockList.get(i);
			blockHolder.bind(block, i);
		}

		@Override
		public int getItemCount() {
			return blockList.size();
		}
	}

	private class BlockHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		private Block block;

		private TextView blockNumber;
		private TextView blockMessage;
		private TextView dateOfCreate;

		@Override
		public void onClick(View v) {
			Toast.makeText(getActivity(), "KONO DIO DA", Toast.LENGTH_SHORT).show();
		}

		public BlockHolder(@NonNull View itemView) {
			super(itemView);
			blockNumber = itemView.findViewById(R.id.block_number);
			blockMessage = itemView.findViewById(R.id.block_message);
			dateOfCreate = itemView.findViewById(R.id.block_date_of_create);
			itemView.setOnClickListener(this);
		}

		public void bind(Block block, int i) {
			this.block = block;
			if (i == 0) {
					blockNumber.setText(R.string.genesis_block);
			} else {
				blockNumber.setText(
						String.format(Locale.ENGLISH, "#%d", i)
				);
			}

			blockMessage.setText(this.block.getMessage());

			String formattedDateTime = getFormattedDateFromBlock(block);
			dateOfCreate.setText(formattedDateTime);

		}

		private String getFormattedDateFromBlock(Block block) {
			DateFormat format = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.UK);
			format.setCalendar(GregorianCalendar.getInstance());
			return format.format(block.getCreationTime());
		}
	}
}
