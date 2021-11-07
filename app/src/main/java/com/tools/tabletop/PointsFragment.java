package com.tools.tabletop;

import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class PointsFragment extends Fragment {

    private RecyclerView rv;
    private ArrayList<CardPoints> data;
    private ArrayList<CardPoints> display;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_points, container, false);

        // Some code from: https://youtu.be/xYmH61Ilglc
        this.rv = v.findViewById(R.id.pts_list);

        if (this.data == null) {
            this.data = new ArrayList<>();

            this.data.add(new CardPoints("Player 1", 10));
            this.data.add(new CardPoints("Player 2", 0));
            this.data.add(new CardPoints("Unique", 1000));

            this.display = new ArrayList<>(this.data);
        }

        this.rv.setAdapter(new CardAdapter(this.getContext(), this.display));
        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.points_menu, menu);

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
