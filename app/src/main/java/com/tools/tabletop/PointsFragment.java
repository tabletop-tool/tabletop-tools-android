package com.tools.tabletop;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerViewAccessibilityDelegate;

import java.util.ArrayList;

public class PointsFragment extends Fragment {

    private RecyclerView rv;
    private ArrayList<CardPoints> data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_points, container, false);

        this.rv = v.findViewById(R.id.pts_list);
        this.data = new ArrayList<>();

        this.data.add(new CardPoints("Player 1", 10));
        this.data.add(new CardPoints("Player 2", 0));

        this.rv.setAdapter(new CardAdapter(this.getContext(), data));

        return v;
    }
}
