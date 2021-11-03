package com.tools.tabletop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// some code from: https://www.geeksforgeeks.org/cardview-using-recyclerview-in-android-with-example/

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.Viewholder>{

    private Context ctx;
    private ArrayList<CardPoints> ptsAL;

    public CardAdapter(Context ctx, ArrayList<CardPoints> ini) {
        this.ctx = ctx;
        this.ptsAL = ini;
    }

    public CardAdapter(Context ctx) {
        this.ctx = ctx;
        this.ptsAL = new ArrayList<>();
    }

    @NonNull
    @Override
    public CardAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(
                    parent.getContext()
            ).inflate(R.layout.card_pts, parent, false);
            return new Viewholder(view) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapter.Viewholder holder, int position) {
        CardPoints tmp = this.ptsAL.get(position);
        holder.pts.setText(String.valueOf(tmp.getScore()));
        holder.ply.setText(tmp.getPlayer());
    }

    @Override
    public int getItemCount() {
        return this.ptsAL.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        private TextView pts, ply;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            this.pts = itemView.findViewById(R.id.card_pts_info);
            this.ply = itemView.findViewById(R.id.card_pts_player);
        }
    }
}

class CardPoints {
    private String player;
    private int score;

    public CardPoints(String s, int p) {
        this.player = s;
        this.score = p;
    }

    public String getPlayer() {
        return this.player;
    }

    public int getScore() {
        return this.score;
    }

    public boolean addScore(int i) {
        this.score += i;
        return true;
    }

    public boolean minusScore(int i) {
        this.score -= i;
        return true;
    }
}
