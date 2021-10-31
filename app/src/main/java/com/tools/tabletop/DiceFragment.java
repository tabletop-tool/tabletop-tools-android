package com.tools.tabletop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Random;

public class DiceFragment extends Fragment {
    private Button dice;
    private Integer result = null;
    private static final Random rand = new Random();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dice, container, false);
        this.dice = v.findViewById(R.id.dice);

        this.dice.setOnClickListener(v1 -> {
            if (v1.getId() != R.id.dice) return;

            int temp = rand.nextInt(6);
            this.result = temp + 1;
            this.changeDice();
            Toast.makeText(v1.getContext(), "\uD83C\uDFB2", Toast.LENGTH_SHORT).show();
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.result != null) this.changeDice();
    }

    private void changeDice() {
        dice.setText(String.valueOf(this.result));
        if (dice.getTextSize() != 80) dice.setTextSize(80);
    }
}
