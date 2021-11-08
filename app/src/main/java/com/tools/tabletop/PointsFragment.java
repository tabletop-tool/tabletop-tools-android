package com.tools.tabletop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

    private CardPoints deleted;

    private RecyclerView rv;
    private ArrayList<CardPoints> data;
    private ArrayList<CardPoints> display;

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
            if ( direction == ItemTouchHelper.LEFT) {
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
                        }).show();
            }
        }
    });

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_points, container, false);

        // Code Reference: https://youtu.be/xYmH61Ilglc
        this.rv = v.findViewById(R.id.pts_list);

        if (this.data == null) {
            this.data = new ArrayList<>();
            Random r = new Random();

            for (int i = 0; i < 15; i++)
                this.data.add(new CardPoints("Player " + (i + 1), 1 + r.nextInt(99)));

            this.data.add(new CardPoints("Unique", 1000));

            this.display = new ArrayList<>(this.data);
        }

        this.rv.setAdapter(new CardAdapter(this.getContext(), this.display));
        this.itTh.attachToRecyclerView(this.rv);

        setHasOptionsMenu(true);

        return v;
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
                    for (CardPoints i: data) {
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
}
