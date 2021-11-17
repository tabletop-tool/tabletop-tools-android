package com.tools.tabletop;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

public class PointsFragment extends Fragment {

    private PointsData deleted;

    private View v;
    private TextView msg;
    private RecyclerView rv;
    private ArrayList<PointsData> data;
    private ArrayList<PointsData> display;

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

            int start = viewHolder.getAdapterPosition();
            int end = target.getAdapterPosition();

            Collections.swap(display, start, end);
            Collections.swap(data, start, end);

            rv.getAdapter().notifyItemMoved(start, end);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // code reference: https://youtu.be/Aup-aPj24eU
            int pos = viewHolder.getAdapterPosition();

            if (direction == ItemTouchHelper.LEFT) {
                deleted = data.get(pos);
                display.remove(pos);
                data.remove(pos);
                rv.getAdapter().notifyItemRemoved(pos);

                Snackbar.make(
                        rv, String.format("Deleted: %s", deleted.getPlayer()),
                        Snackbar.LENGTH_LONG).setAction("Undo", view -> {
                            display.add(pos, deleted);
                            data.add(pos, deleted);
                            rv.getAdapter().notifyItemInserted(pos);
                            msg.setText("");
                        }).show();
                if (display.size() == 0) msg.setText(R.string.kind_of_lonely_in_here);

            } else { // right
                // code reference: https://youtu.be/eslYJArppnQ

                PointsData target = display.get(pos);
                EditText et = new EditText(v.getContext());
                et.setText(target.getPlayer());

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Change Name");
                builder.setCancelable(true);
                builder.setView(et);

                builder.setNeutralButton("Cancel", (d, v) -> {
                    rv.getAdapter().notifyItemChanged(pos);
                });

                builder.setOnCancelListener(i -> {
                    rv.getAdapter().notifyItemChanged(pos);
                });

                builder.setPositiveButton("Update", (d, v) -> {
                   target.setPlayer(et.getText().toString());
                   data.get(pos).setPlayer(et.getText().toString());
                   rv.getAdapter().notifyItemChanged(pos);
                });

                builder.show();
            }
        }
    });

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        this.v = inflater.inflate(R.layout.fragment_points, container, false);

        // Code Reference: https://youtu.be/xYmH61Ilglc
        this.rv = this.v.findViewById(R.id.pts_list);
        this.msg = this.v.findViewById(R.id.empty_msg_2);

        if (this.data == null) {
            this.data = new ArrayList<>();
            Random r = new Random();

            for (int i = 0; i < 3; i++)
                this.data.add(new PointsData("Player " + (i + 1), 1 + r.nextInt(99)));

            this.data.add(new PointsData("Unique", 1000));

            this.display = new ArrayList<>(this.data);
        }

        this.rv.setAdapter(new PointsAdapter(this.getContext(), this.display));
        this.itTh.attachToRecyclerView(this.rv);

        if (data.size() > 0) {
            this.msg.setText("");
        }

        setHasOptionsMenu(true);

        return this.v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.points_menu, menu);

        // code reference: https://youtu.be/V1aLOkwV84o

        MenuItem search = menu.findItem(R.id.search);
        SearchView sv = (SearchView) search.getActionView();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    display.clear();
                    display.addAll(data);
                } else {
                    display.clear();
                    newText = newText.toLowerCase(Locale.getDefault());

                    for (PointsData i: data) {
                        if(i.getPlayer().toLowerCase(Locale.getDefault()).contains(newText)) {
                            display.add(i);
                        }
                    }

                }

                rv.getAdapter().notifyDataSetChanged();

                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add_plyer:
                EditText et = new EditText(v.getContext());
                et.setText("New Challenger");

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("New Points Tracker");
                builder.setCancelable(true);
                builder.setView(et);

                builder.setNeutralButton("Cancel", null);

                builder.setPositiveButton("Create", (d, v) -> {
                    PointsData created = new PointsData(et.getText().toString(),0);
                    data.add(created);
                    display.add(created);
                    rv.getAdapter().notifyItemChanged(display.size() - 1);
                });
                this.msg.setText("");

                builder.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

class PointsAdapter extends RecyclerView.Adapter<PointsAdapter.Viewholder>{

    private Context ctx;
    private ArrayList<PointsData> ptsAL;
    public Integer ptsAdd = 10;
    public Integer ptsMin = 10;

    public PointsAdapter(Context ctx, ArrayList<PointsData> ini) {
        this.ctx = ctx;
        this.ptsAL = ini;
    }

    @NonNull
    @Override
    public PointsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()
        ).inflate(R.layout.card_pts, parent, false);
        return new Viewholder(view) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull PointsAdapter.Viewholder holder, int position) {
        PointsData tmp = this.ptsAL.get(position);
        holder.pts.setText(String.valueOf(tmp.getScore()));
        holder.ply.setText(tmp.getPlayer());

        holder.pBtn.setOnClickListener(v -> {
            PointsData temp = ptsAL.get(position);
            int val = temp.getScore() + ptsAdd;
            holder.pts.setText(String.valueOf(val));
            temp.setScore(val);
        });

        holder.mBtn.setOnClickListener(v -> {
            PointsData temp = ptsAL.get(position);
            int val = temp.getScore() - ptsMin;
            holder.pts.setText(String.valueOf(val));
            temp.setScore(val);
        });
    }

    @Override
    public int getItemCount() {
        return this.ptsAL.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        private TextView pts, ply;
        private ImageButton pBtn, mBtn;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            this.pts = itemView.findViewById(R.id.card_pts_info);
            this.ply = itemView.findViewById(R.id.card_pts_player);
            this.pBtn = itemView.findViewById(R.id.plus_btn);
            this.mBtn = itemView.findViewById(R.id.minus_btn);
        }
    }
}

class PointsData {
    private String player;
    private int score;

    public PointsData(String s, int p) {
        this.player = s;
        this.score = p;
    }

    public String getPlayer() {
        return this.player;
    }

    public int getScore() {
        return this.score;
    }

    public boolean setScore(int i) {
        this.score = i;
        return true;
    }

    public void setPlayer(String player) {
        this.player = player;
    }
}

