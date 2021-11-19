package com.tools.tabletop;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

public class HistoryFragment<T> extends Fragment {
    private final int mode;
    private View v;
    private RecyclerView rv;
    private TextView tv;

    private final ArrayList<T> history;
    private T deleted;

    public HistoryFragment(ArrayList<T> history, int type) {
        super();
        this.history = history;

        this.mode = type;
    }

    private final ItemTouchHelper itTh = new ItemTouchHelper(new ItemTouchHelper.Callback() {
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
            Objects.requireNonNull(rv.getAdapter()).notifyItemRemoved(pos);

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

        if (mode == 0) this.rv.setAdapter(
                new CoinAdapter(this.getContext(), (ArrayList<Boolean>) this.history)
        );
        else if (mode == 1) this.rv.setAdapter(
                new DiceAdapter((ArrayList<int[]>) this.history)
        );
        else if (mode == 2) this.rv.setAdapter(
                new SpinAdapter((ArrayList<String[]>) this.history)
        );
        // should never reach the line below
        else tv.setText(getString(R.string.err));

        this.itTh.attachToRecyclerView(this.rv);
        setHasOptionsMenu(true);

        return this.v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.history_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("You sure you want to clear all history record(s)?");
        builder.setCancelable(true);

        builder.setNeutralButton("No", null);

        builder.setPositiveButton("Yes", (d, v) -> {
            history.clear();
            rv.getAdapter().notifyDataSetChanged();
            tv.setText(getString(R.string.aww_there_is_nothing_here));
        });

        builder.show();
        return true;
    }
}

class CoinAdapter extends RecyclerView.Adapter<CoinAdapter.Viewholder> {

    private final ArrayList<Boolean> coinHis;
    private final Context ctx;

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
            holder.bk.setCardBackgroundColor(Color.parseColor("#0984e3"));
            holder.result.setText(ctx.getString(R.string.h));
        } else {
            holder.bk.setCardBackgroundColor(Color.parseColor("#00cec9"));
            holder.result.setText(ctx.getString(R.string.t));
        }
    }

    @Override
    public int getItemCount() { return this.coinHis.size(); }

    protected static class Viewholder extends RecyclerView.ViewHolder {
        private final TextView result;
        private final CardView bk;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            this.result = itemView.findViewById(R.id.coin_result);
            this.bk = itemView.findViewById(R.id.coin_card_view);
        }
    }
}

class DiceAdapter extends RecyclerView.Adapter<DiceAdapter.Viewholder> {

    private final ArrayList<int[]> diceHis;

    public DiceAdapter(ArrayList<int[]> ini) {
        this.diceHis = ini;
    }

    @NonNull
    @Override
    public DiceAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()
        ).inflate(R.layout.card_dice, parent, false);

        return new Viewholder(view){};
    }

    @Override
    public void onBindViewHolder(@NonNull DiceAdapter.Viewholder holder, int position) {
        int[] data = this.diceHis.get(position);

        holder.result.setText(String.valueOf(data[0]));
        holder.range.setText(data[1] + " - " + data[2]);
    }

    @Override
    public int getItemCount() { return this.diceHis.size(); }

    protected static class Viewholder extends RecyclerView.ViewHolder {
        private final TextView result, range;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            this.result = itemView.findViewById(R.id.dice_result);
            this.range = itemView.findViewById(R.id.dice_range);
        }
    }
}

class SpinAdapter extends RecyclerView.Adapter<SpinAdapter.Viewholder> {

    private final ArrayList<String[]> spinHis;

    public SpinAdapter(ArrayList<String[]> ini) {
        this.spinHis = ini;
    }

    @NonNull
    @Override
    public SpinAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()
        ).inflate(R.layout.card_spin, parent, false);

        return new Viewholder(view){};
    }

    @Override
    public void onBindViewHolder(@NonNull SpinAdapter.Viewholder holder, int position) {
        String[] data = this.spinHis.get(position);

        holder.result.setText(data[0] + "°");
        holder.label.setText(data[2]);
        holder.bk.setCardBackgroundColor(Integer.parseInt(data[1]));
        holder.point.setRotation(Float.parseFloat(data[0]));
    }

    @Override
    public int getItemCount() { return this.spinHis.size(); }

    protected static class Viewholder extends RecyclerView.ViewHolder {
        private final TextView result, label;
        private final CardView bk;
        private final ImageView point;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            this.result = itemView.findViewById(R.id.spin_result);
            this.label = itemView.findViewById(R.id.spin_label);
            this.bk = itemView.findViewById(R.id.spin_card_view);
            this.point = itemView.findViewById(R.id.spin_point);
        }
    }
}
