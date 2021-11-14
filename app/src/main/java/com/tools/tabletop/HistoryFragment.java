package com.tools.tabletop;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class HistoryFragment<T> extends Fragment {
    private int mode = -1;
    private View v;
    private RecyclerView rv;
    private TextView tv;

    private ArrayList<T> history;
    private T deleted;

    public HistoryFragment(ArrayList<T> history) {
        super();
        this.history = history;

        this.mode = 0;
    }

    private ItemTouchHelper itTh = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(
                @NonNull RecyclerView recyclerView,
                @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(
                    ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
            );
        }

        @Override
        public boolean onMove(
                @NonNull RecyclerView recyclerView,
                @NonNull RecyclerView.ViewHolder viewHolder,
                @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // code reference: https://youtu.be/Aup-aPj24eU
            int pos = viewHolder.getAdapterPosition();

            deleted = history.get(pos);
            history.remove(pos);
            rv.getAdapter().notifyItemRemoved(pos);

            Snackbar.make(rv, "Record Deleted",
                    Snackbar.LENGTH_LONG).setAction("Undo", view -> {
                history.add(pos, deleted);
                rv.getAdapter().notifyItemInserted(pos);
                tv.setText("");
            }).show();
            if (history.size() == 0) tv.setText(getString(R.string.aww_there_is_nothing_here));
        }
    });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        this.v = inflater.inflate(R.layout.fragment_history, container, false);
        this.rv = this.v.findViewById(R.id.history_list);

        if (this.history.size() > 0) {
            this.tv = this.v.findViewById(R.id.empty_msg);
            tv.setText("");
        }

        this.rv.setAdapter(new CoinAdapter(this.getContext(), (ArrayList<Boolean>) this.history));

        this.itTh.attachToRecyclerView(this.rv);

        return this.v;
    }
}

class CoinAdapter extends RecyclerView.Adapter<CoinAdapter.Viewholder> {

    private ArrayList<Boolean> coinHis;
    private Context ctx;

    public CoinAdapter(Context ctx, ArrayList<Boolean> ini) {
        this.coinHis = ini;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public CoinAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()
        ).inflate(R.layout.card_coin, parent, false);

        return new Viewholder(view){};
    }

    @Override
    public void onBindViewHolder(@NonNull CoinAdapter.Viewholder holder, int position) {
        Boolean data = this.coinHis.get(position);

        if (data) {
            holder.bk.setBackgroundColor(Color.parseColor("#0984e3"));
            holder.result.setText(ctx.getString(R.string.h));
        } else {
            holder.bk.setBackgroundColor(Color.parseColor("#00cec9"));
            holder.result.setText(ctx.getString(R.string.t));
        }
    }

    @Override
    public int getItemCount() { return this.coinHis.size(); }

    protected static class Viewholder extends RecyclerView.ViewHolder {
        private TextView result;
        private CardView bk;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            this.result = itemView.findViewById(R.id.coin_result);
            this.bk = itemView.findViewById(R.id.coin_card_view);
        }
    }
}
