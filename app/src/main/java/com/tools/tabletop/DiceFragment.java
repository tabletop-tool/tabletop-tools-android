package com.tools.tabletop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Random;

public class DiceFragment extends Fragment {
    private Button dice;
    private Integer result = null;
    private static final Random rand = new Random();
    private int[] range = {1, 6};
    private ArrayList<Integer> custom = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dice, container, false);
        this.dice = v.findViewById(R.id.dice);

        this.dice.setOnClickListener(v1 -> {
            if (v1.getId() != R.id.dice) return;

            this.changeDice();
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.result != null) this.changeDice(false);
    }

    private void changeDice(boolean change) {
        if (change)
            this.result = rand.nextInt(this.range[1] - this.range[0] + 1) + this.range[0];

        dice.setText(String.valueOf(this.result));
        if (dice.getTextSize() != 80) dice.setTextSize(80);
    }

    private void changeDice() {
        this.changeDice(true);
    }
}
