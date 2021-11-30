package com.tools.tabletop;

import android.annotation.SuppressLint;
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

/**
 * HistoryFragment that implements Fragment class that is associated with fragment_history
 * @param <T> represent the data type of the history list
 */
public class HistoryFragment<T> extends Fragment {
    private final int mode; // mode determining what history fragment it is
    private RecyclerView rv; // recycler view
    private TextView tv; // text view to notify user of empty list

    private final ArrayList<T> history; // ArrayList containing history data
    private T deleted; // reference to the most recently deleted item from history

    /**
     * Constructor for HistoryFragment class
     * @param history ArrayList reference of the history data
     * @param type history fragment type | 0 - coin , 1 - dice, 2 - spinner
     */
    public HistoryFragment(ArrayList<T> history, int type) {
        super();
        this.history = history;

        this.mode = type;
    }

    /**
     * item touch helper for the recycler view
     */
    private final ItemTouchHelper itTh = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        /**
         * method that marks the allowed movement for the recycler view
         */
        @Override
        public int getMovementFlags(
                @NonNull RecyclerView recyclerView,
                @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(
                    ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
            );
        }

        /**
         * method returns whether or not items within the recycler view can be moved around
         */
        @Override
        public boolean onMove(
                @NonNull RecyclerView recyclerView,
                @NonNull RecyclerView.ViewHolder viewHolder,
                @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        /**
         * method responsible for the item swipe action, in this case just left and right for
         * removal
         */
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

    /**
     * Method called to initialize view graphics
     */
    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_history, container, false);
        this.rv = v.findViewById(R.id.history_list);

        if (this.history.size() > 0) {
            this.tv = v.findViewById(R.id.empty_msg);
            tv.setText("");
        }

        // set adapter base on the mode variable
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

        return v;
    }

    /**
     * Method called to initialize menu view graphics
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.history_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * method to be called when the clear button is pressed
     */
    @SuppressLint("NotifyDataSetChanged")
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("You sure you want to clear all history record(s)?");
        builder.setCancelable(true);

        builder.setNeutralButton("No", null);

        builder.setPositiveButton("Yes", (d, v) -> {
            history.clear();
            Objects.requireNonNull(rv.getAdapter()).notifyDataSetChanged();
            tv.setText(getString(R.string.aww_there_is_nothing_here));
        });

        builder.show();
        return true;
    }
}

/**
 * CoinAdapter class inherits from RecyclerView.Adapter that deals with card_coin view within
 * recycler view.
 */
class CoinAdapter extends RecyclerView.Adapter<CoinAdapter.Viewholder> {

    private final ArrayList<Boolean> coinHis; // coin history ArrayList reference
    private final Context ctx; // context

    /**
     * Constructor for CoinAdapter class
     *
     * @param ctx Context
     * @param ini ArrayList of boolean
     */
    public CoinAdapter(Context ctx, ArrayList<Boolean> ini) {
        this.coinHis = ini;
        this.ctx = ctx;
    }

    /**
     * Method responsible for generating the view of the item(s) within recycler view
     */
    @NonNull
    @Override
    public CoinAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()
        ).inflate(R.layout.card_coin, parent, false);

        return new Viewholder(view){};
    }

    /**
     * Method that modifies the item view base on history data
     */
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

    /**
     * Method that returns total item count
     * @return total item count within the "recycler list"
     */
    @Override
    public int getItemCount() { return this.coinHis.size(); }

    /**
     * Coin Viewholder class inherit from RecyclerView.ViewHolder
     */
    protected static class Viewholder extends RecyclerView.ViewHolder {
        private final TextView result; // text view from card_coin
        private final CardView bk; // card view from card_coin

        /**
         * Constructor of Viewholder (coin) class
         * @param itemView View
         */
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            this.result = itemView.findViewById(R.id.coin_result);
            this.bk = itemView.findViewById(R.id.coin_card_view);
        }
    }
}

/**
 * DiceAdapter class inherits from RecyclerView.Adapter that deals with card_dice view within
 * recycler view.
 */
class DiceAdapter extends RecyclerView.Adapter<DiceAdapter.Viewholder> {

    private final ArrayList<int[]> diceHis; // dice history ArrayList reference

    /**
     * DiceAdapter class constructor
     * @param ini dice history reference
     */
    public DiceAdapter(ArrayList<int[]> ini) {
        this.diceHis = ini;
    }

    /**
     * Method responsible for generating the view of the item(s) within recycler view
     */
    @NonNull
    @Override
    public DiceAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()
        ).inflate(R.layout.card_dice, parent, false);

        return new Viewholder(view){};
    }

    /**
     * Method that modifies the item view base on history data
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DiceAdapter.Viewholder holder, int position) {
        int[] data = this.diceHis.get(position);

        holder.result.setText(String.valueOf(data[0]));
        holder.range.setText(data[1] + " - " + data[2]);
    }

    /**
     * Method that returns total item count
     * @return total item count within the "recycler list"
     */
    @Override
    public int getItemCount() { return this.diceHis.size(); }

    /**
     * Viewholder (dice) that inherits from RecyclerView.ViewHolder
     */
    protected static class Viewholder extends RecyclerView.ViewHolder {
        private final TextView result, range; // text views from card_dice

        /**
         * Constructor of Viewholder (dice) class
         * @param itemView View
         */
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            this.result = itemView.findViewById(R.id.dice_result);
            this.range = itemView.findViewById(R.id.dice_range);
        }
    }
}

/**
 * SpinAdapter class inherits from RecyclerView.Adapter that deals with card_spin view within
 * recycler view.
 */
class SpinAdapter extends RecyclerView.Adapter<SpinAdapter.Viewholder> {

    private final ArrayList<String[]> spinHis; // array list reference of the spinner history

    /**
     * SpinAdapter class constructor
     * @param ini spinner history reference
     */
    public SpinAdapter(ArrayList<String[]> ini) {
        this.spinHis = ini;
    }

    /**
     * Method responsible for generating the view of the item(s) within recycler view
     */
    @NonNull
    @Override
    public SpinAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()
        ).inflate(R.layout.card_spin, parent, false);

        return new Viewholder(view){};
    }

    /**
     * Method that modifies the item view base on history data
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SpinAdapter.Viewholder holder, int position) {
        String[] data = this.spinHis.get(position);

        holder.result.setText(data[0] + "°");
        holder.label.setText(data[2]);
        holder.bk.setCardBackgroundColor(Integer.parseInt(data[1]));
        holder.point.setRotation(Float.parseFloat(data[0]));
    }

    /**
     * Method that returns total item count
     * @return total item count within the "recycler list"
     */
    @Override
    public int getItemCount() { return this.spinHis.size(); }

    /**
     * Viewholder (spinner) that inherits from RecyclerView.ViewHolder
     */
    protected static class Viewholder extends RecyclerView.ViewHolder {
        private final TextView result, label; // text views from card_spin
        private final CardView bk; // card view from card_spin
        private final ImageView point; // image view pointer from card_spin

        /**
         * Constructor of Viewholder (spinner) class
         * @param itemView View
         */
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            this.result = itemView.findViewById(R.id.spin_result);
            this.label = itemView.findViewById(R.id.spin_label);
            this.bk = itemView.findViewById(R.id.spin_card_view);
            this.point = itemView.findViewById(R.id.spin_point);
        }
    }
}
