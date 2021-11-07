package com.tools.tabletop;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Random;

public class CoinFragment extends Fragment {
    private int result = -1;
    private static final Random rand = new Random();
    private Button flipBtn;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_coin, container, false);

        this.flipBtn = v.findViewById(R.id.coin_btn);
        this.flipBtn.setOnClickListener(v1 -> {
            if (v1.getId() != R.id.coin_btn) return;

            int temp = rand.nextInt(2);
            if (this.result != temp) {
                this.result = temp;
                this.changeBtn();
            }
        });

        if (this.result != -1) this.changeBtn();

        return v;
    }

    private void changeBtn() {
        if (this.result == 1) {
            flipBtn.setBackgroundColor(Color.parseColor("#0984e3"));
            flipBtn.setText(getString(R.string.h));
        } else {
            flipBtn.setBackgroundColor(Color.parseColor("#00cec9"));
            flipBtn.setText(getString(R.string.t));
        }
    }
}
